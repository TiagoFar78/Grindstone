package io.github.tiagofar78.grindstone.game;

import org.bukkit.entity.Player;

public class MinigamePlayer {

    private String _name;

    public MinigamePlayer(Player player) {
        this._name = player.getName();
    }

    public String getName() {
        return _name;
    }

}
