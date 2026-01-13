package io.github.tiagofar78.grindstone.party;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.function.Consumer;

public final class SinglePlayerPartyListener implements Listener {

    protected SinglePlayerPartyListener() {
    }

    private static Consumer<Party> partySizeChangeListener;

    public static void registerPartySizeChangeEventListener(Consumer<Party> listener) {
        partySizeChangeListener = listener;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        String playerName = e.getPlayer().getName();
        if (PartyService.getParty(playerName) instanceof SinglePlayerParty s) {
            partySizeChangeListener.accept(s);
        }
    }

}
