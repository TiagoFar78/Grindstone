package io.github.tiagofar78.grindstone.queue;

import io.github.tiagofar78.grindstone.game.MinigameFactory;
import io.github.tiagofar78.grindstone.game.MinigameMap;
import io.github.tiagofar78.grindstone.game.MinigameSettings;
import io.github.tiagofar78.grindstone.party.Party;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LobbyBasedQueue extends MatchmakingQueue {

    // TODO Maybe change the priority system to make players with no map preference to be sent to where there are more players

    private Queue<Lobby> queue = new LinkedList<>();

    protected LobbyBasedQueue(MinigameFactory factory, MinigameSettings settings, List<MinigameMap> availableMaps) {
        super(factory, settings, availableMaps);
    }

    @Override
    public void enqueue(Party party, MinigameMap map) {
        for (Lobby lobby : queue) {
            if (lobby.add(party, map)) {
                return;
            }
        }

        Lobby lobby = new Lobby(this, getFactory(), getSettings(), getMaps(), party);
        queue.add(lobby);
    }

    @Override
    public void dequeue(Party party) {
        Lobby lobby = remove(party);
        if (lobby != null && lobby.isEmpty()) {
            queue.remove(lobby);
            lobby.delete();
        }
    }

    private Lobby remove(Party party) {
        for (Lobby lobby : queue) {
            if (lobby.remove(party)) {
                return lobby;
            }
        }

        return null;
    }

    protected void transferedToGame(Lobby lobby) {
        queue.remove(lobby);
    }

}
