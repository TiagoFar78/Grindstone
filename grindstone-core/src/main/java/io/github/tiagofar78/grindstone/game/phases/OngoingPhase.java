package io.github.tiagofar78.grindstone.game.phases;

import io.github.tiagofar78.grindstone.game.Game;

public abstract class OngoingPhase<G extends Game<?, ?, ?>> extends Phase<G> {

    public OngoingPhase(G game) {
        super(game);
    }

    @Override
    public Phase<G> next() {
        return new FinishedPhase<G>(getGame());
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
        return true;
    }

    @Override
    public boolean isGameDisabled() {
        return false;
    }

}
