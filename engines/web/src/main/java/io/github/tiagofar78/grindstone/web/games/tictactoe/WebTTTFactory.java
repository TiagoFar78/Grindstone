package io.github.tiagofar78.grindstone.web.games.tictactoe;

import java.util.List;
import java.util.UUID;

import io.github.tiagofar78.grindstone.game.GameDependencies;
import io.github.tiagofar78.grindstone.games.tictactoe.TTTPlayer;
import io.github.tiagofar78.grindstone.web.WebGame;
import io.github.tiagofar78.grindstone.web.WebPlayer;
import io.github.tiagofar78.grindstone.web.playground.AvailableGame;
import io.github.tiagofar78.grindstone.web.services.WebServices;

public class WebTTTFactory {

    private static final AvailableGame factory = build();

    public static AvailableGame get() {
        return factory;
    }

    private static AvailableGame build() {
        return new AvailableGame(
                "TicTacToe",
                WebTTTFactory::create,
                WebTTTFactory::checkTeams
        );
    }

    private static WebGame create(List<List<WebPlayer>> teams) {
        GameDependencies deps = new GameDependencies(WebServices.scheduler);
        List<WebPlayer> t1 = teams.get(0);
        List<WebPlayer> t2 = teams.get(1);
        TTTPlayer p1 = new TTTPlayer(t1.getFirst(), UUID.randomUUID(), 0);
        TTTPlayer p2 = new TTTPlayer(t2.getFirst(), UUID.randomUUID(), 1);
        return new WebTicTacToe(deps, new WebTTTBridge(), p1, p2);
    }

    private static String checkTeams(List<List<WebPlayer>> teams) {
        if (teams.size() != 2) {
            return "There must exist exactly two teams";
        }

        for (List<WebPlayer> team : teams) {
            if (team.size() != 1) {
                return "Each team can have only one player";
            }
        }

        return null;
    }

}
