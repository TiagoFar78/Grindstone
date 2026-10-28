package io.github.tiagofar78.grindstone.web.playground.commands;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.tiagofar78.grindstone.web.PlayerState;
import io.github.tiagofar78.grindstone.web.WebGame;
import io.github.tiagofar78.grindstone.web.WebPlayer;
import io.github.tiagofar78.grindstone.web.network.messages.Inbound;
import io.github.tiagofar78.grindstone.web.network.messages.Inbound.Start;
import io.github.tiagofar78.grindstone.web.network.messages.Outbound;
import io.github.tiagofar78.grindstone.web.playground.AvailableGame;
import io.github.tiagofar78.grindstone.web.playground.GameSession;
import io.github.tiagofar78.grindstone.web.playground.Playground;

public class StartGameCommand implements PlaygroundCommand<Inbound.Start> {

    @Override
    public Class<Start> inputType() {
        return Inbound.Start.class;
    }

    @Override
    public boolean execute(Playground playground, WebPlayer sender, Start data) {
        int gameIndex = data.gameIndex();
        AvailableGame type = playground.findAvailableGame(gameIndex);
        if (type == null) {
            sender.sendMessage(new Outbound.Error("No with index '" + gameIndex + "'."));
            return true;
        }

        List<WebPlayer> registry = playground.getPlayerRegistry();
        List<List<Integer>> playersIndexes = data.teams();

        if (playersIndexes.isEmpty()) {
            sender.sendMessage(new Outbound.Error("Choose the players first."));
            return true;
        }

        if (playersIndexes.stream().anyMatch(List::isEmpty)) {
            sender.sendMessage(new Outbound.Error("At least one of the teams is empty."));
            return true;
        }

        Set<Integer> invalidIndexes = invalidPlayerIndexes(playersIndexes, registry);
        if (invalidIndexes.size() != 0) {
            String setString = joinIndexes(invalidIndexes);
            sender.sendMessage(new Outbound.Error("Invalid player indexes: " + setString + ". Choose players with an index in the player list"));
            return true;
        }

        Set<Integer> repeatedIndexes = repeatedIndexes(playersIndexes);
        if (!repeatedIndexes.isEmpty()) {
            String playersString = describePlayers(repeatedIndexes, registry);
            sender.sendMessage(new Outbound.Error("There are repeated players: " + playersString));
            return true;
        }

        Set<Integer> playersInGame = playersInGame(playersIndexes, registry);
        if (!playersInGame.isEmpty()) {
            String playersString = describePlayers(playersInGame, registry);
            sender.sendMessage(new Outbound.Error("Some players are still in a game: " + playersString));
            return true;
        }

        List<List<WebPlayer>> teams = playersIndexes.stream().map(t -> t.stream().map(registry::get).toList()).toList();

        String teamsFormationProblem = type.teamsChecker().apply(teams);
        if (teamsFormationProblem != null) {
            sender.sendMessage(new Outbound.Error(teamsFormationProblem));
            return true;
        }

        WebGame newGame = type.factory().apply(teams);
        GameSession session = playground.createSession(newGame, teams);
        for (List<WebPlayer> team : teams) {
            for (WebPlayer p : team) {
                p.setState(PlayerState.IN_GAME);
                p.setCurrentGame(session);
                // p.setCorePlayer is done inside the type.factory().apply() just because it is easier
                // if I already had the core player available at this time it would be preferable to
                // do that here.
            }
        }

        playground.refreshMenus();
        return true;
    }

    private Set<Integer> invalidPlayerIndexes(List<List<Integer>> playersIndexes, List<WebPlayer> registry) {
        return playersIndexes.stream()
                .flatMap(List::stream)
                .filter(i -> i < 0 || i >= registry.size())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<Integer> repeatedIndexes(List<List<Integer>> playersIndexes) {
        Set<Integer> seen = new HashSet<>();
        return playersIndexes.stream()
                .flatMap(List::stream)
                .filter(i -> !seen.add(i))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<Integer> playersInGame(List<List<Integer>> playersIndexes, List<WebPlayer> registry) {
        return playersIndexes.stream()
                .flatMap(List::stream)
                .filter(i -> registry.get(i).getState() == PlayerState.IN_GAME)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String joinIndexes(Set<Integer> indexes) {
        return indexes.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

    private String describePlayers(Set<Integer> indexes, List<WebPlayer> registry) {
        return indexes.stream()
                .map(i -> i + ". " + registry.get(i).getName())
                .collect(Collectors.joining(", "));
    }

}
