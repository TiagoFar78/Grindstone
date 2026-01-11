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

    protected void enqueue(String party, MinigameMap map) {
        MinigameLobby lobby = addToEligibleLobby(party, map);
        if (lobby != null) {
            if (lobby.isFull(settings)) {
                queue.remove(lobby);
                Coordinator.createMinigame(factory, settings, lobby.getMap(), lobby.getParties());
            }

            return;
        }

        // TODO maybe check if party has more players than allowed on game

        lobby = new MinigameLobby(party.toString(), availableMaps); // Allow the creation of competitive games as well TODO take care of party
        if (lobby.isFull(settings)) {
            Coordinator.createMinigame(factory, settings, lobby.getMap(), lobby.getParties());
        } else {
            queue.add(lobby);
        }
    }

    private MinigameLobby addToEligibleLobby(String party, MinigameMap map) {
        for (MinigameLobby lobby : queue) {
            if (lobby.add(party, map, settings)) {
                return lobby;
            }
        }

        return null;
    }

}
