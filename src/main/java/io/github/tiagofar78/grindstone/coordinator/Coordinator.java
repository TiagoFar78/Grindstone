package io.github.tiagofar78.grindstone.coordinator;

import io.github.tiagofar78.grindstone.Grindstone;
import io.github.tiagofar78.grindstone.game.Minigame;
import io.github.tiagofar78.grindstone.game.MinigameMap;
import io.github.tiagofar78.grindstone.game.MinigamePlayer;
import io.github.tiagofar78.grindstone.game.MinigameSettings;
import io.github.tiagofar78.grindstone.game.MinigameTeam;

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
        if (commandLabel.contains(" ")) {
            throw new IllegalArgumentException(
                    "Could not register gamemode with command label \"" + commandLabel + "\". A command cannot contain spaces."
            );
        }

        Grindstone instance = Grindstone.getInstance();
        MatchmakingQueue queue = new MatchmakingQueue(factory, gamemode, availableMaps);
        instance.getCommand(commandLabel).setExecutor(null); // TODO

        for (MinigameMap map : availableMaps) {
            instance.getCommand(commandLabel + "_" + map.getName().toLowerCase()).setExecutor(null); // TODO
        }
    }

    private static Map<String, Minigame> playerMinigame = new HashMap<>();

    public static void createMinigame(
            MinigameFactory minigameFactory,
            MinigameSettings settings,
            MinigameMap map,
            List<List<String>> parties
    ) {
        Minigame minigame = minigameFactory.create(map, settings, parties);

        for (List<String> party : parties) {
            for (String playerName : party) {
                playerMinigame.put(playerName, minigame);
            }
        }
    }

    public static void removeGame(Minigame minigame) {
        for (MinigameTeam<? extends MinigamePlayer> team : minigame.getTeams()) {
            for (MinigamePlayer player : team.getMembers()) {
                playerMinigame.remove(player.getName());
            }
        }
    }

}
