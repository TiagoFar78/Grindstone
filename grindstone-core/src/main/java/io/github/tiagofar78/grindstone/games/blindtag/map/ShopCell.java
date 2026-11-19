package io.github.tiagofar78.grindstone.games.blindtag.map;

import io.github.tiagofar78.grindstone.games.blindtag.BTPlayer;
import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;

public class ShopCell extends Cell {

    public ShopCell(int number, int row, int col) {
        super(number, row, col);
    }

    @Override
    public void applyEffect(BlindTag game, BTPlayer player) {
        game.openShop(player);
    }
}
