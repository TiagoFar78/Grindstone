package io.github.tiagofar78.grindstone.game.phases;

import io.github.tiagofar78.grindstone.game.Game;

public class FinishedPhase<G extends Game<?, ?, ?>> extends Phase<G> {

    public FinishedPhase(G game) {
        super(game);
    }

    @Override
    public Phase<G> next() {
        return new DisabledPhase<G>(getGame());
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
        G game = getGame();
        game.runGameOver();
        game.archive();
    }
}
