package io.github.tiagofar78.grindstone.gameengine.bukkit.game;

import org.bukkit.Location;

public abstract class GameMap {

    private String name;

    public GameMap(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract Location preparingRoomLocation();

    public abstract void load();

}
