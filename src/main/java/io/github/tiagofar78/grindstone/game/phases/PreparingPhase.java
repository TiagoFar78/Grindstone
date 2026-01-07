package io.github.tiagofar78.grindstone.game.phases;

import io.github.tiagofar78.grindstone.Grindstone;
import io.github.tiagofar78.grindstone.game.Minigame;
import io.github.tiagofar78.grindstone.game.MinigamePlayer;
import io.github.tiagofar78.grindstone.managers.ConfigManager;

import org.bukkit.Bukkit;

public class PreparingPhase extends Phase {

    public PreparingPhase(Minigame game) {
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
        getGame().sendGameExplanationMessage();

        int preparingPhaseDuration = ConfigManager.getInstance().preparingPhaseDuration();
        runTimer(preparingPhaseDuration);
    }

    private static final int TICKS_PER_SECOND = 20;
    private static final String LIGHT_RED_COLOR = "§c";

    private void runTimer(int secondsRemaining) {
        if (getGame().getCurrentPhase() != this) {
            return;
        }

        if (secondsRemaining == 0) {
            getGame().startNextPhase();
            return;
        }

        for (MinigamePlayer player : getGame().getPlayersOnLobby()) {
            player.getPlayerAdapter().sendTitleMessage(
                    LIGHT_RED_COLOR + Integer.toString(secondsRemaining),
                    "",
                    0,
                    1,
                    0
            );
        }

        Bukkit.getScheduler().runTaskLater(Grindstone.getInstance(), new Runnable() {

            @Override
            public void run() {
                runTimer(secondsRemaining - 1);
            }

        }, TICKS_PER_SECOND);
    }

}
