package io.github.tiagofar78.grindstone.web.playground.commands;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.tiagofar78.grindstone.web.PlayerState;
import io.github.tiagofar78.grindstone.web.WebPlayer;
import io.github.tiagofar78.grindstone.web.playground.GameSession;
import io.github.tiagofar78.grindstone.web.playground.Playground;

public class LeaveCommand implements PlaygroundCommand {

    @Override
    public void execute(Playground playground, WebPlayer player, JsonNode message) {
        if (player.getState() == PlayerState.IN_LOBBY) {
            // TODO Send message to user
            return;
        }

        GameSession currentSession = player.getCurrentGame();
        if (currentSession == null) {
            // TODO Send message to user
            return;
        }

        currentSession.playerLeft(player);
        playground.refreshMenus();
    }

}
