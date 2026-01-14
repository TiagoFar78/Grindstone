package io.github.tiagofar78.grindstone;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class GrindstoneConfig {

    private static YamlConfiguration getYamlConfiguration() {
        File configFile = new File(Grindstone.getInstance().getDataFolder(), "config.yml");
        return YamlConfiguration.loadConfiguration(configFile);
    }

    public static void load() {
        instance = new GrindstoneConfig();
    }

    private static GrindstoneConfig instance;

    public static GrindstoneConfig getInstance() {
        return instance;
    }

    public boolean canQueueAfterDisconnect;
    public int lobbyQueueMinPlayersCooldown;
    public int lobbyQueueHalfPlayersCooldown;
    public int lobbyQueueFullPlayersCooldown;
    public int gameCountdownAnnouncementsInterval;
    public int preparingPhaseDuration;
    public int finishedPhaseDuration;

    private GrindstoneConfig() {
        YamlConfiguration config = getYamlConfiguration();

        canQueueAfterDisconnect = config.getBoolean("CanQueueAfterDisconnect");
        lobbyQueueMinPlayersCooldown = config.getInt("LobbyQueueMinPlayersCooldown");
        lobbyQueueHalfPlayersCooldown = config.getInt("lobbyQueueHalfPlayersCooldown");
        lobbyQueueFullPlayersCooldown = config.getInt("LobbyQueueFullPlayersCooldown");
        gameCountdownAnnouncementsInterval = config.getInt("GameCountdownAnnouncementsInterval");
        preparingPhaseDuration = config.getInt("PreparingPhaseDuration");
        finishedPhaseDuration = config.getInt("FinishedPhaseDuration");
    }

}
