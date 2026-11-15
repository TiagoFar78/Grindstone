package io.github.tiagofar78.grindstone.game.phases;

import java.time.Duration;

import io.github.tiagofar78.grindstone.game.Game;

public class FinishedPhase<G extends Game<?, ?, ?>> extends Phase<G> {

    private static final int DEFAULT_DURATION_SECONDS = 15;

    public FinishedPhase(G game) {
        super(game);
    }

    public Duration duration() {
        return Duration.ofSeconds(DEFAULT_DURATION_SECONDS);
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
        game.sendGameOverMessages();
        game.archive();
        game.getDependencies().getSchedulerService().runAfter(duration(), () -> game.startNextPhase());
    }
}
