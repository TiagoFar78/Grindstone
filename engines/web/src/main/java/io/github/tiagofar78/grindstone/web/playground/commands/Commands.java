package io.github.tiagofar78.grindstone.web.playground.commands;

import java.util.HashMap;
import java.util.Map;

public class Commands<T extends WebCommand> {

    private final Map<String, T> commands = new HashMap<>();

    public void register(String type, T command) {
        commands.put(type, command);
    }

    public T find(String type) {
        return commands.get(type);
    }

}
