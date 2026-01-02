package io.github.tiagofar78.grindstone.game.phases;

import io.github.tiagofar78.grindstone.game.Minigame;

public abstract class Phase {

    private Minigame game;

    public Phase(Minigame game) {
        this.game = game;
    }

    public Minigame getGame() {
        return game;
    }

    public abstract Phase next();

    public abstract boolean isClockStopped();

    public abstract boolean hasGameStarted();

    public abstract boolean hasGameEnded();

    public abstract boolean isGameDisabled();

    public abstract void start();

}
