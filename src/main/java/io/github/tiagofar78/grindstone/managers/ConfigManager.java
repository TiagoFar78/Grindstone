package io.github.tiagofar78.grindstone.managers;

import io.github.tiagofar78.grindstone.GrindstoneResources;

import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {

    private static ConfigManager instance;

    public static ConfigManager getInstance() {
        return instance;
    }

    public static boolean load() {
        instance = new ConfigManager();
        return instance != null;
    }

    private int _finishedPhaseDuration;

    public ConfigManager() {
        YamlConfiguration config = GrindstoneResources.getYamlConfiguration();

        _finishedPhaseDuration = config.getInt("FinishedPhaseDuration");
    }

    public int finishedPhaseDuration() {
        return _finishedPhaseDuration;
    }

}
