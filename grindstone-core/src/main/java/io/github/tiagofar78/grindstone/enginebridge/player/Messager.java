package io.github.tiagofar78.grindstone.enginebridge.player;

import io.github.tiagofar78.grindstone.game.MessagesChannel;

public interface Messager {

    void sendMessage(String message, MessagesChannel channel);

}
