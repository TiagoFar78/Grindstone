package io.github.tiagofar78.grindstone.web.playground;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.tiagofar78.grindstone.game.Game;
import io.github.tiagofar78.grindstone.game.Rejoinable;
import io.github.tiagofar78.grindstone.web.WebGame;
import io.github.tiagofar78.grindstone.web.player.WebPlayer;

public class GameSession {

    private final int id;
    private final WebGame game;
    private final List<List<WebPlayer>> initialTeams;
    private final Set<WebPlayer> playersWhoLeft;
    private boolean isRunning;

    public GameSession(int id, WebGame game, List<List<WebPlayer>> teams) {
        this.id = id;
        this.game = game;
        this.initialTeams = List.copyOf(teams);
        this.playersWhoLeft = new HashSet<>();
        this.isRunning = false;
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

    public boolean canRejoin(WebPlayer p) {
        return game instanceof Rejoinable && wasPlaying(p) && playersWhoLeft.contains(p) && isRunning;
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
        playersWhoLeft.add(p);
        game.disconnect(p);
    }

    public void playerRejoined(WebPlayer p) {
        if (!canRejoin(p)) {
            return;
        }
        playersWhoLeft.remove(p);
        ((Rejoinable) game).playerRejoin(p.getCorePlayer());
    }

    public void start() {
        if (!isRunning) {
            ((Game<?, ?, ?>) game).start();
            isRunning = true;
        }
    }

    public void disable() {
        isRunning = false;
    }

}
