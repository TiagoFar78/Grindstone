# Plan: refactor the Playground (engines/web)

Scope: **only the `engines/web` module**. Do not change `grindstone-core` or other modules.
Base package: `io.github.tiagofar78.grindstone.web`.

## Goal

`Playground` currently keys everything by an `int` client id (`idToPlayer`, `idToName`, `idToEmoji`, `clientGame`, `awaitingName`, `GameEntry`). Replace this with real objects:

- No numeric player id anywhere. A player is a `WebPlayer`. On the wire, players are referred to by their **position (1-based) in the online list** shown in the lobby. The nickname is the unique identity (unique, case-insensitive).
- `Playground` is the lobby only: no sockets, no `int` ids, no JSON building.
- Rejoin is part of this refactor: a player that disconnects during a rejoinable game can reconnect with the same nickname and rejoin it.
- Every client message becomes a **command object** (a class with its own logic and its own field validation). Game commands are also `PlaygroundCommand`s, reached through a single `GameCommand` that forwards to the right game. The Playground never knows a game's protocol.

Before coding, read the existing `Playground`, `WebServer`, `WebPlayer`, `WebGame`, `WebTicTacToe`, `WebTTTBridge` and the frontend under `src/main/resources/web` (to see which messages it sends and receives).

## Target structure

```
web/
├── WebPlayer.java
├── PlayerState.java
├── WebGame.java
├── games/tictactoe/
│   ├── WebTicTacToe.java
│   ├── PlaceMarkCommand.java
│   └── WebTTTBridge.java
├── playground/
│   ├── Playground.java
│   ├── GameSession.java
│   ├── AvailableGame.java
│   ├── PlayerRegistry.java
│   ├── LobbyView.java
│   ├── commands/
│   │   ├── PlaygroundCommand.java
│   │   ├── PlaygroundCommandParser.java
│   │   ├── EditProfileCommand.java
│   │   ├── StartGameCommand.java
│   │   ├── LeaveCommand.java
│   │   ├── RejoinCommand.java
│   │   └── GameCommand.java
│   └── network/WebServer.java
└── services/WebServices.java
```

## Files and responsibilities

### `WebPlayer` (fat player, acts as the web client)
- Implements `Messager`.
- Fields: `WsContext` (given at construction), `name` (null until chosen), `emoji`, `state` (`PlayerState`).
- `sendMessage(String, MessagesChannel)`: if the message starts with `{` it is already JSON and is sent as is; otherwise it is wrapped as `{"type":"chat","text":...}` (serialize with `WebServer.toJson`). Sending to the socket is `synchronized` on the context. This is the **only** way to send to a player; do not add other send methods.

### `PlayerState`
One file, sealed interface with nested records, pure data:
```java
public sealed interface PlayerState {
    record AwaitingName() implements PlayerState {}
    record InLobby() implements PlayerState {}
    record InGame(GameSession session) implements PlayerState {}
}
```

### `WebGame`
```java
public interface WebGame {
    String getName();
    void start();
    Optional<PlaygroundCommand> parseCommand(JsonNode message);
    void disconnect(WebPlayer player);
}
```
`parseCommand` switches on `type` and builds the game's own command class. It returns `Optional.empty()` when the type isn't one of the game's commands. Checking the fields is the command's job.

### `WebTicTacToe` and `PlaceMarkCommand`
- `WebTicTacToe extends TicTacToe implements WebGame`. `parseCommand`: type `play` -> `new PlaceMarkCommand(this, message)`; anything else -> empty.
- `PlaceMarkCommand implements PlaygroundCommand`. `allowedIn` is true only for `InGame`. `execute` checks that `row` and `col` exist and are ints (same checks `process` has today, ignoring the message otherwise) and calls `play(getPlayer(player), row, col)` on the game. The `Playground` argument is not used.
- `getPlayer` lookup must go from `WebPlayer` to its `TTTPlayer`. Inspect core's `getPlayer` first; if it doesn't fit, resolve it inside `WebTicTacToe` (e.g. through the `Messager`) without touching core.
- `disconnect(WebPlayer)` calls `removePlayerFromGame(...)` as today. If the game implements `Rejoinable`, it must not remove the player.
- Should implement core's `Rejoinable` (if the core `TicTacToe` supports it), so rejoin can be tested end to end. See the Rejoin section.

### `GameSession`
Created when a game starts. Holds: `id` (int, from a counter in the Playground, used in the `running` list), the `WebGame`, `teams` (`List<List<WebPlayer>>`, as given at start, used for the `running` list) and `players` (the `WebPlayer`s still in the game, used to know when a session is empty; on rejoin the old `WebPlayer` is replaced by the new one). Game name comes from `getName()`.

### `AvailableGame`
Display name + factory `List<List<WebPlayer>> -> WebGame`. Keep the existing TicTacToe registration, now building `TTTPlayer`s from the `WebPlayer`s.

### Commands (`playground/commands`)
```java
public interface PlaygroundCommand {
    boolean allowedIn(PlayerState state);
    void execute(Playground playground, WebPlayer player);
}
```
Each command holds its own validation and logic. The Playground exposes only the primitives they need (registry access, create/remove session, refresh lobbies, send error); keep them narrow.

- `EditProfileCommand(name, emoji)`: allowed in `AwaitingName` and `InLobby`. Validates the name (1..16 characters, unique case-insensitive, ignoring the player's own current name). The emoji is used if it is in the list, otherwise a random one. In `AwaitingName` it registers the player and moves the state to `InLobby`. If the registry has a **disconnected** entry with that name, the new `WebPlayer` takes over that entry (keeping its core `Player`, setting `isConnected` back to true) instead of creating a new one; this is how a player comes back (see Rejoin). In `InLobby` it just updates the profile. Refreshes lobbies. On error, re-sends the nickname prompt with the error.
- `StartGameCommand(game, List<List<Integer>> indexes)`: allowed in `InLobby`. Indexes are 1-based positions in the online list; resolve them with the registry's lobby order. Validations as today: known game, valid index, nobody already in a game, no duplicates, no empty team, at least one team. Then build the `WebPlayer` teams, call the `AvailableGame` factory (catch `RuntimeException` -> "wrong number of teams or players"), create the `GameSession`, set each player's state to `InGame(session)`, refresh lobbies, call `game.start()`.
- `LeaveCommand`: allowed in `InGame`. Calls `game.disconnect(player)`, sets the state to `InLobby`, removes the player from the session's `players`, removes the session if no connected player is left (dropping its disconnected entries), refreshes lobbies. Leaving is final: the player cannot rejoin that game afterwards.
- `GameCommand(JsonNode message)`: allowed in `InGame`. Its behavior is forwarding to the right game: `execute` takes the session from the player's state, calls `session.game().parseCommand(message)`, and executes the resulting command (`command.execute(playground, player)`). If the game returns empty, it replies "Unknown message type".
- `RejoinCommand` (no fields): allowed in `InLobby`. Rejoining a game is only possible if the player's core `Player` still belongs to an **ongoing** game that implements `Rejoinable`; otherwise reply with an error ("You have no game to rejoin."). Otherwise: switch the messager manually with `setPlayerEngine` so the game talks to the new `WebPlayer`, replace the old `WebPlayer` with the new one in the session's `players`, set the state to `InGame(session)`, call `rejoin(corePlayer)` on the game (the only `Rejoinable` method the engine needs), and refresh lobbies.

### `PlaygroundCommandParser`
`PlaygroundCommand parse(JsonNode)`: switch on `type` -> `profile` -> `EditProfileCommand`, `start` -> `StartGameCommand`, `leave` -> `LeaveCommand`, `rejoin` -> `RejoinCommand`; **any other type -> `GameCommand(message)`**. It only extracts raw values from the message (missing or wrong-typed values become empty/-1); the commands validate them. This is the only place that knows the playground's message types.

### `PlayerRegistry`
- A plain `ArrayList` of entries. Each entry holds the current `WebPlayer`, the core `Player` and `isConnected`. The core `Player` is what survives a disconnect and is needed for rejoin. No id and no map.
- Operations: `isNameTaken(name, except)`, `register(player)`, `remove(player)`, `lobbyOrder()` (connected named players, free players first; **the single definition of the order**), `byIndex(int)` (1-based, over `lobbyOrder()`), `findDisconnected(name)`.
- A disconnected entry does **not** reserve its name: `isNameTaken` ignores it, and `lobbyOrder()` leaves it out. This is what lets the same name reconnect.
- Players still in `AwaitingName` are not in the registry; the Playground keeps them in a small set so they can be disconnected.

### `LobbyView`
Builds every outgoing message as a JSON string: nickname prompt (emoji list and optional error), lobby, error, game-started. All JSON/Map building moves here out of `Playground`. The Playground sends them with `player.sendMessage(json, channel)` (pick the `MessagesChannel` value that fits). It uses `registry.lobbyOrder()` so the numbers the client shows match the indexes the server resolves. Contract changes vs today:
- Online entries: `name`, `emoji`, `you` (boolean), `game` (session id or null). No player ids. The client shows position + 1 as the number to type.
- `running[].teams` is a list of lists of names, built from the session's `teams`.
- The top-level `you` (an id) is removed.
- The lobby message has `canRejoin` (boolean), true when the viewer has an ongoing rejoinable game.

### `Playground` (the lobby)
State: `PlayerRegistry`, set of awaiting-name players, `availableGames`, running `GameSession`s, `nextSessionId`, `PlaygroundCommandParser`, `LobbyView`.
Public API: `registerGame`, `connect(WebPlayer)`, `disconnect(WebPlayer)`, `process(WebPlayer, String raw)`, `removeGame(WebGame)`. Keep entry points `synchronized` as today.

`process(player, raw)`:
1. Decode JSON (invalid -> `error: "Invalid message"`).
2. `command = parser.parse(node)`.
3. If `command.allowedIn(player.state)`: `command.execute(this, player)`. Otherwise reply with an error (in `AwaitingName`, re-send the nickname prompt; in `InLobby` reply "You are not in a game." for game messages).

`connect`: new player is `AwaitingName`, added to the awaiting set, nickname prompt sent.
`disconnect`:
  - `AwaitingName`: remove from the awaiting set.
  - `InLobby`: remove from the registry.
  - `InGame`: call `game.disconnect(player)` and set the entry's `isConnected` to false. The game decides what a disconnect means (a non-rejoinable game removes the player, a rejoinable one keeps it). Do not check for `Rejoinable` here and do not remove the entry or the session; both are cleaned up by `removeGame` when the game ends.
  - Refresh lobbies in every case.
`removeGame(WebGame)` (called when a game ends): remove the session, drop its disconnected entries from the registry, put its connected players back to `InLobby`, refresh lobbies. Note: today it only clears the `clientGame` entries; the new version must also reset the state.
`refreshMenus`: send the lobby to every registered player whose state is `InLobby`.

### `WebServer` (transport only)
- Owns the `Playground` instance and a `Map<String, WebPlayer>` (`sessionId -> WebPlayer`).
- `onConnect`: `new WebPlayer(ctx)`, store it, `playground.connect(player)`.
- `onMessage`: find the player, `playground.process(player, ctx.message())`.
- `onClose`: remove it and `playground.disconnect(player)`.
- Remove `nextId`, the `Map<Integer, WsContext>`, `sendMessage(int, ...)` and `sendRaw(int, ...)`. Keep `toJson`.
- Check how `WebServices` and the games reference `WebServer.playground` (e.g. to call `removeGame`) and adapt that path.

## Request flow (reference)

```
socket -> WebServer (sessionId -> WebPlayer)
       -> playground.process(player, rawText)
       -> PlaygroundCommandParser.parse(node) -> PlaygroundCommand
       -> command.allowedIn(state) ? command.execute(playground, player) : error

GameCommand.execute -> session.game().parseCommand(node) -> game's command (e.g. PlaceMarkCommand) .execute(...)
```

Example: `{"type":"play","row":1,"col":2}` is not a playground type, so the parser returns `GameCommand`. The player is `InGame`, so it asks `WebTicTacToe.parseCommand`, which builds `PlaceMarkCommand`, which validates the fields and calls `play(...)`.

Game message types must not reuse playground types (`profile`, `start`, `leave`, `rejoin`), because those are matched first.

## Implementation order

1. New `WebGame`, `PlaygroundCommand`, and update `WebTicTacToe` + `PlaceMarkCommand` (adapt `getPlayer` lookup).
2. `PlayerState`, `WebPlayer` (holds `WsContext`), `GameSession`, `AvailableGame`.
3. `PlayerRegistry`, `LobbyView`, the five commands (including `RejoinCommand`), `PlaygroundCommandParser`.
4. Rewrite `Playground` on top of them.
5. Rewrite `WebServer` as transport only.
6. Update the frontend for the new protocol: `nickname` message becomes `profile`, `start` sends 1-based indexes per team, no player ids, no `lobby` request, no top-level `you`; a `rejoin` message sent when the lobby says `canRejoin`.
7. Make sure the module compiles (`mvn -pl engines/web -am compile`) and run the existing tests; run spotless if configured.

## Rejoin

Flow: the player disconnects during a `Rejoinable` game, so the entry stays with `isConnected = false` and the core `Player` untouched. Later a new connection starts in `AwaitingName` and sends `profile` with the same nickname. `EditProfileCommand` finds the disconnected entry, the new `WebPlayer` takes it over, and the state becomes `InLobby`. The lobby message says `canRejoin`, the client sends `rejoin`, and `RejoinCommand` reattaches the player to the game.

Rules:
- Identity is the nickname only (the game is played between friends, no authentication needed).
- The core `Player` must be the same one. Only the messager (the `WebPlayer`) changes, and it is switched manually with `setPlayerEngine`.
- A game opts in by implementing `Rejoinable`. Games that don't implement it keep today's behavior: disconnecting removes the player from the game.
- Inspect core first: `Rejoinable.rejoin(Player)`, `setPlayerEngine`, how to know that a game is still ongoing, and how to get a core `Player`'s game. If a core `Player` doesn't expose its game, keep the `GameSession` in the registry entry instead. Report any mismatch instead of guessing.

## Notes

- Concurrency: keep `Playground.process` `synchronized` for now, game commands included, as the code does today. Real-time games may later need game commands to run outside that lock, each game handling its own thread safety.
- Keep code style consistent with the repo (spotless/eclipse formatter, section comment banners as in the current `Playground`).
- Do not add features beyond this plan. If something in the existing code contradicts this plan (e.g. core API shape), stop and report instead of guessing.
