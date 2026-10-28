package io.github.tiagofar78.grindstone.web.games.tictactoe;

import io.github.tiagofar78.grindstone.web.network.messages.Outbound;

public record TTTUpdateGridMessage(int row, int col, int playerIndex) implements Outbound.GameUpdateData {}
