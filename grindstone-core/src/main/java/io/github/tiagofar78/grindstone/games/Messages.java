package io.github.tiagofar78.grindstone.games;

import io.github.tiagofar78.grindstone.game.MessageKey;

public enum Messages implements MessageKey {

    GAME_PAUSED("game_paused");
    
    private String key;
    
    Messages(String key) {
        this.key = key;
    }
    
    @Override
    public String getKey() {
        return key;
    }

}
