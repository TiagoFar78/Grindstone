package io.github.tiagofar78.grindstone.web.playground.commands;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.tiagofar78.grindstone.game.Rejoinable;
import io.github.tiagofar78.grindstone.web.PlayerState;
import io.github.tiagofar78.grindstone.web.WebPlayer;
import io.github.tiagofar78.grindstone.web.playground.GameSession;
import io.github.tiagofar78.grindstone.web.playground.OutboundMessages;
import io.github.tiagofar78.grindstone.web.playground.Playground;

public class RejoinCommand implements PlaygroundCommand {

    @Override
    public void execute(Playground playground, WebPlayer player, JsonNode message) {
        if (player.getState() != PlayerState.IN_GAME) {
            // TODO Send error message
            return;
        }

        GameSession lastSession = player.getCurrentGame();
        if (lastSession == null || !lastSession.isAlive()) {
            player.sendMessage(OutboundMessages.buildErrorToast("You have no game to rejoin."));
            return;
        }

        if (!(lastSession.getGame() instanceof Rejoinable)) {
            player.sendMessage(OutboundMessages.buildErrorToast("The game you were at is not rejoinable."));
            return;
        }

        lastSession.playerReconnected(player);
        player.setState(PlayerState.IN_GAME);

        playground.refreshMenus();
    }

}
