package io.github.tiagofar78.grindstone;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;


public class GrindstoneResources {

    //  ########################################
    //  #                Config                #
    //  ########################################

    public static YamlConfiguration getYamlConfiguration() {
        return YamlConfiguration.loadConfiguration(configFile());
    }

    private static File configFile() {
        return new File(Grindstone.getInstance().getDataFolder(), "config.yml");
    }

}
