package io.github.tiagofar78.grindstone.game;

import java.util.Locale;
import java.util.UUID;

public abstract class Player {
    
    private Game game;
    private UUID uuid;
    
    public Player(UUID uuid) {
        this.uuid = uuid;
    }
    
    public UUID getUUID() {
        return uuid;
    }
    
    public Game getGame() {
        return game;
    }
    
    public void setGame(Game game) {
        this.game = game;
    }
    
    public abstract void sendTranslatedMessage(String message, MessagesChannel channel);
    
    public void sendMessage(MessageKey messageKey, MessagesChannel channel, Object... args) {
        GameDependencies services = getGame().getDependencies();
        Locale playerLocale = services.getPlayerLocaleService().getLocale(getUUID());
        String translatedMessage = services.getTranslationService().translate(playerLocale, messageKey.getKey());
        // TODO Formatting missing
        sendTranslatedMessage(translatedMessage, channel);
    }

}
