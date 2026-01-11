package io.github.tiagofar78.grindstone.party;

public interface PartyProvider {

    boolean isInParty(String playerName);

    Party getParty(String playerName);

}
