package io.github.tiagofar78.grindstone.games.turnbasedduel;

import io.github.tiagofar78.grindstone.game.MessageKey;

public enum Messages implements MessageKey {
    
    YOU_LEFT("you_left"),
    PLAYER_LEFT("player_left"),
    VICTORY("victory"),
    DEFEAT("defeat"),
    DRAW("draw"),
    YOUR_TURN("your_turn"),
    OTHER_TURN("other_turn"),
    NOT_YOUR_TURN("not_your_turn"),
    INVALID_POSITION("invalid_position"),
    POSITION_OCCUPIED("position_occupied");
    
    public final static String PREFIX = "TurnBasedDuel.";
    
    private String key;
    
    Messages(String key) {
        this.key = PREFIX + key;
    }

    @Override
    public String getKey() {
        return key;
    }

}
