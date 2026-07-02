package io.github.tiagofar78.grindstone.game;

import java.util.Set;

public interface Spectatable {
    
    Set<Player> getSpectators();
    
    default boolean isSpectator(Player player) {
        return getSpectators().contains(player);
    }
    
//  >-------------------------{ Add }-------------------------<
    
    default void addSpectator(Player player) {
        sendSpectatorJoinedMessage(player);
        getSpectators().add(player);
        setSpectatorMode(player);
    }

    void sendSpectatorJoinedMessage(Player player);
    
    void setSpectatorMode(Player player);
    
//  >------------------------{ Remove }------------------------<

    default void removeSpectator(Player player) {
        sendSpectatorLeftMessage(player);
        getSpectators().remove(player);
        removeSpectator(player);
    }
    
    void sendSpectatorLeftMessage(Player player);
    
    void removeSpectatorMode(Player player);
    
//  >------------------------{ Other }------------------------<    
    
    void sendSpectatorGameOverMessage();

}
