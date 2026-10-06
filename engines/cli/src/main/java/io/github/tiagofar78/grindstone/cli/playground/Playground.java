package io.github.tiagofar78.grindstone.cli.playground;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import io.github.tiagofar78.grindstone.cli.CLIGame;
import io.github.tiagofar78.grindstone.cli.CLIPlayer;
import io.github.tiagofar78.grindstone.cli.games.tictactoe.CLITTTBridge;
import io.github.tiagofar78.grindstone.cli.games.tictactoe.CLITicTacToe;
import io.github.tiagofar78.grindstone.cli.playground.network.Server;
import io.github.tiagofar78.grindstone.cli.services.CLIServices;
import io.github.tiagofar78.grindstone.game.Game;
import io.github.tiagofar78.grindstone.game.GameDependencies;
import io.github.tiagofar78.grindstone.games.tictactoe.TTTPlayer;

public class Playground {

    private static final String CMD_START = "start";
    private static final String CMD_LEAVE = "leave";

    private static final String START_USAGE =
            "Usage: " + CMD_START + " <game_index|game_name> <team> <team> ...  (players of a team separated by ',', e.g. '" + CMD_START + " 1 1 2' or '" + CMD_START + " 1 1,2 3,4')";

    private static final String COL_GAMES = "GAMES";
    private static final String COL_RUNNING = "RUNNING";
    private static final String COL_ONLINE = "ONLINE";
    private static final String CLEAR_SCREEN = "\u001B[H\u001B[2J";

    private static class GameEntry {

        private final int id;
        private final AvailableGame type;
        private final List<List<Integer>> teams; // client ids, grouped by team

        private GameEntry(int id, AvailableGame type, List<List<Integer>> teams) {
            this.id = id;
            this.type = type;
            this.teams = teams;
        }

    }

    private final List<AvailableGame> availableGames = new ArrayList<>();
    private final Map<CLIGame, GameEntry> games = new LinkedHashMap<>();
    private final Map<Integer, CLIGame> clientGame = new HashMap<>();
    private final List<Integer> online = new ArrayList<>();
    private final Map<Integer, CLIPlayer> idToPlayer = new HashMap<>();
    private final Map<Integer, String> idToName = new HashMap<>();
    private final Set<Integer> awaitingName = new LinkedHashSet<>();
    private int nextPlayerIndex = 1;
    private int nextGameId = 1;

    public Playground() {
        registerGame(
                new AvailableGame(
                        "TicTacToe",
                        teams -> {
                            GameDependencies dependencies = new GameDependencies(CLIServices.scheduler);
                            TTTPlayer p1 = new TTTPlayer(teams.get(0).getFirst(), UUID.randomUUID(), 0);
                            TTTPlayer p2 = new TTTPlayer(teams.get(1).getFirst(), UUID.randomUUID(), 1);
                            return (CLIGame) new CLITicTacToe(dependencies, new CLITTTBridge(), p1, p2);
                        }
                )
        );
    }

    public void registerGame(AvailableGame game) {
        availableGames.add(game);
    }

    // ############################################# Client lifecycle #############################################

    public synchronized void connect(int clientId) {
        CLIPlayer p = new CLIPlayer(clientId);
        idToPlayer.put(clientId, p);
        online.add(clientId);

        String defaultName = "Player" + nextPlayerIndex++;
        idToName.put(clientId, defaultName);
        awaitingName.add(clientId);

        send(p, "Welcome to the Grindstone Playground!");
        send(p, "Enter your name (leave blank to stay '" + defaultName + "'):");

        refreshMenus();
    }

    public synchronized void disconnect(Integer clientId) {
        online.remove(clientId);
        awaitingName.remove(clientId);
        idToPlayer.remove(clientId);
        idToName.remove(clientId);

        CLIGame current = currentGame(clientId);
        if (current != null) {
            removeFromGame(clientId, current);
        }

        refreshMenus();
    }

    public synchronized void removeGame(CLIGame game) {
        GameEntry entry = games.remove(game);
        if (entry == null) {
            return;
        }

        for (Entry<Integer, CLIGame> mapEntry : new ArrayList<>(clientGame.entrySet())) {
            if (mapEntry.getValue() != game) {
                continue;
            }

            int playerId = mapEntry.getKey();
            clientGame.remove(playerId);
            send(playerId, "Game #" + entry.id + " ended.");
        }

        refreshMenus();
    }

    // ################################################# Commands #################################################

    public synchronized void process(int clientId, String[] args) {
        if (awaitingName.remove(clientId)) {
            setName(clientId, args);
            refreshMenus();
            return;
        }

        String command = args[0].toLowerCase();
        CLIGame current = currentGame(clientId);

        if (command.equals(CMD_LEAVE)) {
            leaveGame(clientId, current);
            return;
        }

        if (current != null) {
            current.process(clientId, args);
            return;
        }

        switch (command) {
            case CMD_START -> startGame(clientId, args);
            case "" -> sendMainPage(clientId);
            default -> send(clientId, "Unknown command '" + args[0] + "'");
        }
    }

    private void setName(int clientId, String[] args) {
        String name = String.join(" ", args).trim();
        if (!name.isEmpty()) {
            idToName.put(clientId, name);
        }
        send(clientId, "Your name is '" + idToName.get(clientId) + "'.");
    }

    private void startGame(int clientId, String[] args) {
        if (args.length < 3) {
            send(clientId, START_USAGE);
            return;
        }

        AvailableGame type = findGame(args[1]);
        if (type == null) {
            send(clientId, "Unknown game '" + args[1] + "'.");
            return;
        }

        List<List<CLIPlayer>> teams = new ArrayList<>();
        List<List<Integer>> teamIds = new ArrayList<>();
        Set<Integer> players = new LinkedHashSet<>();
        for (int i = 2; i < args.length; i++) {
            List<CLIPlayer> team = new ArrayList<>();
            List<Integer> teamClientIds = new ArrayList<>();
            for (String playerIndexString : args[i].split(",")) {
                int playerIndex;
                try {
                    playerIndex = Integer.parseInt(playerIndexString);
                } catch (NumberFormatException e) {
                    send(clientId, "'" + playerIndexString + "' is not a valid player number. " + START_USAGE);
                    return;
                }

                if (playerIndex <= 0 || playerIndex > online.size()) {
                    send(clientId, "'" + playerIndexString + "' is not a valid player number. " + START_USAGE);
                    return;
                }

                int playerId = online.get(playerIndex - 1);
                if (clientGame.containsKey(playerId)) {
                    send(clientId, idToName.get(playerId) + " is already in a game.");
                    return;
                }
                if (!players.add(playerId)) {
                    send(clientId, idToName.get(playerId) + " was listed more than once.");
                    return;
                }

                team.add(idToPlayer.get(playerId));
                teamClientIds.add(playerId);
            }

            if (team.isEmpty()) {
                send(clientId, "Empty team. " + START_USAGE);
                return;
            }

            teams.add(team);
            teamIds.add(teamClientIds);
        }

        CLIGame game;
        try {
            game = type.factory().apply(teams);
        } catch (RuntimeException e) {
            send(clientId, "Could not start " + type.displayName() + ": " + e.getMessage());
            return;
        }

        GameEntry entry = new GameEntry(nextGameId++, type, teamIds);
        games.put(game, entry);
        for (int playerId : players) {
            clientGame.put(playerId, game);
        }
        for (int playerId : players) {
            send(
                    playerId,
                    "Game #" + entry.id + " (" + type.displayName() + ") started! Type '" + CMD_LEAVE + "' to go back to the main page."
            );
        }

        refreshMenus();
        ((Game<?, ?, ?>) game).start();
    }

    private void leaveGame(int clientId, CLIGame current) {
        if (current == null) {
            send(clientId, "You are not in a game.");
            return;
        }

        removeFromGame(clientId, current);
        send(clientId, "You left the game.");
        refreshMenus();
    }

    // ################################################## Helpers #################################################

    private AvailableGame findGame(String selector) {
        try {
            int index = Integer.parseInt(selector);
            return index >= 1 && index <= availableGames.size() ? availableGames.get(index - 1) : null;
        } catch (NumberFormatException e) {
            for (AvailableGame game : availableGames) {
                if (game.displayName().equalsIgnoreCase(selector)) {
                    return game;
                }
            }
        }

        return null;
    }

    private CLIGame currentGame(int clientId) {
        return clientGame.get(clientId);
    }

    private void removeFromGame(int clientId, CLIGame game) {
        clientGame.remove(clientId);
        game.disconnect(clientId);

        boolean stillHasPlayers = clientGame.values().stream().anyMatch(g -> g == game);
        if (!stillHasPlayers) {
            games.remove(game);
        }
    }

    // ################################################## Display #################################################

    private void refreshMenus() {
        for (int id : new ArrayList<>(online)) {
            if (!awaitingName.contains(id) && currentGame(id) == null) {
                sendMainPage(id);
            }
        }
    }

    private void sendMainPage(int clientId) {
        List<String> table = mergeColumns(
                new Column(COL_GAMES, buildGamesColumn()),
                new Column(COL_RUNNING, buildRunningColumn()),
                new Column(COL_ONLINE, buildOnlineColumn(clientId))
        );

        int width = table.isEmpty() ? 20 : table.get(0).length();
        String border = "=".repeat(width);

        send(clientId, CLEAR_SCREEN);
        send(clientId, "");
        send(clientId, border);
        send(clientId, centered("Grindstone Playground", width));
        send(clientId, border);
        send(clientId, "");
        for (String line : table) {
            send(clientId, line);
        }
        for (int i = 0; i < 5; i++) {
            send(clientId, "");
        }
        for (String line : buildStartHelp()) {
            send(clientId, line);
        }
        send(clientId, "");
    }

    private List<String> buildStartHelp() {
        List<String> lines = new ArrayList<>();
        lines.add("How to start a game:");
        lines.add("  1. Pick a game from GAMES, by its number or name.");
        lines.add("  2. Pick the players for each team from ONLINE, by their number.");
        lines.add("  3. Type: " + CMD_START + " <game> <team1> <team2> ...");
        lines.add("       - players on the same team are separated by ','");
        lines.add("       - teams are separated by a space");
        lines.add("");
        lines.add("  Example: " + CMD_START + " 1 1 2       -> game 1, player 1 vs player 2");
        lines.add("  Example: " + CMD_START + " 1 1,2 3,4   -> game 1, team {1,2} vs team {3,4}");
        return lines;
    }

    private static String centered(String text, int width) {
        if (text.length() >= width) {
            return text;
        }

        int padding = width - text.length();
        int left = padding / 2;
        int right = padding - left;
        return " ".repeat(left) + text + " ".repeat(right);
    }

    private List<String> buildGamesColumn() {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < availableGames.size(); i++) {
            lines.add((i + 1) + ". " + availableGames.get(i).displayName());
        }
        return lines;
    }

    private List<String> buildRunningColumn() {
        if (games.isEmpty()) {
            return List.of("none");
        }

        List<String> lines = new ArrayList<>();
        for (GameEntry entry : games.values()) {
            lines.add("#" + entry.id + " " + entry.type.displayName());

            boolean singleMemberTeams = entry.teams.stream().allMatch(team -> team.size() == 1);
            for (int i = 0; i < entry.teams.size(); i++) {
                List<Integer> team = entry.teams.get(i);
                if (singleMemberTeams) {
                    lines.add("  - " + idToName.get(team.get(0)));
                    continue;
                }

                lines.add("  - Team" + (i + 1) + ":");
                for (int playerId : team) {
                    lines.add("    - " + idToName.get(playerId));
                }
            }
        }
        return lines;
    }

    private List<String> buildOnlineColumn(int viewerId) {
        if (online.isEmpty()) {
            return List.of("none");
        }

        List<Integer> sorted = online.stream().sorted(Comparator.comparing(clientGame::containsKey)) // free players first
                .toList();

        List<String> lines = new ArrayList<>();
        for (int playerId : sorted) {
            lines.add(playerLabel(playerId, viewerId));
        }
        return lines;
    }

    private String playerLabel(int playerId, int viewerId) {
        String label = (online.indexOf(playerId) + 1) + ". " + idToName.get(playerId);
        if (playerId == viewerId) {
            label += " (you)";
        }

        CLIGame game = clientGame.get(playerId);
        if (game != null) {
            GameEntry entry = games.get(game);
            label += " (#" + (entry != null ? entry.id : "?") + ")";
        }

        return label;
    }

    // ############################################### Column layout ###############################################

    private record Column(String header, List<String> lines) {

    }

    private List<String> mergeColumns(Column... columns) {
        int[] widths = new int[columns.length];
        int rows = 0;
        for (int c = 0; c < columns.length; c++) {
            widths[c] = columns[c].header().length();
            for (String line : columns[c].lines()) {
                widths[c] = Math.max(widths[c], line.length());
            }
            rows = Math.max(rows, columns[c].lines().size());
        }

        List<String> result = new ArrayList<>();
        result.add(joinRow(columns, widths, Column::header));
        result.add(joinRow(columns, widths, column -> "-".repeat(headerWidth(columns, widths, column))));

        for (int r = 0; r < rows; r++) {
            int row = r;
            result.add(joinRow(columns, widths, c -> row < c.lines().size() ? c.lines().get(row) : ""));
        }

        return result;
    }

    private String joinRow(Column[] columns, int[] widths, java.util.function.Function<Column, String> cellText) {
        StringBuilder line = new StringBuilder();
        for (int c = 0; c < columns.length; c++) {
            line.append(pad(cellText.apply(columns[c]), widths[c]));
            if (c < columns.length - 1) {
                line.append(" | ");
            }
        }
        return line.toString();
    }

    private static int headerWidth(Column[] columns, int[] widths, Column column) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i] == column) {
                return widths[i];
            }
        }
        return 0;
    }

    private static String pad(String s, int width) {
        return s + " ".repeat(Math.max(0, width - s.length()));
    }

    private void send(CLIPlayer p, String message) {
        send(p.id, message);
    }

    private void send(int clientId, String message) {
        Server.sendMessage(clientId, message);
    }

}
