package io.github.tiagofar78.grindstone.games.blindtag.map;

import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

public class GoldCell extends Cell {

    private static final int GOLD_AMOUNT = 1;

    public GoldCell(int number, int row, int col) {
        super(number, row, col);
    }

    @Override
    public void applyEffect(BlindTag game, BTPlayer player) {
        player.addGold(GOLD_AMOUNT);
        game.onGoldGained(player, GOLD_AMOUNT);
    }
}
