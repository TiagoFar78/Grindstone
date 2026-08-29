package io.github.tiagofar78.grindstone.games.blindtag.map;

import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

/** Gives the landing player 5 gold. */
public class GoldCell extends Cell {

    public GoldCell(int number, int row, int col) {
        super(number, row, col);
    }

    @Override
    public void applyEffect(BlindTag game, BTPlayer player) {
        // Implemented in Task 6
    }
}
