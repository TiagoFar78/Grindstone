package io.github.tiagofar78.grindstone.game.phases;

import org.bukkit.Bukkit;

import io.github.tiagofar78.grindstone.Grindstone;
import io.github.tiagofar78.grindstone.game.Minigame;
import io.github.tiagofar78.grindstone.managers.ConfigManager;

public class FinishedPhase extends Phase {

    private static final int TICKS_PER_SECOND = 20;

    public FinishedPhase(Minigame game) {
        super(game);
    }

    @Override
    public Phase next() {
        return new DisabledPhase(getGame());
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
        getGame().sendVictoryMessage();

        int finishedPhaseDuration = ConfigManager.getInstance().finishedPhaseDuration();
        Bukkit.getScheduler().runTaskLater(Grindstone.getInstance(), new Runnable() {

            @Override
            public void run() {
                if (!getGame().getCurrentPhase().isGameDisabled()) {
                    getGame().startNextPhase();
                }
            }
        }, finishedPhaseDuration * TICKS_PER_SECOND);
    }
}

