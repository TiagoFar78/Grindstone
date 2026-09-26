package io.github.tiagofar78.grindstone.game.phases;

import io.github.tiagofar78.grindstone.game.Game;

public abstract class Phase<G extends Game<?, ?>> {

    private G game;

    public Phase(G game) {
        this.game = game;
    }

    public G getGame() {
        return game;
    }

    public abstract Phase<G> next();

    public abstract boolean isClockStopped();

    public abstract boolean hasGameStarted();

    public abstract boolean hasGameEnded();

    public abstract boolean isGameDisabled();

    public abstract void start();

}
