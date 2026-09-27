package io.github.tiagofar78.grindstone.games.tictactoe;

import io.github.tiagofar78.grindstone.game.MessagesChannel;
import io.github.tiagofar78.grindstone.game.phases.OngoingPhase;

public class TicTacToePhase extends OngoingPhase<TicTacToe> {

    public TicTacToePhase(TicTacToe game) {
        super(game);
    }

    @Override
    public void start() {
        TicTacToe g = getGame();
        g.gameEngine.updateGrid(g, 0, 0, 0);
        
        g.getPlayer(0).sendMessage("TurnBasedDuel.your_turn", MessagesChannel.TOP_BAR);
        g.getPlayer(1).sendMessage("TurnBasedDuel.other_turn", MessagesChannel.TOP_BAR, g.getPlayer(0));
    }

}
