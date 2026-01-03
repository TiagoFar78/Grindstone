package io.github.tiagofar78.grindstone.game;

import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;

public class MinigameTeam<T extends MinigamePlayer> {

    private String name;
    private Color color;
    private int maxSize;

    private List<T> members;

    public MinigameTeam(String name, Color color, int maxSize) {
        this.name = name;
        this.color = color;
        this.members = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public int maxSize() {
        return maxSize;
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
