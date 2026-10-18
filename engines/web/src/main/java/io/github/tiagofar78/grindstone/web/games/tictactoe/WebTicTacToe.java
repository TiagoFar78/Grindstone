package io.github.tiagofar78.grindstone.web.games.tictactoe;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.tiagofar78.grindstone.game.GameDependencies;
import io.github.tiagofar78.grindstone.games.tictactoe.TTTBridge;
import io.github.tiagofar78.grindstone.games.tictactoe.TTTPlayer;
import io.github.tiagofar78.grindstone.games.tictactoe.TicTacToe;
import io.github.tiagofar78.grindstone.web.WebGame;

public class WebTicTacToe extends TicTacToe implements WebGame {

    public WebTicTacToe(GameDependencies dependencies, TTTBridge bridge, TTTPlayer p1, TTTPlayer p2) {
        super(dependencies, bridge, p1, p2);
    }

    @Override
    public String getName() {
        return "TicTacToe";
    }

    @Override
    public void process(int id, JsonNode message) {
        if (!message.path("type").asText("").equals("play")) {
            return;
        }

        JsonNode row = message.get("row");
        JsonNode col = message.get("col");
        if (row == null || col == null || !row.isInt() || !col.isInt()) {
            return;
        }

        play(getPlayer(id), row.asInt(), col.asInt());
    }

    @Override
    public void disconnect(int id) {
        removePlayerFromGame(getPlayer(id));
    }

}
