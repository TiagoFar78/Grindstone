package io.github.tiagofar78.grindstone.game.maps;

import org.bukkit.Location;

public abstract class GameMap {

    private Location referenceBlock;
    private MapSettings settings;

    public GameMap(Location referenceBlock, MapSettings settings) {
        this.referenceBlock = referenceBlock;
        this.settings = settings;
    }

    public Location getReferenceBlock() {
        return referenceBlock;
    }

    public MapSettings getSettings() {
        return settings;
    }

    public abstract void load();

}
