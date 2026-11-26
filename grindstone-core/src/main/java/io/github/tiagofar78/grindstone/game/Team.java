package io.github.tiagofar78.grindstone.game;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Team<T extends Player<?>> {

    private Set<T> members;

    public Team(List<T> members) {
        this.members = new HashSet<>(members);
    }

    public Collection<T> getMembers() {
        return Collections.unmodifiableCollection(members);
    }

    public boolean contains(T member) {
        return members.contains(member);
    }

}
