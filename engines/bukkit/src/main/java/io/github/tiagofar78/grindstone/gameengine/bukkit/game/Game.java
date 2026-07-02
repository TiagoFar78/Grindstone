package io.github.tiagofar78.grindstone.gameengine.bukkit.game;

public abstract class Game {
    
    /*

    private GameMap map;
    private GameSettings settings;

    private List<GamePlayer> playersOnLobby;
    private List<GameTeam<? extends GamePlayer>> teams;

    private Phase _phase;

    public Game(GameMap map, GameSettings settings, List<Collection<String>> parties) {
        this(map, settings, parties, false);
    }

    public Game(GameMap map, GameSettings settings, List<Collection<String>> parties, boolean keepTeams) {
        List<List<GamePlayer>> players = toGamePlayer(parties);

        this.map = map;
        this.settings = settings;

        playersOnLobby = new ArrayList<>();
        addToLobby(players);
        teams = keepTeams ? partiesToTeams(players) : distributeParties(players);

        startNextPhase(new LoadingPhase(this));
    }

    public GameMap getMap() {
        return map;
    }

    public GameSettings getSettings() {
        return settings;
    }

    public List<GameTeam<? extends GamePlayer>> getTeams() {
        return teams;
    }

    private List<GameTeam<? extends GamePlayer>> partiesToTeams(List<List<GamePlayer>> parties) {
        List<GameTeam<? extends GamePlayer>> teams = new ArrayList<>();
        for (List<GamePlayer> party : parties) {
            teams.add(createTeam(party));
        }

        return teams;
    }

//  #########################################
//  #                 Lobby                 #
//  #########################################

    public void addToLobby(List<List<GamePlayer>> players) {
        for (List<GamePlayer> parties : players) {
            for (GamePlayer player : parties) {
                playersOnLobby.add(player);
            }
        }
    }

    public List<GamePlayer> getPlayersOnLobby() {
        return playersOnLobby;
    }

    public GamePlayer getPlayer(String playerName) {
        for (int i = 0; i < playersOnLobby.size(); i++) {
            if (playersOnLobby.get(i).getName().equals(playerName)) {
                return playersOnLobby.get(i);
            }
        }

        return null;
    }

    public boolean isInLobby(String playerName) {
        return getPlayer(playerName) != null;
    }

    public abstract void addPlayerToGame(GamePlayer player);

    public void playerRejoin(String playerName) {
        GamePlayer player = getPlayer(playerName);
        addPlayerToGame(player);
        playersOnLobby.add(player);
        sendPlayerRejoinMessage(playerName);
    }

    public abstract void removePlayerFromGame(GamePlayer player);

    public void playerLeft(String playerName) {
        GamePlayer player = getPlayer(playerName);
        removePlayerFromGame(player);
        playersOnLobby.remove(player);
        sendPlayerLeftMessage(playerName);
    }

    private List<List<GamePlayer>> toGamePlayer(List<Collection<String>> parties) {
        List<List<GamePlayer>> newParties = new ArrayList<>();
        for (Collection<String> party : parties) {
            List<GamePlayer> newParty = new ArrayList<>();
            for (String member : party) {
                newParty.add(createPlayer(member));
            }
            newParties.add(newParty);
        }

        return newParties;
    }

    private List<GameTeam<? extends GamePlayer>> distributeParties(List<List<GamePlayer>> parties) {
        int maxTeams = getSettings().maxTeams();
        int playersPerTeam = getSettings().maxPlayersPerTeam();
        List<List<GamePlayer>> teamPlayers = TeamLayoutSolver.solve(parties, maxTeams, playersPerTeam);
        List<GameTeam<? extends GamePlayer>> teams = new ArrayList<>();
        for (List<GamePlayer> playersInTeam : teamPlayers) {
            teams.add(createTeam(playersInTeam));
        }

        return teams;
    }

    public int getTeamIndex(GamePlayer member) {
        for (int i = 0; i < teams.size(); i++) {
            if (teams.get(i).getMembers().contains(member)) {
                return i;
            }
        }

        return -1;
    }

    public GameTeam<? extends GamePlayer> getTeam(GamePlayer member) {
        return teams.get(getTeamIndex(member));
    }

    public void makeSpectator(GamePlayer player) {
        player.setSpectator(true);
    }

    public void teleportToPreparingRoom() {
        for (GamePlayer player : playersOnLobby) {
            BukkitPlayer.teleport(player, map.preparingRoomLocation());
        }
    }

//  ########################################
//  #              Admin zone              #
//  ########################################

    public void forceStop() {
        startNextPhase(new DisabledPhase(this));
    }

//  ########################################
//  #                Phases                #
//  ########################################

    public abstract void load();

    public abstract Phase newOngoingPhase();

    public abstract void disable();

    public Phase getCurrentPhase() {
        return this._phase;
    }

    public void startNextPhase() {
        startNextPhase(_phase.next());
    }

    public void startNextPhase(Phase phase) {
        _phase = phase;
        _phase.start();
    }

//  ########################################
//  #            Games Specific            #
//  ########################################

    public abstract GamePlayer createPlayer(String name);

    public abstract GameTeam<? extends GamePlayer> createTeam(List<GamePlayer> party);

    public abstract void resolvePlayerOutcomes();

//  ########################################
//  #               Messages               #
//  ########################################

    public void lobbyBroadcast(String messageKey, Object... args) {
        for (GamePlayer playerOnLobby : playersOnLobby) {
            BukkitPlayer.sendMessage(playerOnLobby, messageKey, args);
        }
    }

    public void sendLoadingMessage() {
        lobbyBroadcast("grindstone.game.loading");
    }

    public void sendVictoryMessage() {
        GameTeam<? extends GamePlayer> winnerTeam = null;
        for (GameTeam<? extends GamePlayer> team : getTeams()) {
            if (team.isWinner()) {
                winnerTeam = team;
                break;
            }
        }

        if (winnerTeam == null) {
            throw new IllegalStateException("A winner must be decided when calling this method. Use GameTeam#won");
        }

        String gamemode = settings.getName();
        String winnerColor = winnerTeam.getChatColor().asHexString();
        String winnerName = winnerTeam.getName();
        String teamMembers = String.join(", ", winnerTeam.getMembers().stream().map(p -> p.getName()).toList());
        String[][] playersWithBestStats = playersWithBestStats();

        GrindstoneConfig config = GrindstoneConfig.getInstance();
        String template = String.join("\n", config.gameSummary);
        Object[] staticArgs = new Object[]{
                gamemode,
                winnerName,
                teamMembers,
                winnerColor,
                playersWithBestStats[0][0],
                playersWithBestStats[0][1],
                playersWithBestStats[1][0],
                playersWithBestStats[1][1],
                playersWithBestStats[2][0],
                playersWithBestStats[2][1],
                null // placeholder for {10}
        };

        for (GamePlayer playerOnLobby : playersOnLobby) {
            staticArgs[10] = MessagesRepo.getTranslations().translate(
                    BukkitPlayer.getLocale(playerOnLobby),
                    highlightedStatMessageKey()
            );
            BukkitPlayer.sendTranslatedMessage(playerOnLobby, template, staticArgs);
        }

        for (GamePlayer player : winnerTeam.getMembers()) {
            BukkitPlayer.sendTitleMessage(
                    player,
                    "grindstone.game.winner.title",
                    new Object[0],
                    "grindstone.game.winner.subtitle",
                    new Object[0]
            );
        }

        for (GameTeam<? extends GamePlayer> team : getTeams()) {
            if (team == winnerTeam) {
                continue;
            }

            for (GamePlayer player : winnerTeam.getMembers()) {
                BukkitPlayer.sendTitleMessage(
                        player,
                        "grindstone.game.loser.title",
                        new Object[0],
                        "grindstone.game.loser.subtitle",
                        new Object[0]
                );
            }
        }
    }

    public abstract String highlightedStatMessageKey();

    public abstract String[][] playersWithBestStats();

    public void sendPlayerRejoinMessage(String playerName) {
        NamedTextColor teamColor = getTeam(getPlayer(playerName)).getChatColor();
        lobbyBroadcast("grindstone.game.rejoin", playerName, teamColor.asHexString());
    }

    public void sendPlayerLeftMessage(String playerName) {
        NamedTextColor teamColor = getTeam(getPlayer(playerName)).getChatColor();
        lobbyBroadcast("grindstone.game.left", playerName, teamColor.asHexString());
    }

    public void sendGameExplanationMessage() {
        GrindstoneConfig config = GrindstoneConfig.getInstance();
        int width = config.gameExplanationWidth;
        String divisor = config.gameExplanationDivisor;
        String divisorColor = config.gameExplanationDivisorColor;
        String explanationColor = config.gameExplanationColor;
        String gameColor = config.gameExplanationGameColor;

        String explanationKey = gameExplanationMessageKey();
        for (GamePlayer player : playersOnLobby) {
            String message = MessagesRepo.getTranslations().translate(BukkitPlayer.getLocale(player), explanationKey);
            String centered = "\n" + TextFormatting.formatCentered(message, width) + "\n";
            centered = explanationColor + centered.replaceAll("\n", "\n" + explanationColor);
            centered = gameColor + settings.getName() + "\n" + centered;
            centered = divisorColor + divisor.repeat(width) + "\n" + centered + "\n" + divisorColor + divisor.repeat(
                    width
            );
            BukkitPlayer.sendMessage(player, centered);
        }
    }

    public abstract String gameExplanationMessageKey();
    
    */

}
