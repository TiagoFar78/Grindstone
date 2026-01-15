package io.github.tiagofar78.grindstone.party;

import io.github.tiagofar78.grindstone.Grindstone;
import io.github.tiagofar78.grindstone.queue.QueuesManager;

import java.util.function.Consumer;

public final class PartyService {

    private static final Consumer<Party> partySizeChangeListener = party -> QueuesManager.dequeue(party);

    private static PartyProvider provider;

    private PartyService() {
    }

    public static void registerProvider(PartyProvider partyProvider) {
        provider = partyProvider;
        provider.registerPartySizeChangeEventListener(partySizeChangeListener);
    }

    public static Party getParty(String playerName) {
        if (provider == null || !provider.isInParty(playerName)) {
            return new SinglePlayerParty(playerName);
        }

        return provider.getParty(playerName);
    }

    public static void registerFallbackProviderListener() {
        SinglePlayerPartyListener.registerPartySizeChangeEventListener(partySizeChangeListener);
        Grindstone.registerListener(new SinglePlayerPartyListener());
    }

}
