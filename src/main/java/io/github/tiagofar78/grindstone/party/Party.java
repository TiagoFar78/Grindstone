package io.github.tiagofar78.grindstone.party;

import java.util.Collection;
import java.util.UUID;

public interface Party {

    UUID getId();

    Collection<String> getMembers();

    default int size() {
        return getMembers().size();
    }

    Party copy();

    boolean wasCopiedFrom(Party party);

    boolean canQueueUp(String memberName);

    boolean canStopQueue(String memberName);

}
