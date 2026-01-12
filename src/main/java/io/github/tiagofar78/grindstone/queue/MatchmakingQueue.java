package io.github.tiagofar78.grindstone.queue;

import io.github.tiagofar78.grindstone.game.MinigameFactory;
import io.github.tiagofar78.grindstone.game.MinigameMap;
import io.github.tiagofar78.grindstone.game.MinigameSettings;
import io.github.tiagofar78.grindstone.party.Party;

import java.util.List;

public abstract class MatchmakingQueue {

    private MinigameFactory factory;
    private MinigameSettings settings;
    private List<MinigameMap> availableMaps;

    public MatchmakingQueue(MinigameFactory factory, MinigameSettings settings, List<MinigameMap> availableMaps) {
        this.factory = factory;
        this.settings = settings;
        this.availableMaps = availableMaps;
    }

    public MinigameFactory getFactory() {
        return factory;
    }

    public MinigameSettings getSettings() {
        return settings;
    }

    public List<MinigameMap> getMaps() {
        return availableMaps;
    }

    protected abstract void enqueue(Party party, MinigameMap map);

    protected abstract void dequeue(Party party);

    protected int maxPartySize() {
        return settings.maxPartySize();
    }

}
