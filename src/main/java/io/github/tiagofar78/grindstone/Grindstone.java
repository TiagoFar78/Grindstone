package io.github.tiagofar78.grindstone;

import io.github.tiagofar78.grindstone.commands.LeaveMatchmakingQueue;
import io.github.tiagofar78.grindstone.managers.ConfigManager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Grindstone extends JavaPlugin {

    public static final String LEAVE_QUEUE_COMMAND_LABEL = "dequeue";

    private static Grindstone instance;

    public static Grindstone getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }

        instance = (Grindstone) Bukkit.getServer().getPluginManager().getPlugin("TF_Grindstone");

        getCommand(LEAVE_QUEUE_COMMAND_LABEL).setExecutor(new LeaveMatchmakingQueue());

        loadManagers();
    }

    private void loadManagers() {
        if (!ConfigManager.load()) {
            Bukkit.getLogger().severe("[Grindstone] Could not load config");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

}
