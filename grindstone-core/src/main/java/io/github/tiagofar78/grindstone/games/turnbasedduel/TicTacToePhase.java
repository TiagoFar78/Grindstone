package io.github.tiagofar78.grindstone.games.turnbasedduel;

import io.github.tiagofar78.grindstone.game.Game;

public class TicTacToePhase extends TurnBasedDuelPhase {

    public TicTacToePhase(Game game) {
        super(game);
    }

    @Override
    public void start() {
        TicTacToe g = (TicTacToe) getGame();
        g.updateGrid(0, 0, 0);
        super.start();
    }

}
