package io.github.tiagofar78.grindstone.games.blindtag.items;

import io.github.tiagofar78.grindstone.games.blindtag.BTPlayer;
import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.map.Cell;

public class Compass extends Item {

    @Override
    public void use(BlindTag game, BTPlayer player) {
        Cell current = player.getCurrentCell();
        game.getGameEngine().onCompassUse(game, player, current, current.getArrows().values());
    }
}
