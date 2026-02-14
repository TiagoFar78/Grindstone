package io.github.tiagofar78.grindstone.game.phases;

import io.github.tiagofar78.grindstone.Grindstone;
import io.github.tiagofar78.grindstone.GrindstoneConfig;
import io.github.tiagofar78.grindstone.bukkit.BukkitPlayer;
import io.github.tiagofar78.grindstone.game.Minigame;
import io.github.tiagofar78.grindstone.game.MinigamePlayer;

import net.kyori.adventure.title.Title;

import org.bukkit.Bukkit;

import java.time.Duration;

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

        int preparingPhaseDuration = GrindstoneConfig.getInstance().preparingPhaseDuration;
        runTimer(preparingPhaseDuration);
    }

    private static final int TICKS_PER_SECOND = 20;

    private void runTimer(int secondsRemaining) {
        if (getGame().getCurrentPhase() != this) {
            return;
        }

        if (secondsRemaining == 0) {
            getGame().startNextPhase();
            return;
        }

        Title.Times times = Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO);
        for (MinigamePlayer player : getGame().getPlayersOnLobby()) {
            Object[] args = new Object[]{Integer.toString(secondsRemaining)};
            BukkitPlayer.sendTitleMessage(
                    player,
                    times,
                    "grindstone.preparing_phase.title",
                    args,
                    "grindstone.preparing_phase.subtitle",
                    args
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
