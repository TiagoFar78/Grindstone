package io.github.tiagofar78.grindstone.web.games.tictactoe;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.tiagofar78.grindstone.games.tictactoe.TTTPlayer;
import io.github.tiagofar78.grindstone.games.tictactoe.TicTacToe;
import io.github.tiagofar78.grindstone.web.playground.commands.GameCommand;

public class PlaceMarkCommand implements GameCommand<TicTacToe, TTTPlayer> {

    @Override
    public void execute(TicTacToe game, TTTPlayer player, JsonNode message) {
        JsonNode rowNode = message.get("row");
        JsonNode colNode = message.get("col");
        if (rowNode == null || colNode == null || !rowNode.isInt() || !colNode.isInt()) {
            return;
        }

        game.play(player, rowNode.asInt(), colNode.asInt());
    }

}
