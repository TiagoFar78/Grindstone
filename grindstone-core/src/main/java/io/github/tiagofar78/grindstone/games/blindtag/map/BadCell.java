package io.github.tiagofar78.grindstone.games.blindtag.map;

import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

/** Spins the unlucky wheel for the landing player. */
public class BadCell extends Cell {

    public BadCell(int number, int row, int col) {
        super(number, row, col);
    }

    @Override
    public void applyEffect(BlindTag game, BTPlayer player) {
        // Implemented in Task 6
    }
}
