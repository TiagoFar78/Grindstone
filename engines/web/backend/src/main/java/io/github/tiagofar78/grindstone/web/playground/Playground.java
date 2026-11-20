package io.github.tiagofar78.grindstone.web.playground;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.tiagofar78.grindstone.web.WebGame;
import io.github.tiagofar78.grindstone.web.games.tictactoe.WebTTTFactory;
import io.github.tiagofar78.grindstone.web.network.messages.Outbound;
import io.github.tiagofar78.grindstone.web.network.messages.Outbound.Lobby.OnlinePlayer;
import io.github.tiagofar78.grindstone.web.network.messages.Outbound.Lobby.RunningGame;
import io.github.tiagofar78.grindstone.web.player.WebPlayer;
import io.github.tiagofar78.grindstone.web.playground.commands.Commands;
import io.github.tiagofar78.grindstone.web.playground.commands.LeaveCommand;
import io.github.tiagofar78.grindstone.web.playground.commands.PlaygroundCommand;
import io.github.tiagofar78.grindstone.web.playground.commands.RejoinCommand;
import io.github.tiagofar78.grindstone.web.playground.commands.StartGameCommand;

public class Playground {

    // ################################################## State ###################################################

    private final String name;
    private final List<WebPlayer> playerRegistry = new ArrayList<>();
    private final List<AvailableGame> availableGames = new ArrayList<>();
    private final List<GameSession> runningSessions = new ArrayList<>();
    private int nextSessionId = 1;

    private final Commands<PlaygroundCommand<?>> commands = new Commands<>();

    // ################################################# Setup ####################################################

    public Playground(String name) {
        this.name = name;
        commands.register("start", new StartGameCommand());
        commands.register("leave", new LeaveCommand());
        commands.register("rejoin", new RejoinCommand());

        registerGame(WebTTTFactory.get());
    }

    private void registerGame(AvailableGame game) {
        availableGames.add(game);
    }

    // ############################################# Client lifecycle #############################################

    public synchronized WebPlayer join(WebPlayer player) {
        WebPlayer existing = findByName(player.getName());
        if (existing == null) {
            playerRegistry.add(player);
            existing = player;
        } else {
            existing.connect(player.getCtx());
        }

        if (existing.isInGame()) {
            if (existing.getCurrentSession().canRejoin(existing)) {
                existing.getCurrentSession().playerRejoined(existing);
            } else {
                existing.leaveGame();
            }
        }

        refreshMenus();
        return existing;
    }

    public synchronized void disconnect(WebPlayer player) {
        if (player.isInGame()) {
            player.getCurrentSession().playerLeft(player);
        }

        player.disconnect();
        refreshMenus();
    }

    public synchronized void removeGame(WebGame game) {
        GameSession session = findSessionByGame(game);
        if (session == null) {
            return;
        }

        session.disable();
        runningSessions.remove(session);

        session.getTeams().forEach(t -> t.forEach(WebPlayer::gameFinished));

        refreshMenus();
    }

    // ################################################# Messages #################################################

    public synchronized void process(WebPlayer player, JsonNode node) {
        String cmdLabel = node.path("type").asText("");
        boolean commandFound = commands.execute(cmdLabel, this, player, node);
        if (commandFound) {
            return;
        }

        if (!player.isInGame()) {
            player.sendMessage(new Outbound.Error("Unknown message type"));
            return;
        }

        if (!player.getCurrentSession().getGame().executeCommand(cmdLabel, this, player, node)) {
            player.sendMessage(new Outbound.Error("Unknown message type"));
        }
    }

    // ################################################## Lobby ###################################################

    public void refreshMenus() {
        List<String> gamesNames = availableGames.stream().map(p -> p.displayName()).toList();
        List<RunningGame> runningGames = runningSessions.stream().map(p -> {
            List<List<String>> teamsPlayerNames = p.getTeams().stream().map(
                    l -> l.stream().map(wp -> wp.getName()).toList()
            ).toList();
            return new Outbound.Lobby.RunningGame(p.getId(), p.getGame().getName(), teamsPlayerNames);
        }).toList();

        for (WebPlayer p : playerRegistry) {
            if (p.isInLobby()) {
                List<OnlinePlayer> onlinePlayers = playerRegistry.stream().map(wp -> {
                    int gameSessionId = !wp.isInGame() ? -1 : wp.getCurrentSession().getId();
                    return new OnlinePlayer(wp.getName(), wp.getEmoji(), wp == p, gameSessionId);
                }).toList();

                boolean canRejoin = p.canRejoin() && p.getCurrentSession().canRejoin(p);
                p.sendMessage(new Outbound.Lobby(name, gamesNames, runningGames, onlinePlayers, canRejoin));
            }
        }
    }

    // ################################################ Public API ################################################

    public List<WebPlayer> getPlayerRegistry() {
        return List.copyOf(playerRegistry);
    }

    public boolean isNameTaken(String name) {
        for (WebPlayer registeredPlayer : playerRegistry) {
            if (registeredPlayer.getName().equalsIgnoreCase(name) && registeredPlayer.isOnline()) {
                return true;
            }
        }
        return false;
    }

    public AvailableGame findAvailableGame(int index) {
        if (index < 0 || index >= availableGames.size()) {
            return null;
        }

        return availableGames.get(index);
    }

    public AvailableGame findAvailableGame(String name) {
        for (AvailableGame g : availableGames) {
            if (g.displayName().equalsIgnoreCase(name)) {
                return g;
            }
        }
        return null;
    }

    public GameSession createSession(WebGame game, List<List<WebPlayer>> teams) {
        GameSession session = new GameSession(nextSessionId++, game, teams);
        runningSessions.add(session);
        return session;
    }

    public GameSession findSessionByGame(WebGame game) {
        for (GameSession s : runningSessions) {
            if (s.getGame() == game) {
                return s;
            }
        }
        return null;
    }

    // ################################################## Helpers #################################################

    private WebPlayer findByName(String name) {
        for (WebPlayer p : playerRegistry) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

}
