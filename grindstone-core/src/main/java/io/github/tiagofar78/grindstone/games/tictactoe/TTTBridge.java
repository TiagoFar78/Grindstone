package io.github.tiagofar78.grindstone.games.tictactoe;

import io.github.tiagofar78.enginebridge.game.GameBridge;

public interface TTTBridge extends GameBridge {

    void updateGrid(TicTacToe game, int row, int col, int playerIndex);
    
}
