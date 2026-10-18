package io.github.tiagofar78.grindstone.web.playground.commands;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.tiagofar78.grindstone.web.WebGame;
import io.github.tiagofar78.grindstone.web.WebPlayer;

public interface GameCommand extends WebCommand {

    void execute(WebGame game, WebPlayer player, JsonNode message);

}
