package io.github.tiagofar78.grindstone.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class GamesManager {

    public static final int SIMULTANEOUS_GAMES = 10;
    public static final int MAPS_DISTANCE = 1000;
    public static final int MAPS_Y_CORD = 100;

    private GamesManager() {
    }

    private static Minigame[] gamesRunning = new Minigame[SIMULTANEOUS_GAMES];
    private static Map<String, Minigame> playerMinigame = new HashMap<>();

    public static boolean createMinigame(
            MinigameFactory minigameFactory,
            MinigameSettings settings,
            MapFactory map,
            List<Collection<String>> parties
    ) {
        return createMinigame(minigameFactory, settings, map, parties, false);
    }

    public static boolean createMinigame(
            MinigameFactory minigameFactory,
            MinigameSettings settings,
            MapFactory map,
            List<Collection<String>> parties,
            boolean keepTeams
    ) {
        int gameIndex = -1;
        for (int i = 0; i < SIMULTANEOUS_GAMES; i++) {
            if (gamesRunning[i] == null) {
                gameIndex = i;
                break;
            }
        }

        if (gameIndex == -1) {
            return false;
        }

        int x = map.indexInWorld() * MAPS_DISTANCE;
        int z = gameIndex * MAPS_DISTANCE;
        Location referenceBlock = new Location(Bukkit.getWorld(map.getWorldName()), x, MAPS_Y_CORD, z);
        Minigame minigame = minigameFactory.create(map.create(referenceBlock), settings, parties, keepTeams);
        gamesRunning[gameIndex] = minigame;

        for (Collection<String> party : parties) {
            for (String playerName : party) {
                playerMinigame.put(playerName, minigame);
            }
        }

        return true;
    }

    public static void removeGame(Minigame minigame) {
        for (int i = 0; i < SIMULTANEOUS_GAMES; i++) {
            if (gamesRunning[i] == minigame) {
                gamesRunning[i] = null;
                break;
            }
        }

        for (MinigameTeam<? extends MinigamePlayer> team : minigame.getTeams()) {
            for (MinigamePlayer player : team.getMembers()) {
                playerMinigame.remove(player.getName());
            }
        }
    }

    public static Set<Minigame> getUniqueMinigames() {
        return new HashSet<>(playerMinigame.values());
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

    // Forcestart Related

    public static ForcestartResult forceStart(
            MinigameFactory factory,
            MinigameSettings settings,
            MapFactory map,
            List<Collection<String>> teams
    ) {
        if (teams.size() > settings.maxTeams()) {
            return ForcestartResult.TOO_MANY_TEAMS;
        }

        if (teams.size() < settings.minTeams()) {
            return ForcestartResult.TOO_FEW_TEAMS;
        }

        int total = totalPlayers(teams);
        if (total < settings.minPlayers()) {
            return ForcestartResult.TOO_FEW_PLAYERS;
        }

        if (total > settings.maxPlayersPerTeam() * settings.maxTeams()) {
            return ForcestartResult.TOO_MANY_PLAYERS;
        }

        for (Collection<String> team : teams) {
            if (team.size() > settings.maxPlayersPerTeam()) {
                return ForcestartResult.TEAM_CAPACITY_EXCEEDED;
            }
        }

        if (!settings.doTeamsFit(teams)) {
            return ForcestartResult.INVALID_TEAMS_DISTRIBUTION;
        }

        createMinigame(factory, settings, map, teams, true);
        return ForcestartResult.SUCCESS;
    }

    private static int totalPlayers(List<Collection<String>> teams) {
        int sum = 0;
        for (Collection<String> team : teams) {
            sum += team.size();
        }

        return sum;
    }

}
