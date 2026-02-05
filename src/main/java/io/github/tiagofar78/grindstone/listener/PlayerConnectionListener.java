package io.github.tiagofar78.grindstone.listener;

import io.github.tiagofar78.grindstone.game.GamesManager;
import io.github.tiagofar78.grindstone.game.Minigame;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        String playerName = e.getPlayer().getName();
        Minigame game = GamesManager.getGame(playerName);
        if (game == null) {
            return;
        }

        game.playerRejoin(playerName);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        String playerName = e.getPlayer().getName();
        Minigame game = GamesManager.getGame(playerName);
        if (game == null) {
            return;
        }

        game.playerLeft(playerName);
    }

}
