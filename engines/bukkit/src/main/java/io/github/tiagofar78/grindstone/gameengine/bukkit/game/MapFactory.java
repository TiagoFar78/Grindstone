package io.github.tiagofar78.grindstone.gameengine.bukkit.game;

import org.bukkit.Location;

public abstract class MapFactory {

    private String name;
    private String worldName;

    public MapFactory(String name, String worldName) {
        this.name = name;
        this.worldName = worldName;
    }

    public String getName() {
        return name;
    }

    public String getWorldName() {
        return worldName;
    }

    public abstract int indexInWorld();

    public abstract GameMap create(Location referenceBlock);

}
