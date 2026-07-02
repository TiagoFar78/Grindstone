package io.github.tiagofar78.grindstone.game.phases;

import io.github.tiagofar78.grindstone.game.Game;

public class FinishedPhase extends Phase {

    public FinishedPhase(Game game) {
        super(game);
    }

    @Override
    public Phase next() {
        return new DisabledPhase(getGame());
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
        return true;
    }

    @Override
    public boolean isGameDisabled() {
        return false;
    }

    @Override
    public void start() {
        Game game = getGame();
        game.runGameOver();
        game.archive();
    }
}
