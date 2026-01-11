package io.github.tiagofar78.grindstone.party;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class SinglePlayerParty implements Party {

    private String playerName;

    public SinglePlayerParty(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public boolean isLeader(String playerName) {
        return true;
    }

    @Override
    public Collection<String> getMembers() {
        List<String> members = new ArrayList<>();
        members.add(playerName);
        return members;
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
        return Objects.hash(playerName);
    }


}
