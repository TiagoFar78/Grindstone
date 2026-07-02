package io.github.tiagofar78.grindstone.gameengine.bukkit.game;

public class GamePlayer {

    private String name;

    private boolean isSpectator;

    public GamePlayer(String playerName) {
        name = playerName;
    }

    public String getName() {
        return name;
    }

    public boolean isSpectator() {
        return isSpectator;
    }

    public void setSpectator(boolean isSpectator) {
        this.isSpectator = isSpectator;
    }

}
