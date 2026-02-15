package io.github.tiagofar78.grindstone.game;

import io.github.tiagofar78.grindstone.GrindstoneConfig;
import io.github.tiagofar78.grindstone.bukkit.BukkitPlayer;
import io.github.tiagofar78.grindstone.game.phases.DisabledPhase;
import io.github.tiagofar78.grindstone.game.phases.LoadingPhase;
import io.github.tiagofar78.grindstone.game.phases.Phase;
import io.github.tiagofar78.grindstone.util.TeamLayoutSolver;
import io.github.tiagofar78.grindstone.util.TextFormatting;
import io.github.tiagofar78.messagesrepo.MessagesRepo;

import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Minigame {

    private MinigameMap map;
    private MinigameSettings settings;

    private List<MinigamePlayer> playersOnLobby;
    private List<MinigameTeam<? extends MinigamePlayer>> teams;

    private Phase _phase;

    public Minigame(MinigameMap map, MinigameSettings settings, List<Collection<String>> parties) {
        this(map, settings, parties, false);
    }

    public Minigame(MinigameMap map, MinigameSettings settings, List<Collection<String>> parties, boolean keepTeams) {
        List<List<MinigamePlayer>> players = toMinigamePlayer(parties);

        this.map = map;
        this.settings = settings;

        playersOnLobby = new ArrayList<>();
        addToLobby(players);
        teams = keepTeams ? partiesToTeams(players) : distributeParties(players);

        startNextPhase(new LoadingPhase(this));
    }

    public MinigameMap getMap() {
        return map;
    }

    public MinigameSettings getSettings() {
        return settings;
    }

    public List<MinigameTeam<? extends MinigamePlayer>> getTeams() {
        return teams;
    }

    private List<MinigameTeam<? extends MinigamePlayer>> partiesToTeams(List<List<MinigamePlayer>> parties) {
        List<MinigameTeam<? extends MinigamePlayer>> teams = new ArrayList<>();
        for (List<MinigamePlayer> party : parties) {
            teams.add(createTeam(party));
        }

        return teams;
    }

//  #########################################
//  #                 Lobby                 #
//  #########################################

    public void addToLobby(List<List<MinigamePlayer>> players) {
        for (List<MinigamePlayer> parties : players) {
            for (MinigamePlayer player : parties) {
                playersOnLobby.add(player);
            }
        }
    }

    public List<MinigamePlayer> getPlayersOnLobby() {
        return playersOnLobby;
    }

    public MinigamePlayer getPlayer(String playerName) {
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

    public abstract void addPlayerToGame(MinigamePlayer player);

    public void playerRejoin(String playerName) {
        MinigamePlayer player = getPlayer(playerName);
        addPlayerToGame(player);
        playersOnLobby.add(player);
        sendPlayerRejoinMessage(playerName);
    }

    public abstract void removePlayerFromGame(MinigamePlayer player);

    public void playerLeft(String playerName) {
        MinigamePlayer player = getPlayer(playerName);
        removePlayerFromGame(player);
        playersOnLobby.remove(player);
        sendPlayerLeftMessage(playerName);
    }

    private List<List<MinigamePlayer>> toMinigamePlayer(List<Collection<String>> parties) {
        List<List<MinigamePlayer>> newParties = new ArrayList<>();
        for (Collection<String> party : parties) {
            List<MinigamePlayer> newParty = new ArrayList<>();
            for (String member : party) {
                newParty.add(createPlayer(member));
            }
            newParties.add(newParty);
        }

        return newParties;
    }

    private List<MinigameTeam<? extends MinigamePlayer>> distributeParties(List<List<MinigamePlayer>> parties) {
        int maxTeams = getSettings().maxTeams();
        int playersPerTeam = getSettings().maxPlayersPerTeam();
        List<List<MinigamePlayer>> teamPlayers = TeamLayoutSolver.solve(parties, maxTeams, playersPerTeam);
        List<MinigameTeam<? extends MinigamePlayer>> teams = new ArrayList<>();
        for (List<MinigamePlayer> playersInTeam : teamPlayers) {
            teams.add(createTeam(playersInTeam));
        }

        return teams;
    }

    public int getTeamIndex(MinigamePlayer member) {
        for (int i = 0; i < teams.size(); i++) {
            if (teams.get(i).getMembers().contains(member)) {
                return i;
            }
        }

        return -1;
    }

    public MinigameTeam<? extends MinigamePlayer> getTeam(MinigamePlayer member) {
        return teams.get(getTeamIndex(member));
    }

    public void makeSpectator(MinigamePlayer player) {
        player.setSpectator(true);
    }

    public void teleportToPreparingRoom() {
        for (MinigamePlayer player : playersOnLobby) {
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

    public abstract MinigamePlayer createPlayer(String name);

    public abstract MinigameTeam<? extends MinigamePlayer> createTeam(List<MinigamePlayer> party);

    public abstract void resolvePlayerOutcomes();

//  ########################################
//  #               Messages               #
//  ########################################

    public void lobbyBroadcast(String messageKey, Object... args) {
        for (MinigamePlayer playerOnLobby : playersOnLobby) {
            BukkitPlayer.sendMessage(playerOnLobby, messageKey, args);
        }
    }

    public void sendLoadingMessage() {
        lobbyBroadcast("grindstone.game.loading");
    }

    public void sendVictoryMessage() {
        MinigameTeam<? extends MinigamePlayer> winnerTeam = null;
        for (MinigameTeam<? extends MinigamePlayer> team : getTeams()) {
            if (team.isWinner()) {
                winnerTeam = team;
                break;
            }
        }

        if (winnerTeam == null) {
            throw new IllegalStateException("A winner must be decided when calling this method. Use MinigameTeam#won");
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

        for (MinigamePlayer playerOnLobby : playersOnLobby) {
            staticArgs[10] = MessagesRepo.getTranslations().translate(
                    BukkitPlayer.getLocale(playerOnLobby),
                    highlightedStatMessageKey()
            );
            BukkitPlayer.sendTranslatedMessage(playerOnLobby, template, staticArgs);
        }

        for (MinigamePlayer player : winnerTeam.getMembers()) {
            BukkitPlayer.sendTitleMessage(
                    player,
                    "grindstone.game.winner.title",
                    new Object[0],
                    "grindstone.game.winner.subtitle",
                    new Object[0]
            );
        }

        for (MinigameTeam<? extends MinigamePlayer> team : getTeams()) {
            if (team == winnerTeam) {
                continue;
            }

            for (MinigamePlayer player : winnerTeam.getMembers()) {
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
        for (MinigamePlayer player : playersOnLobby) {
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

}
