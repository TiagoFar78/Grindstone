package io.github.tiagofar78.grindstone.coordinator;

import io.github.tiagofar78.grindstone.game.Minigame;
import io.github.tiagofar78.grindstone.game.MinigameMap;
import io.github.tiagofar78.grindstone.game.MinigamePlayer;
import io.github.tiagofar78.grindstone.game.MinigameSettings;
import io.github.tiagofar78.grindstone.game.MinigameTeam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Coordinator {

    private static MinigameRegistry minigamesRegistry = new MinigameRegistry();

    public static MinigameRegistry getRegistry() {
        return minigamesRegistry;
    }

    private static Map<String, Minigame> playerMinigame = new HashMap<>();

    public static void initMinigame(
            String minigameId,
            String gamemodeId,
            String mapId,
            List<List<? extends MinigamePlayer>> parties
    ) {
        MinigameSettings settings = minigamesRegistry.GAMEMODES.get(gamemodeId);
        MinigameMap map = minigamesRegistry.MAPS.get(mapId);
        Minigame minigame = minigamesRegistry.MINIGAMES.get(minigameId).create(map, settings, parties);

        for (List<? extends MinigamePlayer> party : parties) {
            for (MinigamePlayer player : party) {
                playerMinigame.put(player.getName(), minigame);
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
