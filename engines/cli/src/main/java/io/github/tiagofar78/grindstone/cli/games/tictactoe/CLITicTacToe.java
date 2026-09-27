package io.github.tiagofar78.grindstone.cli.games.tictactoe;

import java.util.UUID;

import io.github.tiagofar78.grindstone.cli.CLIGame;
import io.github.tiagofar78.grindstone.cli.CLIPlayer;
import io.github.tiagofar78.grindstone.cli.services.CLIServices;
import io.github.tiagofar78.grindstone.game.GameDependencies;
import io.github.tiagofar78.grindstone.game.MessagesChannel;
import io.github.tiagofar78.grindstone.games.tictactoe.TTTBridge;
import io.github.tiagofar78.grindstone.games.tictactoe.TTTPlayer;
import io.github.tiagofar78.grindstone.games.tictactoe.TicTacToe;

public class CLITicTacToe extends TicTacToe implements CLIGame {

    // TODO Remove this from here to matchmaking when it is done.
    public static CLITicTacToe create() {
        GameDependencies dependencies = new GameDependencies(CLIServices.scheduler);
        TTTPlayer p1 = new TTTPlayer(new CLIPlayer(0), UUID.randomUUID(), 0);
        TTTPlayer p2 = new TTTPlayer(new CLIPlayer(1), UUID.randomUUID(), 1);
        return new CLITicTacToe(dependencies, new CLITTTBridge(), p1, p2);
    }

    public CLITicTacToe(GameDependencies dependencies, TTTBridge bridge, TTTPlayer p1, TTTPlayer p2) {
        super(dependencies, bridge, p1, p2);
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
