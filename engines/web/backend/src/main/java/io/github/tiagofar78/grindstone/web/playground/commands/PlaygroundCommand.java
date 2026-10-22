package io.github.tiagofar78.grindstone.web.playground.commands;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.tiagofar78.grindstone.web.WebPlayer;
import io.github.tiagofar78.grindstone.web.playground.Playground;

public interface PlaygroundCommand extends WebCommand {

    void execute(Playground playground, WebPlayer player, JsonNode message);

}
