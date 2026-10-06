package io.github.tiagofar78.grindstone.games.tictactoe;

import java.util.List;

import io.github.tiagofar78.grindstone.game.Game;
import io.github.tiagofar78.grindstone.game.GameDependencies;
import io.github.tiagofar78.grindstone.game.MessagesChannel;
import io.github.tiagofar78.grindstone.game.Team;
import io.github.tiagofar78.grindstone.game.phases.FinishedPhase;
import io.github.tiagofar78.grindstone.game.phases.Phase;

public class TicTacToe extends Game<TTTBridge, TTTPlayer, Team<TTTPlayer>> {

    public static final int BOARD_SIZE = 3;
    private static final int EMPTY = -1;
    
    private int[][] board;
    private int turn;
    private int winner = -1;
    
    public TicTacToe(GameDependencies dependencies, TTTBridge bridge, TTTPlayer p1, TTTPlayer p2) {
        Team<TTTPlayer> p1Team = new Team<>(List.of(p1));
        Team<TTTPlayer> p2Team = new Team<>(List.of(p2));
        super(dependencies, bridge, List.of(p1Team, p2Team));
    }
    
    public int[][] getBoard() {
        return board;
    }

    protected TTTPlayer getPlayer(int index) {
        return getTeams().get(index).getMembers().stream().findFirst().get();
    }

    private TTTPlayer getOtherPlayer(int index) {
        return getPlayer((index + 1) % 2);
    }

    private TTTPlayer getOtherPlayer(TTTPlayer p) {
        return getOtherPlayer(p.getIndex());
    }
    
    private void setWinner(int playerIndex) {
        winner = playerIndex;
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
    public Phase<TicTacToe> getFirstPhase() {
        return new TicTacToePhase(this);
    }

    public void play(TTTPlayer player, int row, int col) {
        int playerIndex = player.getIndex();
        int currentPlayer = turn % 2;
        if (playerIndex != currentPlayer) {
            getPlayer(playerIndex).sendMessage("TurnBasedDuel.not_your_turn", MessagesChannel.CHAT);
            return;
        }

        if (getCurrentPhase().isClockStopped()) {
            getPlayer(playerIndex).sendMessage("game_paused", MessagesChannel.CHAT);
            return;
        }

        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            getPlayer(playerIndex).sendMessage("TurnBasedDuel.invalid_position", MessagesChannel.CHAT);
            return;
        }

        if (board[row][col] != EMPTY) {
            getPlayer(playerIndex).sendMessage("TurnBasedDuel.position_occupied", MessagesChannel.CHAT);
            return;
        }

        board[row][col] = playerIndex;
        turn++;
        
        gameEngine.updateGrid(this, row, col, playerIndex);
        
        if (hasWon(playerIndex)) {
            setWinner(playerIndex);
            startNextPhase();
        } else if (isBoardFull()) {
            startNextPhase();
        }
    }

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

    @Override
    public void sendLoadingMessage() {
        // Empty, it loads instantly, no need to send loading message
    }

    @Override
    public void sendPlayerLeftMessage(TTTPlayer player) {
        player.sendMessage("TurnBasedDuel.you_left", MessagesChannel.TITLE);
        getOtherPlayer(player).sendMessage("TurnBasedDuel.player_left", MessagesChannel.CHAT, player);
    }

    @Override
    public void sendGameOverMessages() {
        if (winner != -1) {
            getPlayer(winner).sendMessage("TurnBasedDuel.victory", MessagesChannel.TITLE);
            getOtherPlayer(winner).sendMessage("TurnBasedDuel.defeat", MessagesChannel.TITLE);
        }
        else {
            getPlayer(0).sendMessage("TurnBasedDuel.draw", MessagesChannel.TITLE);
            getPlayer(1).sendMessage("TurnBasedDuel.draw", MessagesChannel.TITLE);
        }
    }

    @Override
    public void removePlayerFromGame(TTTPlayer player) {
        setWinner(getOtherPlayer(player).getIndex());
        startNextPhase();
    }

}
