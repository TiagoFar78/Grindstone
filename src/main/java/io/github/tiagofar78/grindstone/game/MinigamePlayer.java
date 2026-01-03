package io.github.tiagofar78.grindstone.game;

import io.github.tiagofar78.grindstone.adapters.BukkitMinigamePlayerAdapter;
import io.github.tiagofar78.grindstone.adapters.MinigamePlayerAdapter;

import org.bukkit.entity.Player;

public class MinigamePlayer {

    private MinigamePlayerAdapter playerAdapter;

    private String name;

    public MinigamePlayer(Player player) {
        playerAdapter = new BukkitMinigamePlayerAdapter(player);

        name = player.getName();
    }

    public MinigamePlayerAdapter getPlayerAdapter() {
        return playerAdapter;
    }

    public String getName() {
        return name;
    }

}
