package io.github.tiagofar78.grindstone;

import io.github.tiagofar78.grindstone.commands.ForceStopCommand;
import io.github.tiagofar78.grindstone.commands.LeaveQueueCommand;
import io.github.tiagofar78.grindstone.listener.PlayerConnectionListener;
import io.github.tiagofar78.grindstone.party.PartyService;
import io.github.tiagofar78.messagesrepo.MessagesRepo;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Grindstone extends JavaPlugin {

    public static final String ADMIN_PERMISSION = "Grindstone.Admin";

    private static final String LEAVE_QUEUE_COMMAND_LABEL = "dequeue";
    private static final String FORCE_STOP_COMMAND_LABEL = "forcestop";

    private static Grindstone instance;

    public static Grindstone getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }

        MessagesRepo.getTranslations().register(this.getClassLoader(), "/lang");

        instance = (Grindstone) Bukkit.getServer().getPluginManager().getPlugin("TF_Grindstone");

        registerCommand(LEAVE_QUEUE_COMMAND_LABEL, new LeaveQueueCommand());
        registerCommand(FORCE_STOP_COMMAND_LABEL, new ForceStopCommand());

        GrindstoneConfig.load();

        registerListener(new PlayerConnectionListener());
        PartyService.registerFallbackProviderListener();
    }

    public static void registerListener(Listener listener) {
        instance.getServer().getPluginManager().registerEvents(listener, instance);
    }

    public static void registerCommand(String label, CommandExecutor executor) {
        instance.getCommand(label).setExecutor(executor);
    }

}
