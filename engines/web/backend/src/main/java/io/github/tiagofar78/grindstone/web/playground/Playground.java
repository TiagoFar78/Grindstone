package io.github.tiagofar78.grindstone.web.playground;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.tiagofar78.grindstone.web.PlayerState;
import io.github.tiagofar78.grindstone.web.WebGame;
import io.github.tiagofar78.grindstone.web.WebPlayer;
import io.github.tiagofar78.grindstone.web.games.tictactoe.WebTTTFactory;
import io.github.tiagofar78.grindstone.web.network.OutboundMessages;
import io.github.tiagofar78.grindstone.web.playground.commands.Commands;
import io.github.tiagofar78.grindstone.web.playground.commands.EditProfileCommand;
import io.github.tiagofar78.grindstone.web.playground.commands.LeaveCommand;
import io.github.tiagofar78.grindstone.web.playground.commands.PlaygroundCommand;
import io.github.tiagofar78.grindstone.web.playground.commands.RejoinCommand;
import io.github.tiagofar78.grindstone.web.playground.commands.StartGameCommand;
import io.javalin.websocket.WsContext;

public class Playground {

    // ################################################## State ###################################################

    private final List<WebPlayer> playerRegistry = new ArrayList<>();
    private final List<AvailableGame> availableGames = new ArrayList<>();
    private final List<GameSession> runningSessions = new ArrayList<>();
    private int nextSessionId = 1;

    private final Commands<PlaygroundCommand> commands = new Commands<>();

    // ################################################# Setup ####################################################

    public Playground() {
        commands.register("profile", new EditProfileCommand());
        commands.register("start", new StartGameCommand());
        commands.register("leave", new LeaveCommand());
        commands.register("rejoin", new RejoinCommand());

        registerGame(WebTTTFactory.get());
    }

    public void registerGame(AvailableGame game) {
        availableGames.add(game);
    }

    // ############################################# Client lifecycle #############################################

    public synchronized String join(String name, String emoji, WsContext ctx) {
        WebPlayer existing = findByName(name);

        if (existing != null) {
            if (existing.isOnline()) {
                return "That nickname is already in use.";
            }
            // Reconnect: reuse the existing player, just update the socket
            existing.setCtx(ctx);
            existing.setOnline(true);

            if (existing.getState() == PlayerState.IN_GAME && existing.getCurrentGame() != null &&
                    existing.getCurrentGame().isAlive()) {
                existing.getCurrentGame().playerReconnected(existing);
            } else {
                existing.setState(PlayerState.IN_LOBBY);
            }
        } else {
            WebPlayer player = new WebPlayer(name, emoji, ctx);
            player.setOnline(true);
            playerRegistry.add(player);
        }

        refreshMenus();
        return null;
    }

    public synchronized void disconnect(String name) {
        WebPlayer player = findByName(name);
        if (player == null) {
            return;
        }

        player.setOnline(false);
        if (player.getState() == PlayerState.IN_GAME) {
            GameSession session = player.getCurrentGame();
            if (session != null) {
                session.playerLeft(player);
            }
        }

        refreshMenus();
    }

    public synchronized void removeGame(WebGame game) {
        GameSession session = findSessionByGame(game);
        if (session == null) {
            return;
        }

        session.disable();
        runningSessions.remove(session);

        for (WebPlayer p : session.getOnlinePlayers()) {
            p.setState(PlayerState.IN_LOBBY);
        }

        refreshMenus();
    }

    // ################################################# Messages #################################################

    public synchronized void process(String playerName, JsonNode node) {
        WebPlayer player = findByName(playerName);
        if (player == null) {
            return;
        }

        String cmdLabel = node.path("type").asText("");
        PlaygroundCommand command = commands.find(cmdLabel);
        if (command != null) {
            command.execute(this, player, node);
            return;
        }

        if (player.getState() != PlayerState.IN_GAME) {
            player.sendMessage(OutboundMessages.buildErrorToast("Unknown message type"));
            return;
        }

        WebGame game = player.getCurrentGame().getGame();
        if (!game.executeCommand(cmdLabel, player, node)) {
            player.sendMessage(OutboundMessages.buildErrorToast("Unknown message type"));
        }
    }

    // ################################################## Lobby ###################################################

    public void refreshMenus() {
        for (WebPlayer p : playerRegistry) {
            if (p.isOnline() && p.getState() == PlayerState.IN_LOBBY) {
                p.sendMessage(OutboundMessages.buildLobbyPage(p, playerRegistry, availableGames, runningSessions));
            }
        }
    }

    // ################################################ Public API ################################################

    public String getPlayerName(WsContext ctx) {
        for (WebPlayer p : playerRegistry) {
            if (p.getCtx() == ctx) {
                return p.getName();
            }
        }
        return null;
    }

    public List<WebPlayer> getPlayerRegistry() {
        return List.copyOf(playerRegistry);
    }

    public boolean isNameTaken(WebPlayer p, String name) {
        for (WebPlayer registeredPlayer : playerRegistry) {
            if (p != registeredPlayer && registeredPlayer.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
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
