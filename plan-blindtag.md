# BlindTag Implementation Plan

## Problem Statement

Implement the BlindTag game logic in `grindstone-core/games/blindtag/`, following the same pattern as `TurnBasedDuel`/`TicTacToe`. Engine layer (CLI) is out of scope. The plan lives as `plan-blindtag.md` at the repo root.

---

## Requirements

- 3 players; one is the tagger at any time
- 5Ã—5 grid of positions; some positions are gaps (null) â€” real cell count = 25 minus gap count
- Cell types: Gold (1â€“2), Shop (2â€“3), Good (1â€“3), Bad (3â€“5), Teleport (1), Blank (remainder) â€” each a concrete subclass of `Cell`; total real cells = 25 âˆ’ gaps
- Teleport cell holds no guessable number; spawn cell is a randomly chosen non-Teleport real cell
- Arrows on a cell point to their destination cell directly; an arrow can be self-referencing; wrap-around arrows skip gap positions and land on the next real cell in that direction; each cell has at most one wrap-arrow per cardinal direction
- Players can only move by following an arrow; gaps are never reachable
- Players start spread randomly across distinct real cells (not all on spawn)
- Full turn sequence: move â†’ trap checks â†’ tag checks â†’ cell effect â†’ use items â†’ (if extra move granted, loop back to move) â†’ end turn
- Rotation = all 3 players took a turn; non-taggers score +10 at rotation end
- Win condition: first player to reach score 200
- Final score = current score + 2 Ã— correctly guessed map elements (cell type per cell, each arrow per cell, cell number per non-Teleport cell)
- Guessed map is a `BTMap` submitted in `SubmitGuessPhase`; not populated during play (player's guessedMap is null until submitted)
- Phase chain: `LoadingPhase â†’ MatchIntroPhase â†’ PlayPhase â†’ SubmitGuessPhase â†’ RevealPhase (extends FinishedPhase) â†’ DisabledPhase`
- `RevealPhase` shows results + winner, runs a timer, then calls `FinishedPhase` logic
- Shop: 4 item slots, gold currency; item cost is a property of the item itself (price-per-buy evolution deferred)
- 8 items: Compass, SixthSense, HawkEye, Clone, Witch, Needy, Horse, Trap
- Lucky/unlucky wheels with 6 outcomes each, chainable spins (depth-guarded at 10)
- Teleport cell always teleports to a non-Teleport real cell

---

## Architecture

```
games/blindtag/
â”œâ”€â”€ BlindTag.java
â”œâ”€â”€ phases/
â”‚   â”œâ”€â”€ PlayPhase.java
â”‚   â”œâ”€â”€ SubmitGuessPhase.java
â”‚   â””â”€â”€ RevealPhase.java             (extends FinishedPhase)
â”œâ”€â”€ map/
â”‚   â”œâ”€â”€ BTMap.java
â”‚   â”œâ”€â”€ MapGenerator.java
â”‚   â”œâ”€â”€ Cell.java                    (abstract)
â”‚   â”œâ”€â”€ GoldCell.java
â”‚   â”œâ”€â”€ ShopCell.java
â”‚   â”œâ”€â”€ GoodCell.java
â”‚   â”œâ”€â”€ BadCell.java
â”‚   â”œâ”€â”€ TeleportCell.java
â”‚   â”œâ”€â”€ BlankCell.java
â”‚   â””â”€â”€ Arrow.java
â”œâ”€â”€ player/
â”‚   â””â”€â”€ BTPlayer.java
â”œâ”€â”€ items/
â”‚   â”œâ”€â”€ Item.java                    (abstract)
â”‚   â”œâ”€â”€ Shop.java
â”‚   â”œâ”€â”€ Compass.java
â”‚   â”œâ”€â”€ SixthSense.java
â”‚   â”œâ”€â”€ HawkEye.java
â”‚   â”œâ”€â”€ Clone.java
â”‚   â”œâ”€â”€ Witch.java
â”‚   â”œâ”€â”€ Needy.java
â”‚   â”œâ”€â”€ Horse.java
â”‚   â””â”€â”€ Trap.java
â””â”€â”€ wheel/
    â”œâ”€â”€ LuckyWheel.java
    â””â”€â”€ UnluckyWheel.java
```

**Phase chain:**
```
LoadingPhase â†’ MatchIntroPhase â†’ PlayPhase â†’ SubmitGuessPhase â†’ RevealPhase â†’ DisabledPhase
```

**Turn state inside PlayPhase:**
```
AwaitingMove
  â†’ (player submits direction) â†’ TrapAndTagChecks
  â†’ (resolved) â†’ CellEffect
  â†’ UseItem
    â†’ (extra move granted) â†’ AwaitingMove   [loops back]
    â†’ (no extra move) â†’ TurnEnd
TurnEnd
  â†’ (all players took a turn) â†’ RotationScoring â†’ WinCheck
    â†’ (no winner) â†’ AwaitingMove
    â†’ (score >= 200) â†’ SubmitGuessPhase
  â†’ (not end of rotation) â†’ AwaitingMove [next player]
```

---

## Task Breakdown

### Task 1: Map data model â€” Direction, Arrow, Cell hierarchy

**Objective:** Define the foundational map types with no game logic yet.

**Implementation:**
- `Direction`: enum of 8 values (N, NE, E, SE, S, SW, W, NW). Include `isCardinal()` helper (N, E, S, W) since wrap-around arrows are only on cardinal directions.
- `Arrow`: record with `Direction direction`, `Cell destination`, `boolean selfReferencing`, `Optional<String> wrapColor`. `selfReferencing` means the arrow points back to its own cell; destination is still set to that same cell for uniformity.
- `Cell` (abstract): holds an `int number`, a `Map<Direction, Arrow>` of outgoing arrows, a list of `Trap` markers, and a list of `Clone` markers. Abstract method: `applyEffect(BlindTag, BTPlayer)`. Concrete subclasses: `GoldCell`, `ShopCell`, `GoodCell`, `BadCell`, `TeleportCell`, `BlankCell` â€” each overrides `applyEffect` with a stub (filled in Task 6). `TeleportCell` overrides `getNumber()` to throw `UnsupportedOperationException` since it has no guessable number.

**Tests:** Construct each cell type; assert arrow add/get by direction; assert `TeleportCell.getNumber()` throws; assert `Arrow.selfReferencing` has destination equal to source cell.

**Demo:** All 6 cell types and `Arrow` instantiate and their APIs work correctly in tests.

---

### Task 2: BTMap and MapGenerator

**Objective:** `BTMap` represents the sparse 5Ã—5 grid; `MapGenerator` produces valid random maps.

**Implementation:**
- `BTMap`: wraps a `Cell[5][5]` where some positions are `null` (gaps). Exposes `getCell(row, col)` (nullable), `getCellByNumber(int)`, `getSpawnCell()`. Navigation is done through `Arrow.destination` directly â€” no `getNeighbour` method. `mutateArrows(Random)`: removes one random existing arrow and adds one new valid arrow (used by unlucky wheel).
- `MapGenerator`: places cell types respecting count constraints (Gold 1â€“2, Shop 2â€“3, Good 1â€“3, Bad 3â€“5, Teleport 1, rest Blank). Fills remaining non-null positions with `BlankCell`. Assigns shuffled numbers 1â€“(realCellCount) to non-Teleport cells (Teleport gets no guessable number). Generates arrows: for each real cell, adds at least one outgoing arrow pointing to another real cell, resolving wrap-around by scanning past gaps. Picks a random non-Teleport real cell as spawn. Guarantees no arrow points to a gap position.

**Tests:** Assert cell-type counts within spec over N generated maps. Assert every real cell has â‰¥1 outgoing arrow. Assert all non-Teleport cells have distinct numbers and no gap or Teleport holds a number. Assert spawn cell is never a `TeleportCell`. Assert null positions are unreachable (no arrow points to a gap).

**Demo:** Generator consistently produces valid maps; all constraints verified in tests.

---

### Task 3: BTPlayer

**Objective:** `BTPlayer` wraps the framework `Player` and carries all per-player mutable state.

**Implementation:**
- Fields: current `Cell`, gold (default 0), item slots (list, max 4), score (default 0), guessed map (`BTMap`, initialized as `null` â€” set only during `SubmitGuessPhase`).
- Methods: `addGold(int)`, `deductGold(int)` (allows negative), `addItem(Item)` (enforces 4-slot cap, returns boolean success), `removeItem(Item)`, `moveTo(Cell)` (updates `currentCell` field only â€” cell does not track occupants).

**Tests:** Gold goes negative correctly. Fifth item rejected (`addItem` returns false). `moveTo` updates cell reference. Guessed map starts null.

**Demo:** All `BTPlayer` state transitions verified in unit tests.

---

### Task 4: BlindTag game class + PlayPhase skeleton

**Objective:** `BlindTag extends Game` bootstraps the game; `PlayPhase` drives turn order and rotation scoring.

**Implementation:**
- `BlindTag.load()`: generates map via `MapGenerator`, wraps players into `BTPlayer`, picks a random tagger, spreads players across random distinct real cells.
- `BlindTag` exposes: `getBTPlayers()`, `getTagger()`, `setTagger(BTPlayer)`, `getMap()`. Abstract rendering hooks: `onTurnStart(BTPlayer current)`, `onRotationEnd()`.
- `PlayPhase`: tracks turn index (0â€“2) and rotation count. On `start()` calls `onTurnStart`. After all 3 players complete their turn: awards +10 to non-taggers, calls `onRotationEnd`, checks win (score â‰¥ 200 â†’ `next()` returns `new SubmitGuessPhase(...)`). Otherwise `next()` stays in `PlayPhase` advancing turn index.

**Tests:** Manually advance 3 turns; assert non-taggers gained 10 points; assert rotation counter incremented; assert transition to `SubmitGuessPhase` fires when score hits 200.

**Demo:** Game starts, turns rotate, rotation scoring fires, win condition triggers correct phase transition.

---

### Task 5: Movement, trap checks, and tag checks

**Objective:** Implement `BlindTag.move(BTPlayer, Direction)` and the full post-move check sequence.

**Implementation:**
- Movement: validate arrow exists in direction on current cell; follow `Arrow.destination`; call `player.moveTo(destination)`.
- Trap checks (before tag): if destination has any `Trap` markers â†’ deduct 3 gold from mover per trap, remove each triggered trap, call abstract `onTrapTriggered(BTPlayer victim, int trapsTriggered)`.
- Tag checks (loop until clean): find all other players whose `getCurrentCell() == destination`. If mover is tagger and another player is at destination â†’ `tag(mover, victim)`: `setTagger(victim)`, teleport old tagger to spawn (`moveTo(spawnCell)`), call abstract `onTagEvent(BTPlayer newTagger, BTPlayer teleportedToSpawn)`. Re-check destination. If mover is non-tagger and tagger is at destination â†’ `tag(tagger, mover)` similarly, re-check.

**Tests:** Tagger moves into another player's cell â†’ tag transfer + spawn teleport. Non-tagger walks into tagger â†’ role swap. Invalid direction â†’ rejected. Trap fires and is removed. Multiple traps all fire. Tag re-check after teleport resolves cleanly.

**Demo:** All movement, trap, and tag scenarios pass unit tests on controlled maps.

---

### Task 6: Cell effects

**Objective:** Implement `applyEffect` on all 6 concrete cell subclasses, replacing Task 1 stubs.

**Implementation:**
- `GoldCell`: `player.addGold(5)`; calls abstract `onGoldGained(BTPlayer, 5)`.
- `ShopCell`: calls abstract `openShop(BTPlayer)` (shop logic arrives in Task 8).
- `GoodCell`: `new LuckyWheel(random).spin(game, player)` (wheel stubbed until Task 7).
- `BadCell`: `new UnluckyWheel(random).spin(game, player)` (wheel stubbed until Task 7).
- `TeleportCell`: picks a random real non-Teleport cell; calls `player.moveTo(dest)`; re-runs trap checks, tag checks, then `dest.applyEffect(game, player)`. Because destination is guaranteed non-Teleport, no recursion cap needed.
- `BlankCell`: no-op; calls abstract `onBlankCell(BTPlayer)`.
- Abstract hooks: `onGoldGained(BTPlayer, int)`, `onBlankCell(BTPlayer)`, `onTeleported(BTPlayer, Cell)`.

**Tests:** GoldCell â†’ correct gold delta. ShopCell â†’ `openShop` hook called. TeleportCell â†’ player ends on a different non-Teleport cell; trap/tag checks re-run at destination. BlankCell â†’ no state change.

**Demo:** All cell effects produce correct state changes in unit tests.

---

### Task 7: Lucky and unlucky wheels

**Objective:** Fully implement both wheels with all 6 outcomes each; chainable spins depth-guarded at 10.

**Implementation:**
- `LuckyWheel` outcomes (index 0â€“5): double gold, steal 1 gold from each other player, +3 gold, free item (calls `openShop`), move another space (sets `extraMoveGranted = true` on `PlayPhase`), spin unlucky wheel.
- `UnluckyWheel` outcomes: teleport to spawn (no cell effect), give 2 gold to each other player, âˆ’3 gold, path reveal (calls abstract `onPathReveal(BTPlayer, List<Cell>)` â€” BFS stub until Task 9), board mutated (`map.mutateArrows(random)`), two extra spins (calls `spin` twice, each respects depth guard).
- Both wheels take an injected `Random`.

**Tests:** Mock `Random` to force each of the 12 outcomes; assert correct gold deltas, spawn teleport, map mutation, spin chain halts at depth 10.

**Demo:** All 12 outcomes correct; chaining never exceeds depth limit.

---

### Task 8: Shop and items â€” Compass, Horse, Trap

**Objective:** Implement the `Item` abstract class, `Shop` catalogue, buy/use pipeline, and the three simplest items.

**Implementation:**
- `Item` (abstract): field `int cost`; abstract `use(BlindTag, BTPlayer)`.
- `Shop`: static catalogue (list of concrete `Item` instances). `buy(BlindTag, BTPlayer, Item)`: validates `player.gold >= item.cost` and `player.itemSlots.size() < 4`; deducts gold; adds item. Item may be used immediately after purchase (caller's responsibility).
- `BlindTag.useItem(BTPlayer, Item)`: calls `item.use(this, player)`; removes item from slots.
- `Compass.use`: calls abstract `onCompassUsed(BTPlayer, Cell current, List<Cell> adjacent)` to reveal adjacent cell types and current cell number. Does not write into guessed map (guessing deferred to `SubmitGuessPhase`).
- `Horse.use`: sets `extraMoveGranted = true` on the active `PlayPhase`; calls abstract `onHorseUsed(BTPlayer)`.
- `Trap.use`: places a `Trap` marker on the player's current cell; calls abstract `onTrapPlaced(BTPlayer, Cell)`.

**Tests:** Buy with insufficient gold â†’ rejected. Buy with full slots â†’ rejected. Compass hook called with correct adjacent cells. Horse sets flag. Trap marker present on cell after use; triggered and removed by Task 5 pipeline.

**Demo:** Shop pipeline and first three items behave correctly in unit tests.

---

### Task 9: Remaining items â€” SixthSense, HawkEye, Clone, Witch, Needy

**Objective:** Implement the five remaining items; introduce BFS on the arrow graph.

**Implementation:**
- BFS helper on `BTMap`: `findShortestPath(Cell from, Cell to)` â€” traverses `Arrow.destination` edges, returns `List<Cell>` or empty if unreachable. Wire this into the `onPathReveal` stub from Task 7.
- `SixthSense.use`: computes `Direction` from current player's grid position toward tagger (if not tagger) or nearest other player by grid coordinates (if tagger); calls abstract `onSixthSenseUsed(BTPlayer, Direction)`.
- `HawkEye.use`: runs BFS to target; calls abstract `onHawkEyeUsed(BTPlayer, List<Cell> path)`.
- `Clone.use`: places a `Clone` marker (with owner reference) on current cell; calls abstract `onClonePlaced(BTPlayer, Cell)`. Clone is removed when the current tagger (who is not the clone owner) passes through the cell â€” add clone check to the movement pipeline after tag checks.
- `Witch.use`: calls abstract `onWitchTargetNeeded(BTPlayer caster, List<BTPlayer> targets)` to get chosen target; teleports target to a random non-Teleport real cell; runs trap checks and tag checks at destination.
- `Needy.use`: picks a random other player; picks a random arrow destination from that player's current cell; teleports user there; runs trap checks and tag checks.

**Tests:** BFS finds shortest path in known map; returns empty on disconnected graph. SixthSense direction correct for known grid positions. Clone removed only when tagger crosses it (not a regular player). Witch triggers trap/tag checks at destination. Needy lands on a cell reachable from the chosen player.

**Demo:** All 5 items and BFS helper pass unit tests.

---

### Task 10: SubmitGuessPhase and RevealPhase

**Objective:** Implement the two end-game phases and final score computation.

**Implementation:**
- `SubmitGuessPhase extends Phase`: on `start()` calls abstract `onSubmitGuessStarted(List<BTPlayer>)`. Exposes `submitGuess(BTPlayer, BTMap guessedMap)`: stores guessed map on player (`player.setGuessedMap(...)`). Once all players submitted, computes bonuses and transitions to `RevealPhase`.
- Score bonus computation (inline in `SubmitGuessPhase` or a `ScoreComputer` helper): for each real cell in the real map, compare with corresponding cell in guessed map â€” cell type match (+2), each arrow direction match (+2), cell number match for non-Teleport cells (+2). Add total bonus to `player.score`.
- `RevealPhase extends FinishedPhase`: calls `FinishedPhase` logic (victory/defeat messages via `sendVictoryMessage` etc.), then calls abstract `onRevealPhaseStarted(List<BTPlayer>)` to show each player's guessed map. Ends after a timer via `SchedulerService` from `GameDependencies`.

**Tests:** Perfect guess â†’ bonus = totalElements Ã— 2. Zero correct â†’ bonus = 0. Partial guess â†’ exact expected value. Teleport cell number never counted. Highest scorer wins; tied scores produce all tied players as winners.

**Demo:** Scoring computation verified for all cases; phase transitions correctly after all guesses submitted.

---

### Task 11: Full pipeline integration and end-to-end test

**Objective:** Wire all subsystems into a clean single-entry turn pipeline; run a complete game end-to-end.

**Implementation:**
- Introduce `TurnEngine` (package-private helper on `BlindTag`) with `execute(BTPlayer, Direction)`: move â†’ trap checks â†’ tag checks â†’ cell effect â†’ use items (via abstract hook `onUseItemsPhase(BTPlayer)`) â†’ if `extraMoveGranted` clear flag and loop back to move.
- `PlayPhase.submitMove(BTPlayer, Direction)` delegates entirely to `TurnEngine.execute`.
- Cover edge case: player with no valid arrows (fully isolated cell after `mutateArrows`) â†’ `TurnEngine` detects and skips move step, calls abstract `onNoMovesAvailable(BTPlayer)`.
- End-to-end test: 3 players, seeded `Random`, fixed map; simulate full game until score â‰¥ 200; assert final scores match expected values; assert no illegal states (no player on a gap, no orphaned trap/clone markers, no infinite spin chains).

**Demo:** Complete 3-player game runs end-to-end with seeded RNG, reaches a winner, correct final rankings produced.
