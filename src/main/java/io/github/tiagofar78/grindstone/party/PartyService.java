package io.github.tiagofar78.grindstone.party;

public final class PartyService {

    private static PartyProvider provider;

    private PartyService() {
    }

    public static void registerProvider(PartyProvider partyProvider) {
        provider = partyProvider;
    }

    public static Party getParty(String playerName) {
        if (provider == null || !provider.isInParty(playerName)) {
            return new SinglePlayerParty(playerName);
        }

        return provider.getParty(playerName);
    }

}
