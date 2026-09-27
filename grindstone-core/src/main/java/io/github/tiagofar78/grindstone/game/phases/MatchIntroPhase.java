package io.github.tiagofar78.grindstone.game.phases;

import io.github.tiagofar78.grindstone.game.Game;

public class MatchIntroPhase<G extends Game<?, ?, ?>> extends Phase<G> {

    public MatchIntroPhase(G game) {
        super(game);
    }

    @Override
    public Phase<G> next() {
        return (Phase<G>) getGame().getFirstPhase();
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
        G game = getGame();
        game.runMatchIntro();
    }

}
