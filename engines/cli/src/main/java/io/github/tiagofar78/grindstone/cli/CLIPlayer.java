package io.github.tiagofar78.grindstone.cli;

import java.util.UUID;

import io.github.tiagofar78.grindstone.cli.network.Server;
import io.github.tiagofar78.grindstone.game.MessagesChannel;
import io.github.tiagofar78.grindstone.game.Player;

public class CLIPlayer extends Player {
    
    private int id;

    public CLIPlayer(int id) {
        super(UUID.randomUUID());
        this.id = id;
    }

    @Override
    public void sendTranslatedMessage(String message, MessagesChannel channel) {
        Server.sendMessage(id, message);
    }

}
