package io.github.tiagofar78.grindstone.game;

public interface Rejoinable {

    void addPlayerToGame(Player player);
    
    void addPlayerToLobby(Player player);

    default void playerRejoin(Player player) {
        addPlayerToGame(player);
        addPlayerToLobby(player);
        sendPlayerRejoinMessage(player);
    }

    void sendPlayerRejoinMessage(Player player);

}
