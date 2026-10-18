package io.github.tiagofar78.grindstone.web.games.tictactoe;

import java.util.Map;

import io.github.tiagofar78.grindstone.game.Game;
import io.github.tiagofar78.grindstone.game.MessagesChannel;
import io.github.tiagofar78.grindstone.games.tictactoe.TTTBridge;
import io.github.tiagofar78.grindstone.games.tictactoe.TTTPlayer;
import io.github.tiagofar78.grindstone.games.tictactoe.TicTacToe;
import io.github.tiagofar78.grindstone.web.WebGame;
import io.github.tiagofar78.grindstone.web.playground.network.WebServer;

public class WebTTTBridge implements TTTBridge {

    @Override
    public void updateGrid(TicTacToe game, int row, int col, int playerIndex) {
        String message = WebServer.toJson(Map.of(
            "type", "board",
            "board", game.getBoard(),
            "row", row,
            "col", col,
            "playerIndex", playerIndex
        ));

        for (TTTPlayer player : game.getLobby().getPlayers()) {
            player.sendMessage(message, MessagesChannel.CHAT);
        }

        for (TTTPlayer player : game.getLobby().getSpectators()) {
            player.sendMessage(message, MessagesChannel.CHAT);
        }
    }

    @Override
    public void disable(Game<?, ?, ?> game) {
        WebServer.playground.removeGame((WebGame) game);
    }

}
