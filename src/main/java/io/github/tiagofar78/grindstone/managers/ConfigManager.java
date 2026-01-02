package io.github.tiagofar78.grindstone.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import io.github.tiagofar78.grindstone.GrindstoneResources;

public class ConfigManager {
    
    private static ConfigManager instance;

    public static ConfigManager getInstance() {
        return instance;
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
