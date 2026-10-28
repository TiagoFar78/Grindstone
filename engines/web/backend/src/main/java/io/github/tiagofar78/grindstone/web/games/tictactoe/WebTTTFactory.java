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
        WebPlayer wp1 = teams.get(0).getFirst();
        WebPlayer wp2 = teams.get(1).getFirst();
        TTTPlayer p1 = new TTTPlayer(wp1, UUID.randomUUID(), 0);
        TTTPlayer p2 = new TTTPlayer(wp2, UUID.randomUUID(), 1);

        wp1.setCorePlayer(p1); // This part is done here just because it is easier. It would be preferable
        wp2.setCorePlayer(p2); // to be done inside StartGameCommand, but that would be much harder.

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
