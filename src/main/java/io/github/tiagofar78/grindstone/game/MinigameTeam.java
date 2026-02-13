package io.github.tiagofar78.grindstone.game;

import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;

public class MinigameTeam<T extends MinigamePlayer> {

    private String name;
    private NamedTextColor chatColor;

    private List<T> members;

    private boolean isWinner = false;

    public MinigameTeam(TeamPreset preset) {
        this(preset, new ArrayList<>());
    }

    public MinigameTeam(TeamPreset preset, List<T> members) {
        this(preset.getDisplayName(), preset.getChatColor(), members);
    }

    public MinigameTeam(String name, NamedTextColor chatColor) {
        this(name, chatColor, new ArrayList<>());
    }

    public MinigameTeam(String name, NamedTextColor chatColor, List<T> members) {
        this.name = name;
        this.chatColor = chatColor;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public NamedTextColor getChatColor() {
        return chatColor;
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

    public boolean isWinner() {
        return isWinner;
    }

    public void won() {
        isWinner = true;
    }

}
