package io.github.tiagofar78.grindstone.web.playground.commands;

import io.github.tiagofar78.grindstone.game.Game;
import io.github.tiagofar78.grindstone.game.Player;
import io.github.tiagofar78.grindstone.web.WebPlayer;
import io.github.tiagofar78.grindstone.web.network.messages.Inbound;
import io.github.tiagofar78.grindstone.web.playground.Playground;

public interface GameCommand<G extends Game<?, ?, ?>, P extends Player<?>, T extends Inbound> extends PlaygroundCommand<T> {

    @SuppressWarnings("unchecked")
    @Override
    default boolean execute(Playground playground, WebPlayer player, T data) {
        return execute((G) player.getCurrentGame().getGame(), (P) player.getCorePlayer(), data);
    }

    boolean execute(G game, P player, T data);

}
