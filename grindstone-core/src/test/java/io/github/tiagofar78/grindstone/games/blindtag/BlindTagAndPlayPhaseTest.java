package io.github.tiagofar78.grindstone.games.blindtag;

import io.github.tiagofar78.grindstone.games.blindtag.phases.PlayPhase;
import io.github.tiagofar78.grindstone.games.blindtag.phases.SubmitGuessPhase;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class BlindTagAndPlayPhaseTest {

    private BlindTagTestSupport game;

    @BeforeMethod
    public void setUp() {
        game = BlindTagTestSupport.create(42);
        game.start(); // LoadingPhase â†’ MatchIntroPhase â†’ PlayPhase
    }

    // >--------------------{ load() }--------------------<

    @Test
    public void testMapIsGeneratedAfterLoad() {
        Assert.assertNotNull(game.getMap());
        Assert.assertFalse(game.getMap().getRealCells().isEmpty());
    }

    @Test
    public void testThreeBTPlayersAfterLoad() {
        Assert.assertEquals(game.getBTPlayers().size(), 3);
    }

    @Test
    public void testTaggerIsAssignedAfterLoad() {
        Assert.assertNotNull(game.getTagger());
        Assert.assertTrue(game.getBTPlayers().contains(game.getTagger()));
    }

    @Test
    public void testPlayersSpreadAcrossDistinctCells() {
        List<BTPlayer> players = game.getBTPlayers();
        long distinctCells = players.stream()
                .map(BTPlayer::getCurrentCell)
                .distinct()
                .count();
        // With 3 players and at least 10 real cells, they should be on distinct cells
        Assert.assertEquals(distinctCells, 3,
                "Players should start on distinct cells");
    }

    @Test
    public void testAllPlayersStartOnRealCells() {
        var realSet = new java.util.HashSet<>(game.getMap().getRealCells());
        for (BTPlayer p : game.getBTPlayers()) {
            Assert.assertTrue(realSet.contains(p.getCurrentCell()),
                    "Player is not on a real cell");
        }
    }

    // >--------------------{ PlayPhase initial state }--------------------<

    @Test
    public void testPlayPhaseIsActive() {
        Assert.assertTrue(game.getCurrentPhase() instanceof PlayPhase);
    }

    @Test
    public void testPlayPhaseStartsAtTurnIndexZero() {
        PlayPhase phase = (PlayPhase) game.getCurrentPhase();
        Assert.assertEquals(phase.getTurnIndex(), 0);
    }

    @Test
    public void testPlayPhaseStartsAtRotationZero() {
        PlayPhase phase = (PlayPhase) game.getCurrentPhase();
        Assert.assertEquals(phase.getRotationCount(), 0);
    }

    @Test
    public void testOnTurnStartCalledOnStart() {
        // setUp calls game.start() which fires onTurnStart once
        Assert.assertTrue(game.events.contains("turnStart"));
    }

    @Test
    public void testIsClockNotStopped() {
        Assert.assertFalse(game.getCurrentPhase().isClockStopped());
    }

    @Test
    public void testHasGameStarted() {
        Assert.assertTrue(game.getCurrentPhase().hasGameStarted());
    }

    @Test
    public void testHasGameNotEnded() {
        Assert.assertFalse(game.getCurrentPhase().hasGameEnded());
    }

    // >--------------------{ Rotation scoring }--------------------<

    @Test
    public void testNonTaggersGainScoreAfterRotation() {
        PlayPhase phase = (PlayPhase) game.getCurrentPhase();
        BTPlayer tagger = game.getTagger();

        // End all 3 turns to complete one rotation
        phase.endTurn();
        phase.endTurn();
        phase.endTurn();

        for (BTPlayer p : game.getBTPlayers()) {
            if (p == tagger) {
                Assert.assertEquals(p.getScore(), 0, "Tagger should not gain score");
            } else {
                Assert.assertEquals(p.getScore(), BlindTag.ROTATION_SCORE,
                        "Non-tagger should gain ROTATION_SCORE");
            }
        }
    }

    @Test
    public void testRotationCountIncrementedAfterThreeTurns() {
        PlayPhase phase = (PlayPhase) game.getCurrentPhase();
        phase.endTurn();
        phase.endTurn();
        phase.endTurn();
        Assert.assertEquals(phase.getRotationCount(), 1);
    }

    @Test
    public void testOnRotationEndCalledAfterThreeTurns() {
        PlayPhase phase = (PlayPhase) game.getCurrentPhase();
        int beforeCount = (int) game.events.stream().filter(e -> e.equals("rotationEnd")).count();
        phase.endTurn();
        phase.endTurn();
        phase.endTurn();
        int afterCount = (int) game.events.stream().filter(e -> e.equals("rotationEnd")).count();
        Assert.assertEquals(afterCount, beforeCount + 1);
    }

    @Test
    public void testOnTurnStartCalledForEachNewTurn() {
        PlayPhase phase = (PlayPhase) game.getCurrentPhase();
        long before = game.events.stream().filter(e -> e.equals("turnStart")).count();
        phase.endTurn(); // player 2's turn starts
        long after = game.events.stream().filter(e -> e.equals("turnStart")).count();
        Assert.assertEquals(after, before + 1);
    }

    @Test
    public void testTurnIndexAdvancesAfterEndTurn() {
        PlayPhase phase = (PlayPhase) game.getCurrentPhase();
        phase.endTurn();
        Assert.assertEquals(phase.getTurnIndex(), 1);
        phase.endTurn();
        Assert.assertEquals(phase.getTurnIndex(), 2);
    }

    @Test
    public void testMultipleRotationsAccumulateScore() {
        PlayPhase phase = (PlayPhase) game.getCurrentPhase();
        // Two full rotations
        for (int i = 0; i < 6; i++) {
            phase.endTurn();
        }
        BTPlayer tagger = game.getTagger();
        for (BTPlayer p : game.getBTPlayers()) {
            if (p != tagger) {
                Assert.assertEquals(p.getScore(), BlindTag.ROTATION_SCORE * 2);
            }
        }
    }

    // >--------------------{ Win condition â†’ SubmitGuessPhase }--------------------<

    @Test
    public void testTransitionsToSubmitGuessWhenScoreReachesWinScore() {
        PlayPhase phase = (PlayPhase) game.getCurrentPhase();
        // Manually give a non-tagger enough score to win
        BTPlayer nonTagger = game.getBTPlayers().stream()
                .filter(p -> p != game.getTagger())
                .findFirst()
                .orElseThrow();
        nonTagger.addScore(BlindTag.WIN_SCORE - BlindTag.ROTATION_SCORE);

        // One rotation will push them over
        phase.endTurn();
        phase.endTurn();
        phase.endTurn();

        Assert.assertTrue(game.getCurrentPhase() instanceof SubmitGuessPhase);
    }

    @Test
    public void testNoTransitionBeforeWinScore() {
        PlayPhase phase = (PlayPhase) game.getCurrentPhase();
        // Give non-taggers score just below threshold
        for (BTPlayer p : game.getBTPlayers()) {
            if (p != game.getTagger()) {
                p.addScore(BlindTag.WIN_SCORE - BlindTag.ROTATION_SCORE - 1);
            }
        }

        phase.endTurn();
        phase.endTurn();
        phase.endTurn();

        // Still in PlayPhase (score = WIN_SCORE - 1 after rotation)
        Assert.assertTrue(game.getCurrentPhase() instanceof PlayPhase);
    }

    // >--------------------{ removePlayerFromGame }--------------------<

    @Test
    public void testRemovePlayerReducesCount() {
        BTPlayer p0 = game.getBTPlayers().get(0);
        game.playerLeft(p0);
        Assert.assertEquals(game.getBTPlayers().size(), 2);
    }

    @Test
    public void testRemovePlayerToOneTransitionsPhase() {
        // Remove two players â€” second removal leaves 1 player who wins by default
        BTPlayer p0 = game.getBTPlayers().get(0);
        BTPlayer p1 = game.getBTPlayers().get(1);
        game.playerLeft(p0);
        game.playerLeft(p1);
        // Should have transitioned to SubmitGuessPhase
        Assert.assertTrue(game.getCurrentPhase() instanceof SubmitGuessPhase);
    }

    // >--------------------{ computeWinners }--------------------<

    @Test
    public void testComputeWinnersPicksHighestScore() {
        List<BTPlayer> players = game.getBTPlayers();
        players.get(0).addScore(100);
        players.get(1).addScore(200);
        players.get(2).addScore(150);
        game.computeWinners();
        Assert.assertEquals(game.getWinners().size(), 1);
        Assert.assertEquals(game.getWinners().get(0), players.get(1));
    }

    @Test
    public void testComputeWinnersHandlesTie() {
        List<BTPlayer> players = game.getBTPlayers();
        players.get(0).addScore(200);
        players.get(1).addScore(200);
        players.get(2).addScore(100);
        game.computeWinners();
        Assert.assertEquals(game.getWinners().size(), 2);
        Assert.assertTrue(game.getWinners().containsAll(List.of(players.get(0), players.get(1))));
    }

    @Test
    public void testWinnersEmptyBeforeCompute() {
        Assert.assertTrue(game.getWinners().isEmpty());
    }
}
