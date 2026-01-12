package io.github.tiagofar78.grindstone.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GamesManager {

    private GamesManager() {
    }

    private static Map<String, Minigame> playerMinigame = new HashMap<>();

    public static void createMinigame(
            MinigameFactory minigameFactory,
            MinigameSettings settings,
            MinigameMap map,
            List<Collection<String>> parties
    ) {
        // TODO set referenceBlock of map
        Minigame minigame = minigameFactory.create(map, settings, parties);

        for (Collection<String> party : parties) {
            for (String playerName : party) {
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

    // Player Related

    public static Minigame getGame(String playerName) {
        return playerMinigame.get(playerName);
    }

    public static boolean isInGame(String playerName) {
        Minigame game = playerMinigame.get(playerName);
        return game != null && game.isInLobby(playerName);
    }

    public static boolean isInOngoingGame(String playerName) {
        Minigame game = playerMinigame.get(playerName);
        return game != null && game.isInLobby(playerName) && !game.getCurrentPhase().hasGameEnded();
    }

    public static boolean leftOngoingGame(String playerName) {
        Minigame game = playerMinigame.get(playerName);
        return game != null && !game.isInLobby(playerName) && !game.getCurrentPhase().hasGameEnded();
    }

}
