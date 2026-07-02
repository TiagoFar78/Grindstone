package io.github.tiagofar78.grindstone.gameengine.bukkit;

public final class GamesManager {
    
    /*

    public static final int SIMULTANEOUS_GAMES = 10;
    public static final int MAPS_DISTANCE = 1000;
    public static final int MAPS_Y_CORD = 100;

    private GamesManager() {
    }

    private static Game[] gamesRunning = new Game[SIMULTANEOUS_GAMES];
    private static Map<String, Game> playerGame = new HashMap<>();

    public static boolean createGame(
            GameFactory GameFactory,
            GameSettings settings,
            MapFactory map,
            List<Collection<String>> parties
    ) {
        return createGame(GameFactory, settings, map, parties, false);
    }

    public static boolean createGame(
            GameFactory GameFactory,
            GameSettings settings,
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
        Game Game = GameFactory.create(map.create(referenceBlock), settings, parties, keepTeams);
        gamesRunning[gameIndex] = Game;

        for (Collection<String> party : parties) {
            for (String playerName : party) {
                playerGame.put(playerName, Game);
            }
        }

        return true;
    }

    public static void removeGame(Game Game) {
        for (int i = 0; i < SIMULTANEOUS_GAMES; i++) {
            if (gamesRunning[i] == Game) {
                gamesRunning[i] = null;
                break;
            }
        }

        for (GameTeam<? extends GamePlayer> team : Game.getTeams()) {
            for (GamePlayer player : team.getMembers()) {
                playerGame.remove(player.getName());
            }
        }
    }

    public static Set<Game> getUniqueGames() {
        return new HashSet<>(playerGame.values());
    }

    // Player Related

    public static Game getGame(String playerName) {
        return playerGame.get(playerName);
    }

    public static <T extends Game> T getGame(String playerName, Class<T> type) {
        Game game = getGame(playerName);
        if (type.isInstance(game)) {
            return type.cast(game);
        }

        return null;
    }

    public static boolean isInGame(String playerName) {
        Game game = playerGame.get(playerName);
        return game != null && game.isInLobby(playerName);
    }

    public static boolean isInOngoingGame(String playerName) {
        Game game = playerGame.get(playerName);
        return game != null && game.isInLobby(playerName) && !game.getCurrentPhase().hasGameEnded();
    }

    public static boolean leftOngoingGame(String playerName) {
        Game game = playerGame.get(playerName);
        return game != null && !game.isInLobby(playerName) && !game.getCurrentPhase().hasGameEnded();
    }

    // Forcestart Related

    public static ForcestartResult forceStart(
            GameFactory factory,
            GameSettings settings,
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

        createGame(factory, settings, map, teams, true);
        return ForcestartResult.SUCCESS;
    }

    private static int totalPlayers(List<Collection<String>> teams) {
        int sum = 0;
        for (Collection<String> team : teams) {
            sum += team.size();
        }

        return sum;
    }
    
    */

}
