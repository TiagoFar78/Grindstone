package io.github.tiagofar78.grindstone.web.playground.commands;

import io.github.tiagofar78.grindstone.web.network.messages.Inbound;
import io.github.tiagofar78.grindstone.web.player.WebPlayer;
import io.github.tiagofar78.grindstone.web.playground.Playground;

public class LeaveCommand implements PlaygroundCommand<Inbound.Leave> {

    @Override
    public Class<Inbound.Leave> inputType() {
        return Inbound.Leave.class;
    }

    @Override
    public boolean execute(Playground playground, WebPlayer player, Inbound.Leave data) {
        if (!player.isInGame()) {
            // TODO Send "Not in a game" to user
            return true;
        }

        player.leaveGame();
        player.getCurrentSession().playerLeft(player);
        playground.refreshMenus();
        return true;
    }

}
