package io.github.tiagofar78.grindstone.web.playground.commands;

import io.github.tiagofar78.grindstone.web.network.messages.Inbound;
import io.github.tiagofar78.grindstone.web.network.messages.Inbound.Rejoin;
import io.github.tiagofar78.grindstone.web.player.WebPlayer;
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
        if (!player.canRejoin()) {
            // TODO Send "Already in a game or last game already ended" message
            return true;
        }

        GameSession lastSession = player.getCurrentGame();
        if (!lastSession.canReconnect(player)) {
            player.sendMessage(new Outbound.Error("The previous game is not rejoinable."));
            return true;
        }

        lastSession.playerReconnected(player);
        player.rejoin();
        playground.refreshMenus();
        return true;
    }

}
