package io.github.tiagofar78.grindstone;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

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

    public final boolean canQueueAfterDisconnect;
    public final int lobbyQueueMinPlayersCooldown;
    public final int lobbyQueueHalfPlayersCooldown;
    public final int lobbyQueueFullPlayersCooldown;
    public final int gameCountdownAnnouncementsInterval;
    public final int preparingPhaseDuration;
    public final int finishedPhaseDuration;

    public final List<String> gameSummary;
    public final int gameExplanationWidth;
    public final String gameExplanationDivisor;
    public final String gameExplanationDivisorColor;
    public final String gameExplanationColor;
    public final String gameExplanationGameColor;

    private GrindstoneConfig() {
        YamlConfiguration config = getYamlConfiguration();

        canQueueAfterDisconnect = config.getBoolean("CanQueueAfterDisconnect");
        lobbyQueueMinPlayersCooldown = config.getInt("LobbyQueueMinPlayersCooldown");
        lobbyQueueHalfPlayersCooldown = config.getInt("lobbyQueueHalfPlayersCooldown");
        lobbyQueueFullPlayersCooldown = config.getInt("LobbyQueueFullPlayersCooldown");
        gameCountdownAnnouncementsInterval = config.getInt("GameCountdownAnnouncementsInterval");
        preparingPhaseDuration = config.getInt("PreparingPhaseDuration");
        finishedPhaseDuration = config.getInt("FinishedPhaseDuration");

        gameSummary = config.getStringList("GameSummary");
        gameExplanationWidth = config.getInt("GameExplanation.Width");
        gameExplanationDivisor = config.getString("GameExplanation.Divisor");
        gameExplanationDivisorColor = config.getString("GameExplanation.DivisorColor");
        gameExplanationColor = config.getString("GameExplanation.DescriptionColor");
        gameExplanationGameColor = config.getString("GameExplanation.GameColor");
    }

}
