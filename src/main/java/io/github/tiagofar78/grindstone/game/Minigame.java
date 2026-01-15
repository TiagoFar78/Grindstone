package io.github.tiagofar78.grindstone.game;

import io.github.tiagofar78.grindstone.game.phases.DisabledPhase;
import io.github.tiagofar78.grindstone.game.phases.LoadingPhase;
import io.github.tiagofar78.grindstone.game.phases.Phase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Minigame {

    private MinigameMap map;
    private MinigameSettings settings;

    private List<MinigamePlayer> playersOnLobby;
    private List<MinigameTeam<MinigamePlayer>> teams;

    private Phase _phase;

    public Minigame(MinigameMap map, MinigameSettings settings, List<Collection<String>> parties) {
        List<List<MinigamePlayer>> players = toMinigamePlayer(parties);

        this.map = map;
        this.settings = settings;

        playersOnLobby = new ArrayList<>();
        addToLobby(players);
        teams = createTeams(players);

        startNextPhase(new LoadingPhase(this));
    }

    public MinigameMap getMap() {
        return map;
    }

    public MinigameSettings getSettings() {
        return settings;
    }

    public List<MinigameTeam<MinigamePlayer>> getTeams() {
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
        sendPlayerLeftMessage(playerName);
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

    public abstract List<List<MinigamePlayer>> toMinigamePlayer(List<Collection<String>> parties);

    public abstract List<MinigameTeam<MinigamePlayer>> createTeams(List<List<MinigamePlayer>> players);

    public abstract void resolvePlayerOutcomes();

    public abstract void teleportToPreparingRoom();

//  ########################################
//  #               Messages               #
//  ########################################

    public void sendLoadingMessage() {
        // TODO
    }

    public void sendVictoryMessage() {
        // TODO
    }

    public void sendPlayerRejoinMessage(String playerName) {
        // TODO send to all players in lobby
    }

    public void sendPlayerLeftMessage(String playerName) {
        // TODO send to all players in lobby
    }

    public abstract void sendGameExplanationMessage();

}
