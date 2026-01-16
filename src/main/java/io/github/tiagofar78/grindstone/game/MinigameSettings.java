package io.github.tiagofar78.grindstone.game;

import java.util.Collection;
import java.util.List;

public interface MinigameSettings {

    int maxPartySize();

    int minPlayers();

    int maxPlayersPerTeam();

    int minTeams();

    int maxTeams();

    default boolean doTeamsFit(List<Collection<String>> teams) {
        return true;
    }

}
