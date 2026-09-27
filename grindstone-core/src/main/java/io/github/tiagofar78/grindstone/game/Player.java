package io.github.tiagofar78.grindstone.game;

import java.util.Locale;
import java.util.UUID;

import io.github.tiagofar78.grindstone.enginebridge.player.Messager;

public abstract class Player<B extends Messager> {
    
    private B playerEngine;
    private UUID uuid;
    
    private Game<?, ?, ?> game;
    
    public Player(B playerBridge, UUID uuid) {
        playerEngine = playerBridge;
        this.uuid = uuid;
    }
    
    public UUID getUUID() {
        return uuid;
    }
    
    public Game<?, ?, ?> getGame() {
        return game;
    }
    
    protected void setGame(Game<?, ?, ?> game) {
        this.game = game;
    }
    
    public void sendMessage(String key, MessagesChannel channel, Object... args) {
        GameDependencies services = getGame().getDependencies();
        Locale playerLocale = services.getPlayerLocaleService().getLocale(getUUID());
        String translatedMessage = services.getTranslationService().translate(playerLocale, key);
        // TODO Formatting missing
        playerEngine.sendMessage(translatedMessage, channel);
    }

}
