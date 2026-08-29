package io.github.tiagofar78.grindstone.games.blindtag.items;

import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

public class Clone extends Item {

    private BTPlayer cloned;

    public Clone() {
        super();
    }

    public BTPlayer getCloned() {
        return cloned;
    }

    @Override
    public void use(BlindTag game, BTPlayer player) {
        // Implemented in Task 9
    }
}
