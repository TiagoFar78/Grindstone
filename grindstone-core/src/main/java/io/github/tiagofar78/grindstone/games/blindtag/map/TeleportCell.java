package io.github.tiagofar78.grindstone.games.blindtag.map;

import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

/**
 * Teleports the landing player to a random non-Teleport real cell.
 *
 * <p>This cell type has no guessable number â€” players can never stand on it long
 * enough to read it. {@link #getNumber()} therefore throws
 * {@link UnsupportedOperationException}.
 */
public class TeleportCell extends Cell {

    private static final int NO_NUMBER = -1;

    public TeleportCell(int row, int col) {
        super(NO_NUMBER, row, col);
    }

    /**
     * @throws UnsupportedOperationException always â€” TeleportCell has no guessable number.
     */
    @Override
    public int getNumber() {
        throw new UnsupportedOperationException("TeleportCell has no guessable number");
    }

    @Override
    public void applyEffect(BlindTag game, BTPlayer player) {
        // Implemented in Task 6
    }
}
