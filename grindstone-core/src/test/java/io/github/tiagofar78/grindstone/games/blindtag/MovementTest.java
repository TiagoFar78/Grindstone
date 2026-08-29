package io.github.tiagofar78.grindstone.games.blindtag;

import io.github.tiagofar78.grindstone.game.GameDependencies;
import io.github.tiagofar78.grindstone.game.Player;
import io.github.tiagofar78.grindstone.games.blindtag.BlindTagTestSupport.TestBTPlayer;
import io.github.tiagofar78.grindstone.games.blindtag.items.Trap;
import io.github.tiagofar78.grindstone.games.blindtag.map.Arrow;
import io.github.tiagofar78.grindstone.games.blindtag.map.BTMap;
import io.github.tiagofar78.grindstone.games.blindtag.map.BlankCell;
import io.github.tiagofar78.grindstone.games.blindtag.map.Cell;
import io.github.tiagofar78.grindstone.games.blindtag.map.Direction;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;

/**
 * Tests for BlindTag.move(), runTrapChecks(), and runTagChecks().
 *
 * Uses a hand-crafted 3-cell map so every scenario is deterministic.
 *
 * Layout (row, col):
 *   cellA (0,0) --E--> cellB (0,1) --E--> cellC (0,2)
 *   cellB --W--> cellA
 *   cellA is spawn
 */
public class MovementTest {

    // Map cells
    private BlankCell cellA, cellB, cellC;
    private BTMap map;

    private BlindTagTestSupport game;

    @BeforeMethod
    public void setUp() {
        // Build cells
        cellA = new BlankCell(1);
        cellB = new BlankCell(2);
        cellC = new BlankCell(3);

        // Arrows: Aâ†’B, Bâ†’A, Bâ†’C, Câ†’B
        cellA.addArrow(Arrow.of(Direction.E, cellB));
        cellB.addArrow(Arrow.of(Direction.W, cellA));
        cellB.addArrow(Arrow.of(Direction.E, cellC));
        cellC.addArrow(Arrow.of(Direction.W, cellB));

        // Build map
        map = new BTMap();
        map.setCell(0, 0, cellA);
        map.setCell(0, 1, cellB);
        map.setCell(0, 2, cellC);
        map.setSpawnCell(cellA);

        // Players
        TestBTPlayer p1 = new TestBTPlayer();
        TestBTPlayer p2 = new TestBTPlayer();
        TestBTPlayer p3 = new TestBTPlayer();

        // Game using a controlled map
        GameDependencies deps = new GameDependencies(BlindTagTestSupport.SYNC_SCHEDULER);
        game = new ControlledMapGame(deps,
                List.of(p1, p2, p3),
                map, p1);
        // Note: we don't call game.start() â€” we set up state manually for fine-grained control
        game.start();
    }

    // >--------------------{ Basic movement }--------------------<

    @Test
    public void testMoveToAdjacentCell() {
        BTPlayer tagger = game.getTagger();
        tagger.moveTo(cellA);
        game.move(tagger, Direction.E);
        Assert.assertEquals(tagger.getCurrentCell(), cellB);
    }

    @Test
    public void testMoveReturnsTrueForValidDirection() {
        BTPlayer tagger = game.getTagger();
        tagger.moveTo(cellA);
        boolean result = game.move(tagger, Direction.E);
        Assert.assertTrue(result);
    }

    @Test
    public void testMoveReturnsFalseForInvalidDirection() {
        BTPlayer tagger = game.getTagger();
        tagger.moveTo(cellA);
        // No arrow going North from cellA
        boolean result = game.move(tagger, Direction.N);
        Assert.assertFalse(result);
    }

    @Test
    public void testMoveDoesNotChangePositionOnInvalidDirection() {
        BTPlayer tagger = game.getTagger();
        tagger.moveTo(cellA);
        game.move(tagger, Direction.N);
        Assert.assertEquals(tagger.getCurrentCell(), cellA);
    }

    // >--------------------{ Trap checks }--------------------<

    @Test
    public void testTrapDeductsGold() {
        BTPlayer tagger = game.getTagger();
        BTPlayer other = game.getBTPlayers().stream().filter(p -> p != tagger).findFirst().orElseThrow();
        Trap trap = new Trap(other);
        cellB.addTrap(trap);

        tagger.moveTo(cellA);
        game.move(tagger, Direction.E);

        Assert.assertEquals(tagger.getGold(), -Trap.GOLD_STEAL);
    }

    @Test
    public void testTrapIsRemovedAfterTrigger() {
        BTPlayer tagger = game.getTagger();
        BTPlayer other = game.getBTPlayers().stream().filter(p -> p != tagger).findFirst().orElseThrow();
        Trap trap = new Trap(other);
        cellB.addTrap(trap);

        tagger.moveTo(cellA);
        game.move(tagger, Direction.E);

        Assert.assertFalse(cellB.hasTraps());
    }

    @Test
    public void testMultipleTrapsAllFire() {
        BTPlayer tagger = game.getTagger();
        List<BTPlayer> others = game.getBTPlayers().stream().filter(p -> p != tagger).toList();
        cellB.addTrap(new Trap(others.get(0)));
        cellB.addTrap(new Trap(others.get(1)));

        tagger.moveTo(cellA);
        game.move(tagger, Direction.E);

        Assert.assertEquals(tagger.getGold(), -Trap.GOLD_STEAL * 2);
        Assert.assertFalse(cellB.hasTraps());
    }

    @Test
    public void testTrapGoldCanGoNegative() {
        BTPlayer tagger = game.getTagger();
        BTPlayer other = game.getBTPlayers().stream().filter(p -> p != tagger).findFirst().orElseThrow();
        cellB.addTrap(new Trap(other));
        tagger.moveTo(cellA);
        game.move(tagger, Direction.E);
        Assert.assertTrue(tagger.getGold() < 0);
    }

    @Test
    public void testTrapHookFired() {
        BTPlayer tagger = game.getTagger();
        BTPlayer other = game.getBTPlayers().stream().filter(p -> p != tagger).findFirst().orElseThrow();
        cellB.addTrap(new Trap(other));
        tagger.moveTo(cellA);
        game.events.clear();
        game.move(tagger, Direction.E);
        Assert.assertTrue(game.events.contains("trap"));
    }

    @Test
    public void testNoTrapHookWhenNoTraps() {
        BTPlayer tagger = game.getTagger();
        tagger.moveTo(cellA);
        game.events.clear();
        game.move(tagger, Direction.E);
        Assert.assertFalse(game.events.contains("trap"));
    }

    // >--------------------{ Tag checks â€” tagger moves into victim }--------------------<

    @Test
    public void testTaggerMovesIntoVictimTransfersTag() {
        BTPlayer tagger = game.getTagger();
        // Find the two non-taggers from the game's player list
        List<BTPlayer> others = game.getBTPlayers().stream()
                .filter(p -> p != tagger).toList();
        BTPlayer victim = others.get(0);
        BTPlayer bystander = others.get(1);

        tagger.moveTo(cellA);
        victim.moveTo(cellB);
        bystander.moveTo(cellC);

        game.move(tagger, Direction.E); // tagger steps on cellB where victim is

        Assert.assertEquals(game.getTagger(), victim);
    }

    @Test
    public void testOldTaggerTeleportedToSpawn() {
        BTPlayer tagger = game.getTagger();
        List<BTPlayer> others = game.getBTPlayers().stream()
                .filter(p -> p != tagger).toList();
        BTPlayer victim = others.get(0);
        BTPlayer bystander = others.get(1);

        tagger.moveTo(cellA);
        victim.moveTo(cellB);
        bystander.moveTo(cellC);

        game.setTagger(tagger);
        game.move(tagger, Direction.E);

        Assert.assertEquals(tagger.getCurrentCell(), cellA); // spawn
    }

    @Test
    public void testTagEventHookFired() {
        BTPlayer tagger = game.getTagger();
        List<BTPlayer> others = game.getBTPlayers().stream()
                .filter(p -> p != tagger).toList();
        BTPlayer victim = others.get(0);
        BTPlayer bystander = others.get(1);

        tagger.moveTo(cellA);
        victim.moveTo(cellB);
        bystander.moveTo(cellC);

        game.setTagger(tagger);
        game.events.clear();
        game.move(tagger, Direction.E);

        Assert.assertTrue(game.events.contains("tagEvent"));
    }

    // >--------------------{ Tag checks â€” non-tagger walks into tagger }--------------------<

    @Test
    public void testNonTaggerWalksIntoTaggerGetsTagged() {
        // Set player2 as the tagger sitting on cellB; taggerPlayer walks in
        BTPlayer tagger = game.getBTPlayers().get(1); // pick any player as tagger
        BTPlayer walker = game.getBTPlayers().get(0);
        BTPlayer bystander = game.getBTPlayers().get(2);

        game.setTagger(tagger);
        walker.moveTo(cellA);
        tagger.moveTo(cellB);
        bystander.moveTo(cellC);

        // walker (non-tagger) walks East into tagger
        game.move(walker, Direction.E);

        Assert.assertEquals(game.getTagger(), walker);
    }

    @Test
    public void testNonTaggerWalksIntoTaggerOldTaggerTeleported() {
        BTPlayer tagger = game.getBTPlayers().get(1);
        BTPlayer walker = game.getBTPlayers().get(0);
        BTPlayer bystander = game.getBTPlayers().get(2);

        game.setTagger(tagger);
        walker.moveTo(cellA);
        tagger.moveTo(cellB);
        bystander.moveTo(cellC);

        game.move(walker, Direction.E); // walker lands on tagger's cell

        // old tagger gets teleported to spawn
        Assert.assertEquals(tagger.getCurrentCell(), cellA);
    }

    // >--------------------{ Tag checks â€” no one to tag }--------------------<

    @Test
    public void testMoveIntoEmptyCellNoTagEvent() {
        BTPlayer tagger = game.getTagger();
        List<BTPlayer> others = game.getBTPlayers().stream()
                .filter(p -> p != tagger).toList();

        tagger.moveTo(cellA);
        others.get(0).moveTo(cellC); // not on cellB
        others.get(1).moveTo(cellC);

        game.setTagger(tagger);
        game.events.clear();
        game.move(tagger, Direction.E);

        Assert.assertFalse(game.events.contains("tagEvent"));
        Assert.assertEquals(game.getTagger(), tagger); // unchanged
    }

    // >--------------------{ Trap fires before tag }--------------------<

    @Test
    public void testTrapFiresBeforeTagWhenBothOnSameCell() {
        BTPlayer tagger = game.getTagger();
        List<BTPlayer> others = game.getBTPlayers().stream()
                .filter(p -> p != tagger).toList();
        BTPlayer victim = others.get(0);
        BTPlayer bystander = others.get(1);

        // cellB has a trap; victim (non-tagger) is there too
        cellB.addTrap(new Trap(bystander));
        tagger.moveTo(cellA);
        victim.moveTo(cellB);
        bystander.moveTo(cellC);

        game.setTagger(tagger);
        game.events.clear();
        game.move(tagger, Direction.E);

        // Trap fires (gold deducted from mover = tagger)
        Assert.assertEquals(tagger.getGold(), -Trap.GOLD_STEAL);
        // Tag also happens
        Assert.assertEquals(game.getTagger(), victim);

        // Verify order: trap event before tag event in the event list
        int trapIdx = game.events.indexOf("trap");
        int tagIdx  = game.events.indexOf("tagEvent");
        Assert.assertTrue(trapIdx < tagIdx,
                "Trap should fire before tag event. Events: " + game.events);
    }

    // >--------------------{ Chained tag check resolves cleanly }--------------------<

    @Test
    public void testTagLoopResolvesWithoutInfiniteLoop() {
        BTPlayer tagger = game.getTagger();
        List<BTPlayer> others = game.getBTPlayers().stream()
                .filter(p -> p != tagger).toList();

        tagger.moveTo(cellA);
        others.get(0).moveTo(cellB);
        others.get(1).moveTo(cellC);

        game.setTagger(tagger);
        game.move(tagger, Direction.E);

        Assert.assertNotNull(game.getTagger());
    }

    // >--------------------{ Helper: game with a fixed map }--------------------<

    /**
     * BlindTag subclass that uses a pre-built map and starting state,
     * bypassing the generator and random placement done in load().
     */
    static class ControlledMapGame extends BlindTagTestSupport {

        private final BTMap fixedMap;
        private final BTPlayer fixedTagger;

        ControlledMapGame(GameDependencies deps, List<Player> players,
                          BTMap map, BTPlayer tagger) {
            super(deps, players, new Random(0));
            this.fixedMap = map;
            this.fixedTagger = tagger;
        }

        @Override
        public void load() {
            // Call super to initialize btPlayers list from the lobby
            super.load();
            // Override map and tagger with our controlled versions
            setMap(fixedMap);
            setTagger(fixedTagger);
            // Player positions are set per-test via moveTo â€” don't overwrite here
        }
    }
}
