package io.github.tiagofar78.grindstone.adapters;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitMinigamePlayerAdapter implements MinigamePlayerAdapter {

    private static final int TICKS_PER_SECOND = 20;

    private final String playerName;

    public BukkitMinigamePlayerAdapter(Player player) {
        this.playerName = player.getName();
    }

    public Player getBukkitPlayer() {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null || !player.isOnline()) {
            return null;
        }

        return player;
    }

    @Override
    public void sendTitleMessage(String titleMessage, String subtitleMessage) {
        int defaultFadeIn = 1 * TICKS_PER_SECOND;
        int defaultStay = (int) (3.5 * TICKS_PER_SECOND);
        int defaultFadeOut = 1 * TICKS_PER_SECOND;
        sendTitleMessage(titleMessage, subtitleMessage, defaultFadeIn, defaultStay, defaultFadeOut);
    }

    @Override
    public void sendTitleMessage(
            String titleMessage,
            String subtitleMessage,
            double fadeIn,
            double stay,
            double fadeOut
    ) {
        Player bukkitPlayer = getBukkitPlayer();
        if (bukkitPlayer == null) {
            return;
        }

        fadeIn *= TICKS_PER_SECOND;
        stay *= TICKS_PER_SECOND;
        fadeOut *= TICKS_PER_SECOND;
        bukkitPlayer.sendTitle(titleMessage, subtitleMessage, (int) fadeIn, (int) stay, (int) fadeOut);
    }

}
