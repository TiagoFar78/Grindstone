package io.github.tiagofar78.grindstone.game;

import java.util.List;

import io.github.tiagofar78.grindstone.enginebridge.game.GameBridge;
import io.github.tiagofar78.grindstone.game.phases.DisabledPhase;
import io.github.tiagofar78.grindstone.game.phases.LoadingPhase;
import io.github.tiagofar78.grindstone.game.phases.Phase;

public abstract class Game<B extends GameBridge, P extends Player<?>, T extends Team<P>> {
    
    private GameDependencies dependencies;
    public final B gameEngine;

    private List<T> teams;
    private MatchLobby<P> lobby;

    private Phase<? extends Game<B, P, T>> currPhase;

    public Game(GameDependencies dependencies, B bridge, List<T> teams) {
        this.dependencies = dependencies;
        this.gameEngine = bridge;
        this.lobby = new MatchLobby<P>();
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

    public B getGameEngine() {
        return gameEngine;
    }

//  >------------------------{ Lobby }------------------------<

    public List<T> getTeams() {
        return teams;
    }

    public MatchLobby<P> getLobby() {
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
        startNextPhase(new DisabledPhase<Game<B, P, T>>(this));
    }

//  >------------------------{ Phase }------------------------<
    
    public void start() {
        startNextPhase(new LoadingPhase<Game<B, P, T>>(this));
    }

    public abstract void load();
    
    public void runMatchIntro() {
        startNextPhase();
    }

    public abstract Phase<? extends Game<B, P, T>> getFirstPhase();
    
    public void runGameOver() {
        sendGameOverMessages();
        startNextPhase();
    }

    public void disable() {
        gameEngine.disable(this);
    }

    public Phase<? extends Game<B, P, T>> getCurrentPhase() {
        return currPhase;
    }

    public void startNextPhase() {
        startNextPhase(currPhase.next());
    }

    public void startNextPhase(Phase<? extends Game<B, P, T>> phase) {
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
    
    public abstract void sendGameOverMessages();

}
