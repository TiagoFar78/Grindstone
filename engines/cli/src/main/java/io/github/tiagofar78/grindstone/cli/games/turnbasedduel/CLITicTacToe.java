package io.github.tiagofar78.grindstone.cli.games.turnbasedduel;

import io.github.tiagofar78.grindstone.cli.CLIGame;
import io.github.tiagofar78.grindstone.cli.CLIPlayer;
import io.github.tiagofar78.grindstone.cli.CLIServices;
import io.github.tiagofar78.grindstone.game.Game;
import io.github.tiagofar78.grindstone.game.GameDependencies;
import io.github.tiagofar78.grindstone.game.MessagesChannel;
import io.github.tiagofar78.grindstone.game.Player;
import io.github.tiagofar78.grindstone.games.turnbasedduel.TicTacToe;

public class CLITicTacToe extends TicTacToe implements CLIGame {
    
    private final static Character X = 'X';
    private final static Character O = 'O';

    public static Game create() {
        GameDependencies dependencies = new GameDependencies(CLIServices.scheduler);
        return new CLITicTacToe(dependencies);
    }
    
    public CLITicTacToe(GameDependencies dependencies) {
        super(dependencies, new CLIPlayer(0), new CLIPlayer(1));
    }

    @Override
    public void process(int id, String[] args) {
        if (args.length != 2 || !isDigit(args[0]) || !isDigit(args[1])) {
            getPlayer(id).sendTranslatedMessage("Usage: <row> <col>", MessagesChannel.CHAT);
            return;
        }
        
        play(id, Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }
    
    private boolean isDigit(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public void disconnect(int id) {
        removePlayerFromGame(getPlayer(id));
    }

    @Override
    public void updateGrid(int row, int col, int playerIndex) {
        String board = drawBoard();
        for (Player player : getLobby().getPlayers()) {
            showGrid(player, board);
        }
        
        for (Player player : getLobby().getSpectators()) {
            showGrid(player, board);
        }
    }
    
    private String drawBoard() {
        StringBuilder sb = new StringBuilder();
        int[][] board = getBoard();

        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                char symbol = board[r][c] == 0 ? X : board[r][c] == 1 ? O : ' ';
                sb.append(" ").append(symbol).append(" ");

                if (c < BOARD_SIZE - 1) {
                    sb.append("|");
                }
            }

            if (r < BOARD_SIZE - 1) {
                sb.append("\n");
                for (int c = 0; c < BOARD_SIZE; c++) {
                    sb.append("---");
                    if (c < BOARD_SIZE - 1) {
                        sb.append("+");
                    }
                }
                sb.append("\n");
            }
        }

        return sb.toString();
    }
    
    private void showGrid(Player p, String grid) {
        p.sendTranslatedMessage(grid, MessagesChannel.CHAT);
    }

}
