package io.github.tiagofar78.grindstone;

import io.github.tiagofar78.grindstone.commands.LeaveMatchmakingQueue;
import io.github.tiagofar78.grindstone.party.PartyService;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
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

        registerCommand(LEAVE_QUEUE_COMMAND_LABEL, new LeaveMatchmakingQueue());

        GrindstoneConfig.load();

        PartyService.registerFallbackProviderListener();
    }

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, instance);
    }

    public void registerCommand(String label, CommandExecutor executor) {
        getCommand(label).setExecutor(executor);
    }

}
