package io.github.tiagofar78.grindstone.cli.games.tictactoe;

import io.github.tiagofar78.grindstone.cli.CLIGame;
import io.github.tiagofar78.grindstone.cli.playground.network.Server;
import io.github.tiagofar78.grindstone.game.Game;
import io.github.tiagofar78.grindstone.game.MessagesChannel;
import io.github.tiagofar78.grindstone.games.tictactoe.TTTBridge;
import io.github.tiagofar78.grindstone.games.tictactoe.TTTPlayer;
import io.github.tiagofar78.grindstone.games.tictactoe.TicTacToe;

public class CLITTTBridge implements TTTBridge {

    private final static Character X = 'X';
    private final static Character O = 'O';

    @Override
    public void updateGrid(TicTacToe game, int row, int col, int playerIndex) {
        String board = drawBoard(game.getBoard());
        for (TTTPlayer player : game.getLobby().getPlayers()) {
            showGrid(player, board);
        }

        for (TTTPlayer player : game.getLobby().getSpectators()) {
            showGrid(player, board);
        }
    }

    private String drawBoard(int[][] board) {
        StringBuilder sb = new StringBuilder();
        int boardSize = board.length;

        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                char symbol = board[r][c] == 0 ? X : board[r][c] == 1 ? O : ' ';
                sb.append(" ").append(symbol).append(" ");

                if (c < boardSize - 1) {
                    sb.append("|");
                }
            }

            if (r < boardSize - 1) {
                sb.append("\n");
                for (int c = 0; c < boardSize; c++) {
                    sb.append("---");
                    if (c < boardSize - 1) {
                        sb.append("+");
                    }
                }
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    private void showGrid(TTTPlayer p, String grid) {
        p.sendMessage(grid, MessagesChannel.CHAT);
    }

    @Override
    public void disable(Game<?, ?, ?> game) {
        Server.playground.removeGame((CLIGame) game);
    }

}
