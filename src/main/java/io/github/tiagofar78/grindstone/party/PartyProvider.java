package io.github.tiagofar78.grindstone.party;

import java.util.function.Consumer;

public interface PartyProvider {

    boolean isInParty(String playerName);

    Party getParty(String playerName);

    void registerPartySizeChangeEventListener(Consumer<Party> listener);

}
