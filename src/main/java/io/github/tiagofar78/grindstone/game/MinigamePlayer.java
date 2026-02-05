package io.github.tiagofar78.grindstone.game;

public class MinigamePlayer {

    private String name;

    private boolean isSpectator;

    public MinigamePlayer(String playerName) {
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
