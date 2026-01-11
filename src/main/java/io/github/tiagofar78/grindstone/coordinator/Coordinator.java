package io.github.tiagofar78.grindstone.coordinator;

import io.github.tiagofar78.grindstone.Grindstone;
import io.github.tiagofar78.grindstone.commands.JoinMatchmakingQueue;
import io.github.tiagofar78.grindstone.game.Minigame;
import io.github.tiagofar78.grindstone.game.MinigameMap;
import io.github.tiagofar78.grindstone.game.MinigamePlayer;
import io.github.tiagofar78.grindstone.game.MinigameSettings;
import io.github.tiagofar78.grindstone.game.MinigameTeam;
import io.github.tiagofar78.grindstone.party.Party;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Coordinator {

    public static void registerGamemode(
            MinigameFactory factory,
            MinigameSettings gamemode,
            List<MinigameMap> availableMaps,
            String commandLabel
    ) {
        if (commandLabel.contains(" ") || commandLabel.isEmpty()) {
            throw new IllegalArgumentException(
                    "Could not register gamemode with command label \"" + commandLabel + "\". A command cannot contain spaces."
            );
        }

        Grindstone instance = Grindstone.getInstance();
        MatchmakingQueue queue = new MatchmakingQueue(factory, gamemode, availableMaps);
        instance.getCommand(commandLabel).setExecutor(new JoinMatchmakingQueue(queue));

        for (MinigameMap map : availableMaps) {
            String label = commandLabel + "_" + map.getName().toLowerCase();
            instance.getCommand(label).setExecutor(new JoinMatchmakingQueue(queue, map));
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static Map<Party, MatchmakingQueue> partyQueue = new HashMap<>();

    public static boolean isInQueue(Party party) {
        return partyQueue.containsKey(party);
    }

    public static void enqueue(Party party, MatchmakingQueue queue, MinigameMap map) {
        partyQueue.put(party, queue);
        queue.enqueue(party, map);
    }

    public static void dequeue(Party party) {
        MatchmakingQueue queue = partyQueue.remove(party);
        queue.dequeue(party);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static Map<String, Minigame> playerMinigame = new HashMap<>();

    public static void createMinigame(
            MinigameFactory minigameFactory,
            MinigameSettings settings,
            MinigameMap map,
            List<Party> parties
    ) {
        Minigame minigame = minigameFactory.create(map, settings, parties);

        for (Party party : parties) {
            for (String playerName : party.getMembers()) {
                playerMinigame.put(playerName, minigame);
            }
        }
    }

    public static void removeGame(Minigame minigame) {
        for (MinigameTeam<MinigamePlayer> team : minigame.getTeams()) {
            for (MinigamePlayer player : team.getMembers()) {
                playerMinigame.remove(player.getName());
            }
        }
    }

}
