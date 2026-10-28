package io.github.tiagofar78.grindstone.web.playground.commands;

import io.github.tiagofar78.grindstone.game.Rejoinable;
import io.github.tiagofar78.grindstone.web.PlayerState;
import io.github.tiagofar78.grindstone.web.WebPlayer;
import io.github.tiagofar78.grindstone.web.network.messages.Inbound;
import io.github.tiagofar78.grindstone.web.network.messages.Inbound.Rejoin;
import io.github.tiagofar78.grindstone.web.network.messages.Outbound;
import io.github.tiagofar78.grindstone.web.playground.GameSession;
import io.github.tiagofar78.grindstone.web.playground.Playground;

public class RejoinCommand implements PlaygroundCommand<Inbound.Rejoin> {

    @Override
    public Class<Rejoin> inputType() {
        return Inbound.Rejoin.class;
    }

    @Override
    public boolean execute(Playground playground, WebPlayer player, Inbound.Rejoin data) {
        if (player.getState() != PlayerState.IN_GAME) {
            // TODO Send error message
            return true;
        }

        GameSession lastSession = player.getCurrentGame();
        if (lastSession == null || !lastSession.canReconnect(player)) {
            player.sendMessage(new Outbound.Error("You have no game to rejoin."));
            return true;
        }

        if (!(lastSession.getGame() instanceof Rejoinable)) {
            player.sendMessage(new Outbound.Error("The game you were at is not rejoinable."));
            return true;
        }

        lastSession.playerReconnected(player);
        player.setState(PlayerState.IN_GAME);

        playground.refreshMenus();
        return true;
    }

}
