package io.github.tiagofar78.grindstone.party;

import java.util.Collection;

public interface Party {

    boolean isLeader(String playerName);

    Collection<String> getMembers();

    default int size() {
        return getMembers().size();
    }

}
