package io.github.tiagofar78.grindstone.game;

import org.bukkit.Location;

public abstract class MinigameMap {

    private String name;
    private Location referenceBlock;

    public MinigameMap(String name, Location referenceBlock) {
        this.name = name;
        this.referenceBlock = referenceBlock;
    }

    public String getName() {
        return name;
    }

    public Location getReferenceBlock() {
        return referenceBlock;
    }

    public abstract void load();

}
