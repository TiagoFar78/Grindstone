package io.github.tiagofar78.grindstone;

import org.bukkit.configuration.file.YamlConfiguration;

public class GrindstoneConfig {

    private static GrindstoneConfig instance;

    public static GrindstoneConfig getInstance() {
        return instance;
    }

    public static boolean load() {
        instance = new GrindstoneConfig();
        return instance != null;
    }

    public boolean canQueueAfterDisconnect;
    public int lobbyQueueMinPlayersCooldown;
    public int lobbyQueueHalfPlayersCooldown;
    public int lobbyQueueFullPlayersCooldown;
    public int gameCountdownAnnouncementsInterval;
    public int preparingPhaseDuration;
    public int finishedPhaseDuration;

    private GrindstoneConfig() {
        YamlConfiguration config = GrindstoneResources.getYamlConfiguration();

        canQueueAfterDisconnect = config.getBoolean("CanQueueAfterDisconnect");
        lobbyQueueMinPlayersCooldown = config.getInt("LobbyQueueMinPlayersCooldown");
        lobbyQueueHalfPlayersCooldown = config.getInt("lobbyQueueHalfPlayersCooldown");
        lobbyQueueFullPlayersCooldown = config.getInt("LobbyQueueFullPlayersCooldown");
        gameCountdownAnnouncementsInterval = config.getInt("GameCountdownAnnouncementsInterval");
        preparingPhaseDuration = config.getInt("PreparingPhaseDuration");
        finishedPhaseDuration = config.getInt("FinishedPhaseDuration");
    }

}
