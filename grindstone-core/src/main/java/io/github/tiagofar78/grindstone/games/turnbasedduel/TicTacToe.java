package io.github.tiagofar78.grindstone.games.turnbasedduel;

import io.github.tiagofar78.grindstone.game.GameDependencies;
import io.github.tiagofar78.grindstone.game.MessagesChannel;
import io.github.tiagofar78.grindstone.game.Player;
import io.github.tiagofar78.grindstone.game.phases.Phase;

public abstract class TicTacToe extends TurnBasedDuel {

    public static final int BOARD_SIZE = 3;
    private static final int EMPTY = -1;
    
    private int[][] board;
    private int turn;
    
    public TicTacToe(GameDependencies dependencies, Player p1, Player p2) {
        super(dependencies, p1, p2);
    }
    
    public int[][] getBoard() {
        return board;
    }
    
    @Override
    public void load() {
        this.board = new int[BOARD_SIZE][BOARD_SIZE];
        this.turn = 0;
        
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = EMPTY;
            }
        }
    }
    
    @Override
    public Phase getFirstPhase() {
        return new TicTacToePhase(this);
    }

    public void play(int playerIndex, int row, int col) {
        int currentPlayer = turn % 2;
        if (playerIndex != currentPlayer) {
            getPlayer(playerIndex).sendMessage(Messages.NOT_YOUR_TURN, MessagesChannel.CHAT);
            return;
        }

        if (getCurrentPhase().isClockStopped()) {
            getPlayer(playerIndex).sendMessage(io.github.tiagofar78.grindstone.games.Messages.GAME_PAUSED, MessagesChannel.CHAT);
            return;
        }

        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            getPlayer(playerIndex).sendMessage(Messages.INVALID_POSITION, MessagesChannel.CHAT);
            return;
        }

        if (board[row][col] != EMPTY) {
            getPlayer(playerIndex).sendMessage(Messages.POSITION_OCCUPIED, MessagesChannel.CHAT);
            return;
        }

        board[row][col] = playerIndex;
        turn++;
        
        updateGrid(row, col, playerIndex);
        
        if (hasWon(playerIndex)) {
            setWinner(playerIndex);
            startNextPhase();
        } else if (isBoardFull()) {
            startNextPhase();
        }
    }
    
    public abstract void updateGrid(int row, int col, int playerIndex);

    private boolean hasWon(int playerIndex) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (isRowWin(i, playerIndex) || isColumnWin(i, playerIndex)) {
                return true;
            }
        }
        
        return isMainDiagonalWin(playerIndex) || isAntiDiagonalWin(playerIndex);
    }

    private boolean isRowWin(int row, int playerIndex) {
        for (int col = 0; col < BOARD_SIZE; col++) {
            if (board[row][col] != playerIndex) {
                return false;
            }
        }
        
        return true;
    }

    private boolean isColumnWin(int col, int playerIndex) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            if (board[row][col] != playerIndex) {
                return false;
            }
        }
        
        return true;
    }

    private boolean isMainDiagonalWin(int playerIndex) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][i] != playerIndex) {
                return false;
            }
        }
        
        return true;
    }

    private boolean isAntiDiagonalWin(int playerIndex) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][BOARD_SIZE - 1 - i] != playerIndex) {
                return false;
            }
        }
        
        return true;
    }

    private boolean isBoardFull() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == EMPTY) {
                    return false;
                }
            }
        }
        
        return true;
    }

}
