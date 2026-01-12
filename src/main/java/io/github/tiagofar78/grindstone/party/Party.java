package io.github.tiagofar78.grindstone.party;

import java.util.Collection;

public interface Party {

    Collection<String> getMembers();

    default int size() {
        return getMembers().size();
    }

    Party copy();

    boolean wasCopiedFrom(Party party);

    boolean canQueueUp(String memberName);

    boolean canStopQueue(String memberName);

}
