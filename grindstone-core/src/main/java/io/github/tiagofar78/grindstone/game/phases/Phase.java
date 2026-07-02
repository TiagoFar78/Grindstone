package io.github.tiagofar78.grindstone.game.phases;

import io.github.tiagofar78.grindstone.game.Game;

public abstract class Phase {

    private Game game;

    public Phase(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public abstract Phase next();

    public abstract boolean isClockStopped();

    public abstract boolean hasGameStarted();

    public abstract boolean hasGameEnded();

    public abstract boolean isGameDisabled();

    public abstract void start();

}
