package io.github.tiagofar78.grindstone.game;

import java.util.List;

import io.github.tiagofar78.grindstone.game.phases.DisabledPhase;
import io.github.tiagofar78.grindstone.game.phases.LoadingPhase;
import io.github.tiagofar78.grindstone.game.phases.Phase;

public abstract class Game<P extends Player, T extends Team<P>> {
    
    private GameDependencies dependencies;

    private List<T> teams;
    private MatchLobby lobby;

    private Phase<? extends Game<P, T>> currPhase;

    public Game(GameDependencies dependencies, List<T> teams) {
        this.dependencies = dependencies;
        this.lobby = new MatchLobby();
        this.teams = teams;
        for (T team : teams) {
            for (P player : team.getMembers()) {
                player.setGame(this);
                lobby.addPlayer(player);
            }
        }
    }
    
    public GameDependencies getDependencies() {
        return dependencies;
    }

//  >------------------------{ Lobby }------------------------<

    public List<T> getTeams() {
        return teams;
    }

    public MatchLobby getLobby() {
        return lobby;
    }
    
    public abstract void removePlayerFromGame(P player);

    public void playerLeft(P player) {
        sendPlayerLeftMessage(player);
        removePlayerFromGame(player);
        lobby.removePlayer(player);
    }

//  >------------------------{ Admin }------------------------<

    public void forceStop() {
        startNextPhase(new DisabledPhase<Game<P, T>>(this));
    }

//  >------------------------{ Phase }------------------------<
    
    public void start() {
        startNextPhase(new LoadingPhase<Game<P, T>>(this));
    }

    public abstract void load();
    
    public void runMatchIntro() {
        startNextPhase();
    }

    public abstract Phase<? extends Game<P, T>> getFirstPhase();
    
    public void runGameOver() {
        sendGameOverMessages();
        startNextPhase();
    }

    public void disable() {
        // Empty
    }

    public Phase<? extends Game<P, T>> getCurrentPhase() {
        return currPhase;
    }

    public void startNextPhase() {
        startNextPhase(currPhase.next());
    }

    public void startNextPhase(Phase<? extends Game<P, T>> phase) {
        currPhase = phase;
        currPhase.start();
    }

//  >--------------------{ Game Specifics }--------------------<
    
    public void archive() {
        // Empty
    }

//  >-----------------------{ Messages }-----------------------<
    
    public abstract void sendLoadingMessage();

    public abstract void sendPlayerLeftMessage(P player);
    
    private void sendGameOverMessages() {
        sendVictoryMessage();
        sendDefeatOrDrawMessage();
    }
    
    public abstract void sendVictoryMessage();
    
    public abstract void sendDefeatOrDrawMessage();

}
