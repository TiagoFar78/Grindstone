package io.github.tiagofar78.grindstone.gameengine.bukkit;

public class BukkitPlayer {
    
    /*

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    public static Player getBukkitPlayer(GamePlayer player) {
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

//  ########################################
//  #               Messages               #
//  ########################################

    public static Component translateMessage(Locale locale, String key, Object... args) {
        String message = MessagesRepo.getTranslations().translate(locale, key);
        return formatMessage(message, args);
    }

    public static Component formatMessage(String message, Object... args) {
        String formatted = MessageFormat.format(message, args);
        return MINI.deserialize(formatted);
    }

    public static void sendTranslatedMessage(GamePlayer player, String message, Object... args) {
        Player bukkitPlayer = getBukkitPlayer(player);
        if (bukkitPlayer == null) {
            return;
        }

        bukkitPlayer.sendMessage(formatMessage(message, args));
    }

    public static void sendMessage(GamePlayer player, String key, Object... args) {
        Player bukkitPlayer = getBukkitPlayer(player);
        if (bukkitPlayer == null) {
            return;
        }

        bukkitPlayer.sendMessage(translateMessage(bukkitPlayer.locale(), key, args));
    }

    public static void sendMessage(Audience audience, Locale locale, String key, Object... args) {
        if (audience == null) {
            return;
        }

        audience.sendMessage(translateMessage(locale, key, args));
    }

    public static void sendTitleMessage(
            GamePlayer player,
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
            GamePlayer player,
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
        Component titleComponent = translateMessage(locale, titleKey, titleArgs);
        Component subtitleComponent = translateMessage(locale, subtitleKey, subtitleArgs);
        bukkitPlayer.showTitle(Title.title(titleComponent, subtitleComponent, times));
    }

    public static Locale getLocale(GamePlayer player) {
        Player bukkitPlayer = getBukkitPlayer(player);
        if (bukkitPlayer == null) {
            return null;
        }

        return bukkitPlayer.locale();
    }

    public static void teleport(GamePlayer player, Location location) {
        Player bukkitPlayer = getBukkitPlayer(player);
        if (bukkitPlayer == null) {
            return;
        }

        bukkitPlayer.teleport(location);
    }
    
    */

}
