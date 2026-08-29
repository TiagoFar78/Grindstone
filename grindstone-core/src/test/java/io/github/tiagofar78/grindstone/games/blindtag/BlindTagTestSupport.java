package io.github.tiagofar78.grindstone.games.blindtag;

import io.github.tiagofar78.grindstone.game.GameDependencies;
import io.github.tiagofar78.grindstone.game.MessagesChannel;
import io.github.tiagofar78.grindstone.game.Player;
import io.github.tiagofar78.grindstone.games.blindtag.map.Cell;
import io.github.tiagofar78.grindstone.games.blindtag.map.Direction;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;
import io.github.tiagofar78.grindstone.services.SchedulerService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Minimal concrete BlindTag subclass for unit tests.
 * All rendering hooks are no-ops; messages are captured in a list.
 */
public class BlindTagTestSupport extends BlindTag {

    public final List<String> events = new ArrayList<>();

    /** Simple no-op scheduler for tests. */
    public static final SchedulerService SYNC_SCHEDULER =
            (delay, task) -> task.run();

    public static BlindTagTestSupport create(long seed) {
        GameDependencies deps = new GameDependencies(SYNC_SCHEDULER);
        List<Player> players = List.of(
                new TestBTPlayer(),
                new TestBTPlayer(),
                new TestBTPlayer());
        return new BlindTagTestSupport(deps, players, new Random(seed));
    }

    public BlindTagTestSupport(GameDependencies dependencies,
                                List<Player> players,
                                Random random) {
        super(dependencies, players, random);
    }

    // All abstract hooks â€” record event names for assertion
    @Override public void onTurnStart(BTPlayer current)           { events.add("turnStart"); }
    @Override public void onRotationEnd()                         { events.add("rotationEnd"); }
    @Override public void onTagEvent(BTPlayer n, BTPlayer t)      { events.add("tagEvent"); }
    @Override public void onTrapTriggered(BTPlayer v, int c)      { events.add("trap"); }
    @Override public void onBlankCell(BTPlayer p)                 { events.add("blank"); }
    @Override public void onGoldGained(BTPlayer p, int a)         { events.add("gold"); }
    @Override public void onTeleported(BTPlayer p, Cell d)        { events.add("teleport"); }
    @Override public void openShop(BTPlayer p)                    { events.add("shop"); }
    @Override public void onPathReveal(BTPlayer p, List<Cell> l)  { events.add("path"); }
    @Override public void onCompassUsed(BTPlayer p, Cell c, List<Cell> a) { events.add("compass"); }
    @Override public void onHorseUsed(BTPlayer p)                 { events.add("horse"); }
    @Override public void onTrapPlaced(BTPlayer p, Cell c)        { events.add("trapPlaced"); }
    @Override public void onClonePlaced(BTPlayer p, Cell c)       { events.add("clone"); }
    @Override public void onSixthSenseUsed(BTPlayer p, Direction d) { events.add("sixth"); }
    @Override public void onHawkEyeUsed(BTPlayer p, List<Cell> l) { events.add("hawk"); }
    @Override public BTPlayer onWitchTargetNeeded(BTPlayer c, List<BTPlayer> t) {
        events.add("witch");
        return t.get(0);
    }
    @Override public void onNeedyUsed(BTPlayer p)                 { events.add("needy"); }
    @Override public void onSubmitGuessStarted(List<BTPlayer> p)  { events.add("submitGuess"); }
    @Override public void onRevealPhaseStarted(List<BTPlayer> p)  { events.add("reveal"); }
    @Override public void onNoMovesAvailable(BTPlayer p)          { events.add("noMoves"); }
    @Override public void onUseItemsPhase(BTPlayer p)             { events.add("useItems"); }

    // Inner concrete BTPlayer for tests
    public static class TestBTPlayer extends BTPlayer {
        public TestBTPlayer() { super(UUID.randomUUID()); }

        @Override
        public void sendTranslatedMessage(String message, MessagesChannel channel) {
            // no-op
        }
    }
}
