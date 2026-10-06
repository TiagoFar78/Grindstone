package io.github.tiagofar78.grindstone.cli.games.tictactoe;

import io.github.tiagofar78.grindstone.cli.CLIGame;
import io.github.tiagofar78.grindstone.game.GameDependencies;
import io.github.tiagofar78.grindstone.game.MessagesChannel;
import io.github.tiagofar78.grindstone.games.tictactoe.TTTBridge;
import io.github.tiagofar78.grindstone.games.tictactoe.TTTPlayer;
import io.github.tiagofar78.grindstone.games.tictactoe.TicTacToe;

public class CLITicTacToe extends TicTacToe implements CLIGame {

    public CLITicTacToe(GameDependencies dependencies, TTTBridge bridge, TTTPlayer p1, TTTPlayer p2) {
        super(dependencies, bridge, p1, p2);
    }

    @Override
    public String getName() {
        return "TicTacToe";
    }

    @Override
    public void process(int id, String[] args) {
        TTTPlayer p = getPlayer(id);
        if (args.length != 2 || !isDigit(args[0]) || !isDigit(args[1])) {
            p.sendMessage("Usage: <row> <col>", MessagesChannel.CHAT);
            return;
        }
        
        play(p, Integer.parseInt(args[0]), Integer.parseInt(args[1]));
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

}
