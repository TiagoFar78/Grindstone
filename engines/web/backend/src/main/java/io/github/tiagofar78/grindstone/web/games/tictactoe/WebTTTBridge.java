package io.github.tiagofar78.grindstone.web.games.tictactoe;

import io.github.tiagofar78.grindstone.game.Game;
import io.github.tiagofar78.grindstone.games.tictactoe.TTTBridge;
import io.github.tiagofar78.grindstone.games.tictactoe.TTTPlayer;
import io.github.tiagofar78.grindstone.games.tictactoe.TicTacToe;
import io.github.tiagofar78.grindstone.web.WebGame;
import io.github.tiagofar78.grindstone.web.WebPlayer;
import io.github.tiagofar78.grindstone.web.network.WebServer;
import io.github.tiagofar78.grindstone.web.network.messages.Outbound;
import io.github.tiagofar78.grindstone.web.network.messages.WebMessage;

public class WebTTTBridge implements TTTBridge {

    @Override
    public void updateGrid(TicTacToe game, int row, int col, int playerIndex) {
        TTTUpdateGridMessage boardUpdate = new TTTUpdateGridMessage(row, col, playerIndex);
        WebMessage message = new Outbound.GameUpdate(boardUpdate);

        for (TTTPlayer player : game.getLobby().getPlayers()) {
            ((WebPlayer) player.getEngine()).sendMessage(message);
        }

        for (TTTPlayer player : game.getLobby().getSpectators()) {
            ((WebPlayer) player.getEngine()).sendMessage(message);
        }
    }

    @Override
    public void disable(Game<?, ?, ?> game) {
        WebServer.playground.removeGame((WebGame) game);
    }

}
