package io.github.tiagofar78.grindstone.web.playground;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.tiagofar78.grindstone.game.Rejoinable;
import io.github.tiagofar78.grindstone.web.WebGame;
import io.github.tiagofar78.grindstone.web.WebPlayer;

public class GameSession {

    private final int id;
    private final WebGame game;
    private final List<List<WebPlayer>> initialTeams;
    private final Set<WebPlayer> onlinePlayers;
    private boolean isAlive;

    public GameSession(int id, WebGame game, List<List<WebPlayer>> teams) {
        this.id = id;
        this.game = game;
        this.initialTeams = List.copyOf(teams);
        this.onlinePlayers = new HashSet<>();
        for (List<WebPlayer> team : teams) {
            onlinePlayers.addAll(team);
        }
        this.isAlive = true;
    }

    public int getId() {
        return id;
    }

    public WebGame getGame() {
        return game;
    }

    public List<List<WebPlayer>> getTeams() {
        return initialTeams;
    }

    public Set<WebPlayer> getOnlinePlayers() {
        return new HashSet<>(onlinePlayers);
    }

    public void playerLeft(WebPlayer p) {
        onlinePlayers.remove(p);
        game.disconnect(p);
    }

    public void playerReconnected(WebPlayer p) {
        onlinePlayers.add(p);
        ((Rejoinable) game).playerRejoin(p.getCorePlayer());
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void disable() {
        isAlive = false;
    }

}
