package io.github.tiagofar78.grindstone.commands;

import io.github.tiagofar78.grindstone.Grindstone;
import io.github.tiagofar78.grindstone.game.MapFactory;
import io.github.tiagofar78.grindstone.game.MinigameFactory;
import io.github.tiagofar78.grindstone.game.MinigameSettings;
import io.github.tiagofar78.grindstone.queue.LobbyBasedQueue;
import io.github.tiagofar78.grindstone.queue.MatchmakingQueue;

import java.io.File;
import java.util.List;

public class MinigameCommandBinder {

    public static void bindQueueCommands(
            MinigameFactory factory,
            MinigameSettings gamemode,
            List<MapFactory> availableMaps,
            String commandLabel
    ) {
        MatchmakingQueue queue = new LobbyBasedQueue(factory, gamemode, availableMaps);
        bindQueueCommands(availableMaps, commandLabel, queue);
    }

    public static void bindQueueCommands(List<MapFactory> availableMaps, String commandLabel, MatchmakingQueue queue) {
        if (commandLabel.contains(" ") || commandLabel.isEmpty()) {
            throw new IllegalArgumentException(
                    "Could not register gamemode with command label \"" + commandLabel + "\". A command cannot contain spaces."
            );
        }

        Grindstone.registerCommand(commandLabel, new JoinQueueCommand(queue));

        for (MapFactory map : availableMaps) {
            String label = commandLabel + "_" + map.getName().toLowerCase();
            Grindstone.registerCommand(label, new JoinQueueCommand(queue, map));
        }
    }

    public static void bindForceStartCommands(
            MinigameFactory factory,
            MinigameSettings gamemode,
            List<MapFactory> availableMaps,
            String commandLabel
    ) {
        commandLabel = ForceStartCommand.COMMAND_LABEL + "_" + commandLabel;
        Grindstone.registerCommand(commandLabel, new ForceStartCommand(factory, gamemode, availableMaps));

        for (MapFactory map : availableMaps) {
            String label = commandLabel + "_" + map.getName().toLowerCase();
            Grindstone.registerCommand(label, new ForceStartCommand(factory, gamemode, map));
        }
    }

    public static void bindPasteMapsCommand(
            File pluginFolder,
            String gamemodeName,
            List<MapFactory> maps,
            String worldName
    ) {
        String commandLabel = PasteMapsCommand.COMMAND_LABEL + "_" + gamemodeName;
        Grindstone.registerCommand(commandLabel, new PasteMapsCommand(pluginFolder, gamemodeName, maps, worldName));
    }

}
