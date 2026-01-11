package io.github.tiagofar78.grindstone.game;

import org.bukkit.Location;

public abstract class MinigameMap {

    private String name;
    private Location referenceBlock;

    public MinigameMap(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Location getReferenceBlock() {
        return referenceBlock;
    }

    public void setReferenceBlock(Location referenceBlock) {
        this.referenceBlock = referenceBlock;
    }

    public abstract void load();

}
