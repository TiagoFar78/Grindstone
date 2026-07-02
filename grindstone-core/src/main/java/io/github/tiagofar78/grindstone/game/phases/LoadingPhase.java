package io.github.tiagofar78.grindstone.game.phases;

import io.github.tiagofar78.grindstone.game.Game;

public class LoadingPhase extends Phase {

    public LoadingPhase(Game game) {
        super(game);
    }

    @Override
    public Phase next() {
        return new MatchIntroPhase(getGame());
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
        // TODO Maybe add something that waits for players to say that they have loaded as well

        getGame().startNextPhase();
    }

}
