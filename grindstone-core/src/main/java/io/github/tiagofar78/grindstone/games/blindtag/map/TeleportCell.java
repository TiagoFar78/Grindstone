package io.github.tiagofar78.grindstone.games.blindtag.map;

import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

public class TeleportCell extends Cell {

    private static final int NO_NUMBER = -1;

    public TeleportCell(int row, int col) {
        super(NO_NUMBER, row, col);
    }

    @Override
    public int getNumber() {
        return -1;
    }

    @Override
    public void applyEffect(BlindTag game, BTPlayer player) {
        Cell destination = game.getMap().getRandomNonTeleportCell(game.getRandom());
        game.onTeleported(player, destination);
        game.moveAndApplyEffects(player, destination);
    }
}
