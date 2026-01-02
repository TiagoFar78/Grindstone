package io.github.tiagofar78.grindstone;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Grindstone extends JavaPlugin {

    private static Grindstone instance;

    public static Grindstone getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = (Grindstone) Bukkit.getServer().getPluginManager().getPlugin("TF_Grindstone");
    }

}
