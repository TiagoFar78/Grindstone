package io.github.tiagofar78.grindstone.game.phases;

import io.github.tiagofar78.grindstone.game.Minigame;

public class LoadingPhase extends Phase {

    public LoadingPhase(Minigame game) {
        super(game);
    }

    @Override
    public Phase next() {
        return getGame().newOngoingPhase();
    }

    @Override
    public boolean isClockStopped() {
        return true;
    }

    @Override
    public boolean hasGameStarted() {
        return false;
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
        getGame().sendLoadingMessage();
        getGame().load();
    }

}
