package io.github.tiagofar78.grindstone.bukkit;

import io.github.tiagofar78.grindstone.game.MinigamePlayer;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Locale;

public class BukkitPlayer {

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
        return getBukkitPlayer(playerName) != null;
    }

    public static void sendTranslatedMessage(MinigamePlayer player, String message, Object... args) {
        Player bukkitPlayer = getBukkitPlayer(player);
        if (bukkitPlayer == null) {
            return;
        }

        bukkitPlayer.sendMessage(Messages.format(message, args));
    }

    public static void sendMessage(MinigamePlayer player, String key, Object... args) {
        Player bukkitPlayer = getBukkitPlayer(player);
        if (bukkitPlayer == null) {
            return;
        }

        bukkitPlayer.sendMessage(Messages.translate(bukkitPlayer.locale(), key, args));
    }

    public static void sendMessage(Audience audience, Locale locale, String key, Object... args) {
        if (audience == null) {
            return;
        }

        audience.sendMessage(Messages.translate(locale, key, args));
    }

    public static void sendTitleMessage(
            MinigamePlayer player,
            String titleKey,
            Object[] titleArgs,
            String subtitleKey,
            Object[] subtitleArgs
    ) {
        Title.Times defaultTimes = Title.Times.times(
                Duration.ofSeconds(1),
                Duration.ofMillis(3500),
                Duration.ofSeconds(1)
        );
        sendTitleMessage(player, defaultTimes, titleKey, titleArgs, subtitleKey, subtitleArgs);
    }

    public static void sendTitleMessage(
            MinigamePlayer player,
            Title.Times times,
            String titleKey,
            Object[] titleArgs,
            String subtitleKey,
            Object[] subtitleArgs
    ) {
        Player bukkitPlayer = getBukkitPlayer(player);
        if (bukkitPlayer == null) {
            return;
        }

        Locale locale = bukkitPlayer.locale();
        Component titleComponent = Messages.translate(locale, titleKey, titleArgs);
        Component subtitleComponent = Messages.translate(locale, subtitleKey, subtitleArgs);
        bukkitPlayer.showTitle(Title.title(titleComponent, subtitleComponent, times));
    }

    public static void teleport(MinigamePlayer player, Location location) {
        Player bukkitPlayer = getBukkitPlayer(player);
        if (bukkitPlayer == null) {
            return;
        }

        bukkitPlayer.teleport(location);
    }

    public static Locale getLocale(MinigamePlayer player) {
        Player bukkitPlayer = getBukkitPlayer(player);
        if (bukkitPlayer == null) {
            return null;
        }

        return bukkitPlayer.locale();
    }

}
