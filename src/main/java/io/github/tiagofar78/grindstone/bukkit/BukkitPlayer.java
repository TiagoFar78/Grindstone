package io.github.tiagofar78.grindstone.bukkit;

import io.github.tiagofar78.grindstone.game.MinigamePlayer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BukkitPlayer {

    private static final int TICKS_PER_SECOND = 20;

    public static Player getBukkitPlayer(MinigamePlayer player) {
        return getBukkitPlayer(player.getName());
    }

    public static Player getBukkitPlayer(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null || !player.isOnline()) {
            return null;
        }

        return player;
    }

    public static boolean isOnline(String playerName) {
        return getBukkitPlayer(playerName) == null;
    }

    public static void sendTitleMessage(MinigamePlayer player, String titleMessage, String subtitleMessage) {
        int defaultFadeIn = 1 * TICKS_PER_SECOND;
        int defaultStay = (int) (3.5 * TICKS_PER_SECOND);
        int defaultFadeOut = 1 * TICKS_PER_SECOND;
        sendTitleMessage(player, titleMessage, subtitleMessage, defaultFadeIn, defaultStay, defaultFadeOut);
    }

    public static void sendTitleMessage(
            MinigamePlayer player,
            String title,
            String subtitle,
            double fadeIn,
            double stay,
            double fadeOut
    ) {
        Player bukkitPlayer = getBukkitPlayer(player);
        if (bukkitPlayer == null) {
            return;
        }

        fadeIn *= TICKS_PER_SECOND;
        stay *= TICKS_PER_SECOND;
        fadeOut *= TICKS_PER_SECOND;
        bukkitPlayer.sendTitle(title, subtitle, (int) fadeIn, (int) stay, (int) fadeOut);
    }

    public static void teleport(MinigamePlayer player, Location location) {
        Player bukkitPlayer = getBukkitPlayer(player);
        if (bukkitPlayer == null) {
            return;
        }

        bukkitPlayer.teleport(location);
    }

}
