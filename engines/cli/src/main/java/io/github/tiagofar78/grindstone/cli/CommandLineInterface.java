package io.github.tiagofar78.grindstone.cli;

import io.github.tiagofar78.grindstone.cli.games.tictactoe.CLITicTacToe;
import io.github.tiagofar78.grindstone.games.tictactoe.TicTacToe;

public class CommandLineInterface {
    
    private static final String START_GAME_MESSAGE = "start";
    
    private CLIGame game;
    
    public void process(int id, String[] args) {
        if (args[0].equalsIgnoreCase(START_GAME_MESSAGE)) {
            game = (CLIGame) CLITicTacToe.create();
            System.out.println("Starting game...");
            ((TicTacToe) game).start();
        }
        else {
            game.process(id, args);
        }
    }

    public void disconnect(int id) {
        game.disconnect(id);
    }

}
