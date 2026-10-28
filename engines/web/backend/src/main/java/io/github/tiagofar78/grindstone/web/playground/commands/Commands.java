package io.github.tiagofar78.grindstone.web.playground.commands;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.tiagofar78.grindstone.web.WebPlayer;
import io.github.tiagofar78.grindstone.web.playground.Playground;

public class Commands<T extends PlaygroundCommand<?>> {

    private final Map<String, T> commands = new HashMap<>();

    public void register(String cmdLabel, T messageType) {
        commands.put(cmdLabel, messageType);
    }

    public boolean execute(String cmdLabel, Playground playground, WebPlayer player, JsonNode node) {
        if (!commands.containsKey(cmdLabel)) {
            return false;
        }

        return commands.get(cmdLabel).parseAndExecute(playground, player, node);
    }

}
