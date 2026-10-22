package io.github.tiagofar78.grindstone.web.games.tictactoe;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.tiagofar78.grindstone.game.GameDependencies;
import io.github.tiagofar78.grindstone.games.tictactoe.TTTBridge;
import io.github.tiagofar78.grindstone.games.tictactoe.TTTPlayer;
import io.github.tiagofar78.grindstone.games.tictactoe.TicTacToe;
import io.github.tiagofar78.grindstone.web.WebGame;
import io.github.tiagofar78.grindstone.web.WebPlayer;
import io.github.tiagofar78.grindstone.web.playground.commands.Commands;
import io.github.tiagofar78.grindstone.web.playground.commands.GameCommand;

public class WebTicTacToe extends TicTacToe implements WebGame {

    private final Commands<GameCommand<TicTacToe, TTTPlayer>> commands = new Commands<>();

    public WebTicTacToe(GameDependencies dependencies, TTTBridge bridge, TTTPlayer p1, TTTPlayer p2) {
        super(dependencies, bridge, p1, p2);
        commands.register("play", new PlaceMarkCommand());
    }

    @Override
    public String getName() {
        return "TicTacToe";
    }

    @Override
    public boolean executeCommand(String cmdLabel, WebPlayer player, JsonNode node) {
        GameCommand<TicTacToe,TTTPlayer> cmd = commands.find(cmdLabel);
        if (cmd == null) {
            return false;
        }

        cmd.execute(this, (TTTPlayer) player.getCorePlayer(), node);
        return true;
    }

    @Override
    public void disconnect(WebPlayer player) {
        removePlayerFromGame((TTTPlayer) player.getCorePlayer());
    }

}
