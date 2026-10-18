package io.github.tiagofar78.grindstone.web.playground.commands;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.tiagofar78.grindstone.game.Game;
import io.github.tiagofar78.grindstone.game.Player;

public interface GameCommand<G extends Game<?, ?, ?>, P extends Player<?>> extends WebCommand {

    void execute(G game, P player, JsonNode message);

}
