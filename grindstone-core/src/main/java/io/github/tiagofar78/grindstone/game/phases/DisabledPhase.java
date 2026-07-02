package io.github.tiagofar78.grindstone.game.phases;

import io.github.tiagofar78.grindstone.game.Game;

public class DisabledPhase extends Phase {

    public DisabledPhase(Game game) {
        super(game);
    }

    @Override
    public Phase next() {
        return null;
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
        return true;
    }

    @Override
    public void start() {
        getGame().disable();
    }

}
