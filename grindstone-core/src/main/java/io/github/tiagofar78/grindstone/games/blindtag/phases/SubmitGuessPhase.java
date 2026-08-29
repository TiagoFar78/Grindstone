package io.github.tiagofar78.grindstone.games.blindtag.phases;

import io.github.tiagofar78.grindstone.game.Game;
import io.github.tiagofar78.grindstone.game.phases.Phase;
import io.github.tiagofar78.grindstone.games.blindtag.map.BTMap;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

/**
 * Phase where each player submits their guessed {@link BTMap}.
 * Final scores are computed once all players have submitted.
 *
 * <p>Full implementation in Task 10.
 */
public class SubmitGuessPhase extends Phase {

    public SubmitGuessPhase(Game game) {
        super(game);
    }

    @Override
    public Phase next() {
        return new RevealPhase(getGame());
    }

    @Override
    public boolean isClockStopped() {
        return true;
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
        // Implemented in Task 10
    }

    /**
     * Accepts a player's map guess. Transitions to {@link RevealPhase} once all
     * players have submitted.
     *
     * <p>Implemented in Task 10.
     */
    public void submitGuess(BTPlayer player, BTMap guessedMap) {
        // Implemented in Task 10
    }
}
