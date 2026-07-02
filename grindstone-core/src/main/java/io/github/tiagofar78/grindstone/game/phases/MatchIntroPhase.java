package io.github.tiagofar78.grindstone.game.phases;

import io.github.tiagofar78.grindstone.game.Game;

public class MatchIntroPhase extends Phase {

    public MatchIntroPhase(Game game) {
        super(game);
    }

    @Override
    public Phase next() {
        return getGame().getFirstPhase();
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
        Game game = getGame();
        game.runMatchIntro();
    }

}
