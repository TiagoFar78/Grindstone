package io.github.tiagofar78.grindstone.game;

public abstract class GameMap {

    private String name;

    public GameMap(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void load();

}
