package io.github.tiagofar78.grindstone.web.games.tictactoe;

import io.github.tiagofar78.grindstone.web.network.messages.Outbound;

public record TTTUpdateGridMessage(
        int[][] board, String winner, String playerPlaying, boolean isYourTurn
) implements Outbound.GameUpdateData {
}
