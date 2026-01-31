package io.github.tiagofar78.grindstone.game;

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

    public abstract MinigameMap create(Location referenceBlock);

}
