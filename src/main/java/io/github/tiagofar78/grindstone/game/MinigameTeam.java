package io.github.tiagofar78.grindstone.game;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class MinigameTeam<T extends MinigamePlayer> {

    private String name;
    private ChatColor chatColor;

    private List<T> members;

    public MinigameTeam(TeamPreset preset) {
        this(preset, new ArrayList<>());
    }

    public MinigameTeam(TeamPreset preset, List<T> members) {
        this(preset.getDisplayName(), preset.getChatColor(), members);
    }

    public MinigameTeam(String name, ChatColor chatColor) {
        this(name, chatColor, new ArrayList<>());
    }

    public MinigameTeam(String name, ChatColor chatColor, List<T> members) {
        this.name = name;
        this.chatColor = chatColor;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public ChatColor getChatColor() {
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

}
