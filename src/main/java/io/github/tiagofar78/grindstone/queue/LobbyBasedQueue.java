package io.github.tiagofar78.grindstone.queue;

import io.github.tiagofar78.grindstone.game.MinigameMap;
import io.github.tiagofar78.grindstone.game.MinigameSettings;
import io.github.tiagofar78.grindstone.party.Party;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LobbyBasedQueue extends MatchmakingQueue {

    // TODO Maybe change the priority system to make players with no map preference to be sent to where there are more players

    private Queue<MinigameLobby> queue = new LinkedList<>();

    protected LobbyBasedQueue(MinigameFactory factory, MinigameSettings settings, List<MinigameMap> availableMaps) {
        super(factory, settings, availableMaps);
    }

    @Override
    public void enqueue(Party party, MinigameMap map) {
        for (MinigameLobby lobby : queue) {
            if (lobby.add(party, map)) {
                return;
            }
        }

        MinigameLobby lobby = new MinigameLobby(getFactory(), getSettings(), getMaps(), party); // TODO Allow the creation of competitive games as well
        queue.add(lobby);
    }

    @Override
    public void dequeue(Party party) {
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
