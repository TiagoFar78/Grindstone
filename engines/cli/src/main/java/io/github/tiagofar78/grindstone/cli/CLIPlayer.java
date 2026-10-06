package io.github.tiagofar78.grindstone.cli;

import io.github.tiagofar78.grindstone.cli.playground.network.Server;
import io.github.tiagofar78.grindstone.enginebridge.player.Messager;
import io.github.tiagofar78.grindstone.game.MessagesChannel;

public class CLIPlayer implements Messager {
    
    public final int id;

    public CLIPlayer(int id) {
        this.id = id;
    }

    @Override
    public void sendMessage(String message, MessagesChannel channel) {
        Server.sendMessage(id, message);
    }

}
