package io.github.tiagofar78.grindstone.cli;

import io.github.tiagofar78.grindstone.cli.games.turnbasedduel.CLITicTacToe;
import io.github.tiagofar78.grindstone.game.Game;

public class CommandLineInterface {
    
    private static final String START_GAME_MESSAGE = "start";
    
    private CLIGame game;
    
    public void process(int id, String[] args) {
        if (args[0].equalsIgnoreCase(START_GAME_MESSAGE)) {
            game = (CLIGame) CLITicTacToe.create();
            ((Game) game).start();
            System.out.println("Started game");
        }
        else {
            game.process(id, args);
        }
    }
    
    public void disconnect(int id) {
        game.disconnect(id);
    }

}
