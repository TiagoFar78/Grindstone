package io.github.tiagofar78.grindstone.web.playground.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import io.github.tiagofar78.grindstone.game.MessagesChannel;
import io.github.tiagofar78.grindstone.web.WebPlayer;
import io.github.tiagofar78.grindstone.web.network.JsonFormatter;
import io.github.tiagofar78.grindstone.web.network.messages.Inbound;
import io.github.tiagofar78.grindstone.web.playground.Playground;

public interface PlaygroundCommand<T extends Inbound> {

    Class<T> inputType();

    boolean execute(Playground playground, WebPlayer player, T data);

    default boolean parseAndExecute(Playground playground, WebPlayer player, JsonNode node) {
        T data;
        try {
            data = JsonFormatter.getMapper().treeToValue(node, inputType());
        } catch (JsonProcessingException | IllegalArgumentException e) {
            String msg = "Could not create a java object of type " + inputType().getCanonicalName() + " from the json " + node.toString();
            player.sendMessage(msg, MessagesChannel.CHAT);
            return false;
        }
        
        return execute(playground, player, data);
    }

}
