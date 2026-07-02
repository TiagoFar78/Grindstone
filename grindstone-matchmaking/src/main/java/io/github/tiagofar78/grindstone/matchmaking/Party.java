package io.github.tiagofar78.grindstone.matchmaking;

import java.util.Collections;
import java.util.List;

public abstract class Party<T> {
    
    private int size;
    private List<T> members;
    
    public Party(T member) {
        this(List.of(member));
    }
    
    public Party(List<T> members) {
        this.size = members.size();
        this.members = Collections.unmodifiableList(members);
    }

    public int size() {
        return size;
    }
    
    public List<T> getMembers() {
        return members;
    }

}
