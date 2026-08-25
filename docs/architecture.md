# Grindstone Architecture

## Modules

Grindstone is a Maven multi-module project. The root POM is a pure aggregator.

```
grindstone
├── grindstone-core           — engine-agnostic game framework
├── grindstone-matchmaking    — matchmaking abstractions
├── engines/cli               — CLI engine
└── engines/bukkit            — Bukkit/Paper engine
```

Engines depend on `grindstone-core`. They never depend on each other.

---

## grindstone-core

The heart of the framework. Contains all game logic with zero engine dependencies. Organized into three packages:

### `game/`

General-purpose game infrastructure reused by every game. This includes the base `Game` class and its lifecycle, the `Phase` chain, `Player`, `MatchLobby`, and the messaging contracts (`MessageKey`, `MessagesChannel`).

### `games/`

Concrete game implementations. Each game category gets a sub-package (e.g. `turnbasedduel/`) containing an abstract class with the full game logic. The abstract rendering/output methods are left for the engine-specific subclass to implement.

### `services/`

Service interfaces that games depend on, injected via `GameDependencies`. Currently: `SchedulerService`, `TranslationService`, and `PlayerLocaleService`. Each engine provides its own implementations.

---

## grindstone-matchmaking

Interfaces and abstractions for matchmaking (`Party`, `MatchmakingQueue`, `GamesManager`). Not yet fully implemented.

---

## Engines

Each engine provides:
- A concrete `Player` subclass that delivers messages via the platform API.
- A concrete `SchedulerService` implementation using the platform's task scheduler.
- Engine-specific subclasses of the abstract games in `grindstone-core/games/`, implementing the rendering hooks.

The **CLI engine** is the only fully working engine and serves as the reference implementation. The **Bukkit engine** module exists but still contains pre-refactor legacy code and has not yet been migrated to use the `grindstone-core` abstractions.
