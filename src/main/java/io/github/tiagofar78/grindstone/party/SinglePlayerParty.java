package io.github.tiagofar78.grindstone.party;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SinglePlayerParty implements Party {

    private String playerName;

    public SinglePlayerParty(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public UUID getId() {
        return UUID.fromString(playerName);
    }

    @Override
    public Collection<String> getMembers() {
        List<String> members = new ArrayList<>();
        members.add(playerName);
        return members;
    }

    @Override
    public Party copy() {
        return null;
    }

    @Override
    public boolean wasCopiedFrom(Party party) {
        return party instanceof SinglePlayerParty s && this.getId().equals(s.getId());
    }

    @Override
    public boolean canQueueUp(String memberName) {
        return true;
    }

    @Override
    public boolean canStopQueue(String memberName) {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        return o instanceof SinglePlayerParty s && playerName.equals(s.playerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
