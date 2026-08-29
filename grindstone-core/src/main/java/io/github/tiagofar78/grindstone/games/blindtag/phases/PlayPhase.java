package io.github.tiagofar78.grindstone.games.blindtag.phases;

import io.github.tiagofar78.grindstone.game.Game;
import io.github.tiagofar78.grindstone.game.phases.Phase;
import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.map.Direction;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

import java.util.List;

/**
 * The main gameplay phase for BlindTag.
 *
 * <p>Tracks whose turn it is (0-based index into {@link BlindTag#getBTPlayers()}) and how
 * many full rotations have completed. A rotation ends when every player has taken one turn.
 *
 * <p>At the end of each rotation:
 * <ul>
 *   <li>Non-tagger players receive {@link BlindTag#ROTATION_SCORE} points.</li>
 *   <li>If any player has reached {@link BlindTag#WIN_SCORE}, the game transitions to
 *       {@link SubmitGuessPhase}.</li>
 * </ul>
 *
 * <p>Move input is received via {@link #submitMove(BTPlayer, Direction)}, which is
 * wired to {@link io.github.tiagofar78.grindstone.games.blindtag.TurnEngine} in Task 11.
 */
public class PlayPhase extends Phase {

    private int turnIndex = 0;
    private int rotationCount = 0;
    private boolean extraMoveGranted = false;

    public PlayPhase(Game game) {
        super(game);
    }

    // >-----------------------{ Phase contract }-----------------------<

    @Override
    public Phase next() {
        return new SubmitGuessPhase(getGame());
    }

    @Override
    public boolean isClockStopped() {
        return false;
    }

    @Override
    public boolean hasGameStarted() {
        return true;
    }

    @Override
    public boolean hasGameEnded() {
        return false;
    }

    @Override
    public boolean isGameDisabled() {
        return false;
    }

    @Override
    public void start() {
        BlindTag game = (BlindTag) getGame();
        game.setActivePlayPhase(this);
        BTPlayer current = currentPlayer(game);
        game.onTurnStart(current);
    }

    // >-----------------------{ Turn control }-----------------------<

    /** Returns the player whose turn it currently is. */
    public BTPlayer currentPlayer(BlindTag game) {
        List<BTPlayer> players = game.getBTPlayers();
        return players.get(turnIndex % players.size());
    }

    /**
     * Advances to the next player's turn. If all players have taken a turn this rotation,
     * runs rotation-end scoring and checks for a winner.
     *
     * <p>Called by TurnEngine at the end of each turn (Task 11). Exposed here so tests
     * can drive the turn cycle directly.
     */
    public void endTurn() {
        BlindTag game = (BlindTag) getGame();
        List<BTPlayer> players = game.getBTPlayers();

        turnIndex++;

        // Rotation complete?
        if (turnIndex % players.size() == 0) {
            rotationCount++;
            awardRotationScore(game);
            game.onRotationEnd();

            if (hasWinner(game)) {
                game.startNextPhase(next()); // â†’ SubmitGuessPhase
                return;
            }
        }

        // Start next player's turn
        game.onTurnStart(currentPlayer(game));
    }

    private void awardRotationScore(BlindTag game) {
        BTPlayer tagger = game.getTagger();
        for (BTPlayer p : game.getBTPlayers()) {
            if (p != tagger) {
                p.addScore(BlindTag.ROTATION_SCORE);
            }
        }
    }

    private boolean hasWinner(BlindTag game) {
        for (BTPlayer p : game.getBTPlayers()) {
            if (p.getScore() >= BlindTag.WIN_SCORE) {
                return true;
            }
        }
        return false;
    }

    // >-----------------------{ Extra move flag }-----------------------<

    public boolean isExtraMoveGranted() {
        return extraMoveGranted;
    }

    public void setExtraMoveGranted(boolean extraMoveGranted) {
        this.extraMoveGranted = extraMoveGranted;
    }

    // >-----------------------{ Accessors }-----------------------<

    public int getTurnIndex() {
        return turnIndex;
    }

    public int getRotationCount() {
        return rotationCount;
    }

    // >-----------------------{ Move input }-----------------------<

    /**
     * Entry point for a player's move input.
     * Delegates to {@link io.github.tiagofar78.grindstone.games.blindtag.TurnEngine}
     * in Task 11.
     */
    public void submitMove(BTPlayer player, Direction direction) {
        // Implemented in Task 11
    }
}
