package io.github.tiagofar78.grindstone.game;

import java.util.List;

import io.github.tiagofar78.grindstone.game.phases.DisabledPhase;
import io.github.tiagofar78.grindstone.game.phases.LoadingPhase;
import io.github.tiagofar78.grindstone.game.phases.Phase;

public abstract class Game {
    
    private GameDependencies dependencies;

    private MatchLobby lobby;

    private Phase currPhase;

    public Game(GameDependencies dependencies, List<Player> players) {
        this.dependencies = dependencies;
        this.lobby = new MatchLobby();
        for (Player player : players) {
            player.setGame(this);
            lobby.addPlayer(player);
        }
    }
    
    public GameDependencies getDependencies() {
        return dependencies;
    }

    public MatchLobby getLobby() {
        return lobby;
    }

//  >------------------------{ Lobby }------------------------<
    
    public abstract void removePlayerFromGame(Player player);

    public void playerLeft(Player player) {
        sendPlayerLeftMessage(player);
        removePlayerFromGame(player);
        lobby.removePlayer(player);
    }

//  >------------------------{ Admin }------------------------<

    public void forceStop() {
        startNextPhase(new DisabledPhase(this));
    }

//  >------------------------{ Phase }------------------------<
    
    public void start() {
        startNextPhase(new LoadingPhase(this));
    }

    public abstract void load();
    
    public void runMatchIntro() {
        startNextPhase();
    }

    public abstract Phase getFirstPhase();
    
    public void runGameOver() {
        sendGameOverMessages();
        startNextPhase();
    }

    public void disable() {
        // Empty
    }

    public Phase getCurrentPhase() {
        return currPhase;
    }

    public void startNextPhase() {
        startNextPhase(currPhase.next());
    }

    public void startNextPhase(Phase phase) {
        currPhase = phase;
        currPhase.start();
    }

//  >--------------------{ Game Specifics }--------------------<
    
    public void archive() {
        // Empty
    }

//  >-----------------------{ Messages }-----------------------<
    
    public abstract void sendLoadingMessage();    

    public abstract void sendPlayerLeftMessage(Player player);
    
    private void sendGameOverMessages() {
        sendVictoryMessage();
        sendDefeatOrDrawMessage();
    }
    
    public abstract void sendVictoryMessage();
    
    public abstract void sendDefeatOrDrawMessage();

}
