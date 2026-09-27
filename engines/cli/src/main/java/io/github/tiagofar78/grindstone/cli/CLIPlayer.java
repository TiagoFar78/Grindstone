package io.github.tiagofar78.grindstone.cli;

import io.github.tiagofar78.enginebridge.player.Messager;
import io.github.tiagofar78.grindstone.cli.network.Server;
import io.github.tiagofar78.grindstone.game.MessagesChannel;

public class CLIPlayer implements Messager {
    
    private int id;

    public CLIPlayer(int id) {
        this.id = id;
    }

    @Override
    public void sendMessage(String message, MessagesChannel channel) {
        Server.sendMessage(id, message);
    }

}
