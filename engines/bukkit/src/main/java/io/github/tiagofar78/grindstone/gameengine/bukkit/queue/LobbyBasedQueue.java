package io.github.tiagofar78.grindstone.gameengine.bukkit.queue;

public class LobbyBasedQueue { /* extends MatchmakingQueue {

    // TODO Maybe change the priority system to make players with no map preference to be sent to where there are more players

    private Queue<Lobby> queue = new LinkedList<>();

    public LobbyBasedQueue(MinigameFactory factory, MinigameSettings settings, List<MapFactory> availableMaps) {
        super(factory, settings, availableMaps);
    }

    @Override
    public void enqueue(Party party, MapFactory map) {
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
    
    */

}
