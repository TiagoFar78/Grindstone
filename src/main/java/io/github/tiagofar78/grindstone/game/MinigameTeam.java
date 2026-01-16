package io.github.tiagofar78.grindstone.game;

import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;

public class MinigameTeam<T extends MinigamePlayer> {

    private String name;
    private Color color;

    private List<T> members;

    public MinigameTeam(String name, Color color) {
        this(name, color, new ArrayList<>());
    }

    public MinigameTeam(String name, Color color, List<T> members) {
        this.name = name;
        this.color = color;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public List<T> getMembers() {
        return members;
    }

    public void addMember(T member) {
        members.add(member);
    }

    public void removeMember(T member) {
        members.remove(member);
    }

}
