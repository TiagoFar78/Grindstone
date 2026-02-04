package io.github.tiagofar78.grindstone.game;

import org.bukkit.Location;

public abstract class MinigameMap {

    private String name;

    public MinigameMap(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract Location preparingRoomLocation();

    public abstract void load();

}
