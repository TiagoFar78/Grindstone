package io.github.tiagofar78.grindstone.web.games.tictactoe;

import io.github.tiagofar78.grindstone.game.Game;
import io.github.tiagofar78.grindstone.games.tictactoe.TTTBridge;
import io.github.tiagofar78.grindstone.games.tictactoe.TTTPlayer;
import io.github.tiagofar78.grindstone.games.tictactoe.TicTacToe;
import io.github.tiagofar78.grindstone.web.WebGame;
import io.github.tiagofar78.grindstone.web.network.WebServer;
import io.github.tiagofar78.grindstone.web.network.messages.Outbound;
import io.github.tiagofar78.grindstone.web.network.messages.WebMessage;
import io.github.tiagofar78.grindstone.web.player.WebPlayer;

public class WebTTTBridge implements TTTBridge {

    @Override
    public void updateGrid(TicTacToe game, int row, int col, int playerIndex) {
        String gameName = ((WebTicTacToe) game).getName();
        int[][] board = game.getBoard();
        String winner = game.getWinner() == null
                ? null
                : toWebPlayer(game.getWinner().getMembers().stream().findFirst().get()).getName();
        String playingName = getWebPlayer(game.getTurn() % 2, game).getName();

        for (TTTPlayer player : game.getLobby().getPlayers()) {
            boolean isYourTurn = player.getIndex() == game.getTurn() % 2;
            sendUpdateGridMessage(player, gameName, board, winner, playingName, isYourTurn);
        }

        for (TTTPlayer player : game.getLobby().getSpectators()) {
            sendUpdateGridMessage(player, gameName, board, winner, playingName, false);
        }
    }

    private void sendUpdateGridMessage(
            TTTPlayer player,
            String gameName,
            int[][] board,
            String winner,
            String playerPlaying,
            boolean isYourTurn
    ) {
        TTTUpdateGridMessage boardUpdate = new TTTUpdateGridMessage(board, winner, playerPlaying, isYourTurn);
        WebMessage message = new Outbound.GameUpdate(gameName, boardUpdate);
        toWebPlayer(player).sendMessage(message);
    }

    private WebPlayer getWebPlayer(int playerIndex, TicTacToe game) {
        TTTPlayer player = game.getTeams().get(playerIndex).getMembers().stream().findAny().get();
        return toWebPlayer(player);
    }

    private WebPlayer toWebPlayer(TTTPlayer player) {
        return (WebPlayer) player.getEngine();
    }

    @Override
    public void disable(Game<?, ?, ?> game) {
        WebServer.playgroundManager.removeGame((WebGame) game);
    }

}
