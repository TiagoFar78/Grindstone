package io.github.tiagofar78.grindstone.games.blindtag.items;

import io.github.tiagofar78.grindstone.games.blindtag.BTPlayer;
import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;

public class Horse extends Item {

    @Override
    public void use(BlindTag game, BTPlayer player) {
        game.onHorseUsed(player);
    }
}
