package io.github.tiagofar78.grindstone.games.blindtag.items;

import io.github.tiagofar78.grindstone.games.blindtag.BTPlayer;
import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.map.Cell;

public class Witch extends Item {

    private BTPlayer target;

    public void setTarget(BTPlayer target) {
        this.target = target;
    }

    @Override
    public void use(BlindTag game, BTPlayer player) {
        if (target == null) {
            // TODO Send error message to player.
            return;
        }

        Cell destination = game.getMap().getRandomNonTeleportCell(game.getRandom());
        game.moveAndApplyEffects(player, destination);
    }
}
