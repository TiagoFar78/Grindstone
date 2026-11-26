package io.github.tiagofar78.grindstone.web.games.blindtag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.github.tiagofar78.grindstone.game.Team;
import io.github.tiagofar78.grindstone.games.blindtag.BTPlayer;
import io.github.tiagofar78.grindstone.web.WebGame;
import io.github.tiagofar78.grindstone.web.player.WebPlayer;
import io.github.tiagofar78.grindstone.web.playground.AvailableGame;

public class WebBTFactory {

    private static final AvailableGame factory = build();

    public static AvailableGame get() {
        return factory;
    }

    private static AvailableGame build() {
        return new AvailableGame(
                WebBlindTag.NAME,
                WebBTFactory::create,
                WebBTFactory::checkTeams
        );
    }

    private static WebGame create(List<List<WebPlayer>> teams) {
        List<Team<BTPlayer>> coreTeams = new ArrayList<>();
        for (List<WebPlayer> team : teams) {
            List<BTPlayer> coreTeam = new ArrayList<>();
            for (WebPlayer user : team) {
                BTPlayer corePlayer = new BTPlayer(user, UUID.randomUUID());
                coreTeam.add(corePlayer);
                // This part is done here just because it is easier. It would be preferable
                // to be done inside StartGameCommand, but that would be much harder.
                user.setCorePlayer(corePlayer);
            }

            coreTeams.add(new Team<>(coreTeam));
        }

        return new WebBlindTag(coreTeams);
    }

    private static String checkTeams(List<List<WebPlayer>> teams) {
        if (teams.size() < 2) {
            return "There must exist at least two teams";
        }

        return null;
    }

}
