package io.github.tiagofar78.grindstone.game;

import org.bukkit.Location;

public abstract class MinigameMap {

    private Location referenceBlock;

    public MinigameMap(Location referenceBlock) {
        this.referenceBlock = referenceBlock;
    }

    public Location getReferenceBlock() {
        return referenceBlock;
    }

    public abstract void load();

}
