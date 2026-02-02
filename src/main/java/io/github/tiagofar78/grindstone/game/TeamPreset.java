package io.github.tiagofar78.grindstone.game;

import org.bukkit.ChatColor;

public enum TeamPreset {

    RED("Red", ChatColor.RED),
    BLUE("Blue", ChatColor.BLUE),
    GREEN("Green", ChatColor.BLUE),
    YELLOW("Yellow", ChatColor.YELLOW),
    AQUA("AQUA", ChatColor.AQUA),
    GRAY("GRAY", ChatColor.DARK_GRAY),
    WHITE("WHITE", ChatColor.WHITE),
    PINK("PINK", ChatColor.LIGHT_PURPLE);

    private final String displayName;
    private final ChatColor chatColor;

    TeamPreset(String displayName, ChatColor chatColor) {
        this.displayName = displayName;
        this.chatColor = chatColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

}
