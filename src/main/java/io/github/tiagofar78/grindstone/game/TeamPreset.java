package io.github.tiagofar78.grindstone.game;

import net.kyori.adventure.text.format.NamedTextColor;

public enum TeamPreset {

    RED("Red", NamedTextColor.RED),
    BLUE("Blue", NamedTextColor.BLUE),
    GREEN("Green", NamedTextColor.BLUE),
    YELLOW("Yellow", NamedTextColor.YELLOW),
    AQUA("AQUA", NamedTextColor.AQUA),
    GRAY("GRAY", NamedTextColor.DARK_GRAY),
    WHITE("WHITE", NamedTextColor.WHITE),
    PINK("PINK", NamedTextColor.LIGHT_PURPLE);

    private final String displayName;
    private final NamedTextColor chatColor;

    TeamPreset(String displayName, NamedTextColor chatColor) {
        this.displayName = displayName;
        this.chatColor = chatColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public NamedTextColor getChatColor() {
        return chatColor;
    }

}
