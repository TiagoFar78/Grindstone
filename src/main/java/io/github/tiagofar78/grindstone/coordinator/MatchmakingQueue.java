package io.github.tiagofar78.grindstone.coordinator;

import io.github.tiagofar78.grindstone.game.MinigameMap;
import io.github.tiagofar78.grindstone.game.MinigameSettings;
import io.github.tiagofar78.grindstone.party.Party;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MatchmakingQueue {

    // TODO Maybe change the priority system to make players with no map preference to be sent to where there are more players

    private MinigameFactory factory;
    private MinigameSettings settings;
    private List<MinigameMap> availableMaps;

    private Queue<MinigameLobby> queue = new LinkedList<>();

    protected MatchmakingQueue(MinigameFactory factory, MinigameSettings settings, List<MinigameMap> availableMaps) {
        this.factory = factory;
        this.settings = settings;
        this.availableMaps = availableMaps;
    }

    protected boolean enqueue(Party party, MinigameMap map) {
        if (party.size() > settings.maxPartySize()) {
            return false;
        }


        for (MinigameLobby lobby : queue) {
            if (lobby.add(party, map)) {
                return true;
            }
        }

        MinigameLobby lobby = new MinigameLobby(factory, settings, availableMaps, party); // TODO Allow the creation of competitive games as well
        queue.add(lobby);

        return true;
    }

    protected void dequeue(Party party) {
        MinigameLobby lobby = remove(party);
        if (lobby != null && lobby.isEmpty()) {
            queue.remove(lobby);
        }
    }

    private MinigameLobby remove(Party party) {
        for (MinigameLobby lobby : queue) {
            if (lobby.remove(party)) {
                return lobby;
            }
        }

        return null;
    }

}
