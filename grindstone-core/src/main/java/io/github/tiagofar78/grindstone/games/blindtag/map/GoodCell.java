package io.github.tiagofar78.grindstone.games.blindtag.map;

import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;
import io.github.tiagofar78.grindstone.games.blindtag.wheels.LuckyWheel;

public class GoodCell extends Cell {

    public GoodCell(int number, int row, int col) {
        super(number, row, col);
    }

    @Override
    public void applyEffect(BlindTag game, BTPlayer player) {
        LuckyWheel.spin(game, player, 0);
    }
}
