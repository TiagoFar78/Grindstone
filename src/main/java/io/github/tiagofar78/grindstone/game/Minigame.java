package io.github.tiagofar78.grindstone.game;

import io.github.tiagofar78.grindstone.game.phases.DisabledPhase;
import io.github.tiagofar78.grindstone.game.phases.LoadingPhase;
import io.github.tiagofar78.grindstone.game.phases.Phase;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public abstract class Minigame {

    private int _id;
    private String _mapName;

    private List<MinigamePlayer> _playersOnLobby;
    private List<MinigameTeam<MinigamePlayer>> _teams;

    private Phase _phase;

    public Minigame(int id, String mapName, Location referenceBlock, List<List<? extends MinigamePlayer>> players) {
        _id = id;
        _mapName = mapName;

        _playersOnLobby = new ArrayList<>();
        _teams = new ArrayList<>();

        startNextPhase(new LoadingPhase(this));
    }

//  #########################################
//  #                 Lobby                 #
//  #########################################

    public List<MinigamePlayer> getPlayersOnLobby() {
        return _playersOnLobby;
    }

    public MinigamePlayer getPlayer(String playerName) {
        for (int i = 0; i < _playersOnLobby.size(); i++) {
            if (_playersOnLobby.get(i).getName().equals(playerName)) {
                return _playersOnLobby.get(i);
            }
        }

        return null;
    }

    public abstract void addPlayerToGame(MinigamePlayer player);

    public void playerRejoin(String playerName) {
        MinigamePlayer player = getPlayer(playerName);
        addPlayerToGame(player);
        _playersOnLobby.add(player);
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

    /**
     * @return 0 if successful<br>
     *         -1 if already started ongoing phase
     */
    public int forceStart() {
        if (_phase.hasGameStarted()) {
            return -1;
        }

        startNextPhase(newOngoingPhase());
        return 0;
    }

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

}
