package io.github.tiagofar78.grindstone.web.games.tictactoe;

import io.github.tiagofar78.grindstone.game.MessagesChannel;
import io.github.tiagofar78.grindstone.games.tictactoe.TTTPlayer;
import io.github.tiagofar78.grindstone.games.tictactoe.TicTacToe;
import io.github.tiagofar78.grindstone.web.network.messages.Inbound;
import io.github.tiagofar78.grindstone.web.playground.commands.GameCommand;

public class PlaceMarkCommand implements GameCommand<TicTacToe, TTTPlayer, PlaceMarkCommand.PlaceMarkArgs> {

    record PlaceMarkArgs(int row, int col) implements Inbound {}

    @Override
    public Class<PlaceMarkArgs> inputType() {
        return PlaceMarkCommand.PlaceMarkArgs.class;
    }

    @Override
    public boolean execute(TicTacToe game, TTTPlayer player, PlaceMarkArgs data) {
        int row = data.row();
        if (row < 0 || row >= TicTacToe.BOARD_SIZE) {
            player.sendMessage("Invalid row: " + row, MessagesChannel.ACTION_BAR);
            return true;
        }
        
        int col = data.col();
        if (col < 0 || col >= TicTacToe.BOARD_SIZE) {
            player.sendMessage("Invalid col: " + col, MessagesChannel.ACTION_BAR);
            return true;
        }

        game.play(player, row, col);
        return true;
    }

}
