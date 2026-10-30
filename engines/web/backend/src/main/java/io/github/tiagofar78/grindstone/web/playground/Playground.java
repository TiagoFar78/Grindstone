package io.github.tiagofar78.grindstone.web.playground;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.tiagofar78.grindstone.web.PlayerState;
import io.github.tiagofar78.grindstone.web.WebGame;
import io.github.tiagofar78.grindstone.web.WebPlayer;
import io.github.tiagofar78.grindstone.web.games.tictactoe.WebTTTFactory;
import io.github.tiagofar78.grindstone.web.network.messages.Outbound;
import io.github.tiagofar78.grindstone.web.network.messages.Outbound.Lobby.OnlinePlayer;
import io.github.tiagofar78.grindstone.web.network.messages.Outbound.Lobby.RunningGame;
import io.github.tiagofar78.grindstone.web.playground.commands.Commands;
import io.github.tiagofar78.grindstone.web.playground.commands.EditProfileCommand;
import io.github.tiagofar78.grindstone.web.playground.commands.LeaveCommand;
import io.github.tiagofar78.grindstone.web.playground.commands.PlaygroundCommand;
import io.github.tiagofar78.grindstone.web.playground.commands.RejoinCommand;
import io.github.tiagofar78.grindstone.web.playground.commands.StartGameCommand;

public class Playground {

    // ################################################## State ###################################################

    private final List<WebPlayer> playerRegistry = new ArrayList<>();
    private final List<AvailableGame> availableGames = new ArrayList<>();
    private final List<GameSession> runningSessions = new ArrayList<>();
    private int nextSessionId = 1;

    private final Commands<PlaygroundCommand<?>> commands = new Commands<>();

    // ################################################# Setup ####################################################

    public Playground() {
        commands.register("profile", new EditProfileCommand());
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

        if (existing != null) {
            if (existing.isOnline()) {
                return null;
            }

            existing.updateConnection(player);
            existing.setOnline(true);

            if (existing.getState() == PlayerState.IN_GAME && existing.getCurrentGame() != null &&
                    existing.getCurrentGame().canReconnect(existing)) {
                existing.getCurrentGame().playerReconnected(existing);
            } else {
                existing.setState(PlayerState.IN_LOBBY);
            }
        } else {
            player.setOnline(true);
            playerRegistry.add(player);
            existing = player;
        }

        refreshMenus();
        return existing;
    }

    public synchronized void disconnect(WebPlayer player) {
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

    public synchronized void process(WebPlayer player, JsonNode node) {
        String cmdLabel = node.path("type").asText("");
        boolean commandFound = commands.execute(cmdLabel, this, player, node);
        if (commandFound) {
            return;
        }

        if (player.getState() != PlayerState.IN_GAME) {
            player.sendMessage(new Outbound.Error("Unknown message type"));
            return;
        }

        WebGame game = player.getCurrentGame().getGame();
        if (!game.executeCommand(cmdLabel, this, player, node)) {
            player.sendMessage(new Outbound.Error("Unknown message type"));
        }
    }

    // ################################################## Lobby ###################################################

    public void refreshMenus() {
        List<String> gamesNames = availableGames.stream().map(p -> p.displayName()).toList();
        List<RunningGame> runningGames = runningSessions.stream().map(p -> {
            List<List<String>> teamsPlayerNames = p.getTeams().stream().map(l -> l.stream().map(wp -> wp.getName()).toList()).toList();
            return new Outbound.Lobby.RunningGame(p.getId(), p.getGame().getName(), teamsPlayerNames);
        }).toList();

        for (WebPlayer p : playerRegistry) {
            if (p.isOnline() && p.getState() == PlayerState.IN_LOBBY) {
                List<OnlinePlayer> onlinePlayers = playerRegistry.stream().map(wp -> {
                    int gameSessionId = wp.getCurrentGame() == null ? -1 : wp.getCurrentGame().getId();
                    return new OnlinePlayer(wp.getName(), wp.getEmoji(), wp == p, gameSessionId);
                }).toList();

                boolean canRejoin = p.getCurrentGame() != null;
                p.sendMessage(new Outbound.Lobby(gamesNames, runningGames, onlinePlayers, canRejoin));
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
