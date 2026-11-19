package io.github.tiagofar78.grindstone.games.blindtag.items;

import io.github.tiagofar78.grindstone.games.blindtag.BTPlayer;
import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.map.Cell;

public class Clone extends Item {

    private BTPlayer cloned;

    public BTPlayer getCloned() {
        return cloned;
    }

    @Override
    public void use(BlindTag game, BTPlayer player) {
        cloned = player;
        Cell cell = player.getCurrentCell();
        cell.addClone(this);
        game.onClonePlaced(player, cell);
    }
}
