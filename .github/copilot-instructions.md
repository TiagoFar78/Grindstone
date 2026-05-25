# Copilot instructions for Grindstone

Build / Test / Lint
- Build (full): mvn package
- Build skipping tests: mvn -DskipTests package
- Run all tests: mvn test
- Run a single test class: mvn -Dtest=TeamLayoutSolverTest test
- Run a single test method: mvn -Dtest=TeamLayoutSolverTest#testSimpleExactFit test
  (Surefire/TestNG semantics: class or class#method)
- Check code formatting (Spotless): mvn spotless:check
- Apply code formatting (Spotless): mvn spotless:apply
- Output artifact: target/grindstone-<version>.jar (plugin jar)

Project overview / high-level architecture
- Type: PaperMC (Minecraft) plugin (see src/main/resources/plugin.yml). Main class: io.github.tiagofar78.grindstone.Grindstone
- Build system: Maven (pom.xml). Java language level: 22 (maven.compiler.source/target)
- Packaging: produces a plugin JAR with plugin.yml in target/ — deploy by copying to a Paper server's plugins/ directory
- Key packages:
  - io.github.tiagofar78.grindstone.game — Minigame, MinigameFactory, MapFactory, GamesManager, game phases (Preparing/Loading/Finished/Disabled)
  - io.github.tiagofar78.grindstone.queue — MatchmakingQueue, QueuesManager, Lobby and queue lifecycle
  - io.github.tiagofar78.grindstone.party — Party abstractions and PartyService fallback provider
  - io.github.tiagofar78.grindstone.commands — command implementations wired via Grindstone.registerCommands (Paper brigadier integration)
  - io.github.tiagofar78.grindstone.bukkit — thin wrappers (Scheduler, BukkitPlayer)
  - io.github.tiagofar78.grindstone.util — TeamLayoutSolver (well-tested) and TextFormatting
- Configuration & i18n:
  - config.yml (src/main/resources/config.yml) ↔ GrindstoneConfig loads keys into final fields
  - translations in src/main/resources/lang/*.properties registered via MessagesRepo (external dependency)
- External/runtime dependencies (provided scope): Paper API, WorldEdit. MessagesRepo (messagesrepo) used for translations

Key conventions / patterns to know
- Phase pattern: game phases are modeled as implementations of Phase (PreparingPhase, LoadingPhase, FinishedPhase, DisabledPhase). Minigame lifecycle uses these phases.
- Factories: MinigameFactory and MapFactory produce Minigame/MinigameMap instances — prefer factories to direct constructors for new game types
- Command registration: Commands are registered lazily via lifecycle event (Paper brigadier Commands). Use Grindstone.registerCommand/registerCommands helpers
- Config mapping: config.yml keys use CamelCase names mapped directly in GrindstoneConfig (fields are final, loaded once via GrindstoneConfig.load())
- Formatting: Spotless config uses formatter-config.xml. Run spotless:check in CI to enforce formatting.
- Tests: TestNG is used (see pom.xml). TeamLayoutSolver has targeted unit tests — run single classes/methods with -Dtest as above.

Files to inspect first for major changes
- pom.xml (build & plugins), Grindstone.java (plugin lifecycle), GrindstoneConfig.java (config mapping), src/main/resources/plugin.yml, src/main/resources/config.yml, src/main/resources/lang/

Notes for Copilot sessions
- Prefer edits that preserve plugin lifecycle hooks and resource keys (plugin.yml and config.yml)
- When adding commands or listeners, wire them through the provided registerCommand/registerListener helpers
- Keep Java 22 target in mind for language features and compilation

