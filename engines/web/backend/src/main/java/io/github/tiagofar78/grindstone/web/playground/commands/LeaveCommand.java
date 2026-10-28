package io.github.tiagofar78.grindstone.web.playground.commands;

import io.github.tiagofar78.grindstone.web.PlayerState;
import io.github.tiagofar78.grindstone.web.WebPlayer;
import io.github.tiagofar78.grindstone.web.network.messages.Inbound;
import io.github.tiagofar78.grindstone.web.playground.GameSession;
import io.github.tiagofar78.grindstone.web.playground.Playground;

public class LeaveCommand implements PlaygroundCommand<Inbound.Leave> {

    @Override
    public Class<Inbound.Leave> inputType() {
        return Inbound.Leave.class;
    }

    @Override
    public boolean execute(Playground playground, WebPlayer player, Inbound.Leave data) {
        if (player.getState() == PlayerState.IN_LOBBY) {
            // TODO Send message to user
            return true;
        }

        GameSession currentSession = player.getCurrentGame();
        if (currentSession == null) {
            // TODO Send message to user
            return true;
        }

        currentSession.playerLeft(player);
        playground.refreshMenus();
        return true;
    }

}
