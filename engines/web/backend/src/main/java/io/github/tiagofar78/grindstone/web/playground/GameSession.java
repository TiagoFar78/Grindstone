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
    private boolean isRunning;

    public GameSession(int id, WebGame game, List<List<WebPlayer>> teams) {
        this.id = id;
        this.game = game;
        this.initialTeams = List.copyOf(teams);
        this.onlinePlayers = new HashSet<>();
        for (List<WebPlayer> team : teams) {
            onlinePlayers.addAll(team);
        }
        this.isRunning = true;
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

    public boolean canReconnect(WebPlayer p) {
        return game instanceof Rejoinable && wasPlaying(p) && !onlinePlayers.contains(p) && isRunning;
    }

    private boolean wasPlaying(WebPlayer p) {
        for (List<WebPlayer> team : initialTeams) {
            if (team.contains(p)) {
                return true;
            }
        }

        return false;
    }

    public void playerLeft(WebPlayer p) {
        onlinePlayers.remove(p);
        game.disconnect(p);
    }

    public void playerReconnected(WebPlayer p) {
        if (!canReconnect(p)) {
            return;
        }

        onlinePlayers.add(p);
        ((Rejoinable) game).playerRejoin(p.getCorePlayer());
    }

    public void disable() {
        isRunning = false;
    }

}
