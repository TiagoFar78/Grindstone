package io.github.tiagofar78.grindstone.gameengine.bukkit.party;

import java.util.function.Consumer;

public interface PartyProvider {

    boolean isInParty(String playerName);

    Party getParty(String playerName);

    void registerPartySizeChangeEventListener(Consumer<Party> listener);

}
