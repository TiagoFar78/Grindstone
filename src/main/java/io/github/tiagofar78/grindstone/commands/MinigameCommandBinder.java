package io.github.tiagofar78.grindstone.commands;

import io.github.tiagofar78.grindstone.Grindstone;
import io.github.tiagofar78.grindstone.game.MinigameFactory;
import io.github.tiagofar78.grindstone.game.MinigameMap;
import io.github.tiagofar78.grindstone.game.MinigameSettings;
import io.github.tiagofar78.grindstone.queue.LobbyBasedQueue;
import io.github.tiagofar78.grindstone.queue.MatchmakingQueue;

import java.util.List;

public class MinigameCommandBinder {

    public static void bindQueueCommands(
            MinigameFactory factory,
            MinigameSettings gamemode,
            List<MinigameMap> availableMaps,
            String commandLabel
    ) {
        MatchmakingQueue queue = new LobbyBasedQueue(factory, gamemode, availableMaps);
        bindQueueCommands(availableMaps, commandLabel, queue);
    }

    public static void bindQueueCommands(List<MinigameMap> availableMaps, String commandLabel, MatchmakingQueue queue) {
        if (commandLabel.contains(" ") || commandLabel.isEmpty()) {
            throw new IllegalArgumentException(
                    "Could not register gamemode with command label \"" + commandLabel + "\". A command cannot contain spaces."
            );
        }

        Grindstone.registerCommand(commandLabel, new JoinQueueCommand(queue));

        for (MinigameMap map : availableMaps) {
            String label = commandLabel + "_" + map.getName().toLowerCase();
            Grindstone.registerCommand(label, new JoinQueueCommand(queue, map));
        }
    }

    public static void bindForceStartCommands(
            MinigameFactory factory,
            MinigameSettings gamemode,
            List<MinigameMap> availableMaps,
            String commandLabel
    ) {
        commandLabel = ForceStartCommand.FORCE_START_COMMAND_LABEL + "_" + commandLabel;
        Grindstone.registerCommand(commandLabel, new ForceStartCommand(factory, gamemode, availableMaps));

        for (MinigameMap map : availableMaps) {
            String label = commandLabel + "_" + map.getName().toLowerCase();
            Grindstone.registerCommand(label, new ForceStartCommand(factory, gamemode, map));
        }

    }

}
