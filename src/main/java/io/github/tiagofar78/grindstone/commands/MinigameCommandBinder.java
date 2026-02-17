package io.github.tiagofar78.grindstone.commands;

import io.github.tiagofar78.grindstone.Grindstone;
import io.github.tiagofar78.grindstone.game.MapFactory;
import io.github.tiagofar78.grindstone.game.MinigameFactory;
import io.github.tiagofar78.grindstone.game.MinigameSettings;
import io.github.tiagofar78.grindstone.queue.LobbyBasedQueue;
import io.github.tiagofar78.grindstone.queue.MatchmakingQueue;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class MinigameCommandBinder {

    public static void bindQueueCommands(
            JavaPlugin plugin,
            MinigameFactory factory,
            MinigameSettings gamemode,
            List<MapFactory> availableMaps,
            String commandLabel
    ) {
        MatchmakingQueue queue = new LobbyBasedQueue(factory, gamemode, availableMaps);
        bindQueueCommands(plugin, availableMaps, commandLabel, queue);
    }

    public static void bindQueueCommands(
            JavaPlugin plugin,
            List<MapFactory> availableMaps,
            String commandLabel,
            MatchmakingQueue queue
    ) {
        if (commandLabel.contains(" ") || commandLabel.isEmpty()) {
            throw new IllegalArgumentException(
                    "Could not register gamemode with command label \"" + commandLabel + "\". A command cannot contain spaces."
            );
        }

        Grindstone.registerCommand(plugin, commandLabel, new JoinQueueCommand(queue));

        for (MapFactory map : availableMaps) {
            String label = commandLabel + "_" + map.getName().toLowerCase();
            Grindstone.registerCommand(plugin, label, new JoinQueueCommand(queue, map));
        }
    }

    public static void bindForceStartCommands(
            JavaPlugin plugin,
            MinigameFactory factory,
            MinigameSettings gamemode,
            List<MapFactory> availableMaps,
            String commandLabel
    ) {
        commandLabel = ForceStartCommand.COMMAND_LABEL + "_" + commandLabel;
        Grindstone.registerCommand(plugin, commandLabel, new ForceStartCommand(factory, gamemode, availableMaps));

        for (MapFactory map : availableMaps) {
            String label = commandLabel + "_" + map.getName().toLowerCase();
            Grindstone.registerCommand(plugin, label, new ForceStartCommand(factory, gamemode, map));
        }
    }

    public static void bindPasteMapsCommand(
            JavaPlugin plugin,
            File pluginFolder,
            String gamemodeName,
            List<MapFactory> maps,
            String worldName
    ) {
        String commandLabel = PasteMapsCommand.COMMAND_LABEL + "_" + gamemodeName;
        Grindstone.registerCommand(
                plugin,
                commandLabel,
                new PasteMapsCommand(pluginFolder, gamemodeName, maps, worldName)
        );
    }

}
