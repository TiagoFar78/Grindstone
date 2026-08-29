package io.github.tiagofar78.grindstone.games.blindtag.items;

import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

public class Trap extends Item {

    public static final int GOLD_STEAL = 3;

    private BTPlayer placer;

    public Trap() {
        super();
    }

    public BTPlayer getPlacer() {
        return placer;
    }

    public void trigger(BTPlayer player) {
        if (player == placer) {
            return;
        }

        player.deductGold(GOLD_STEAL);
        placer.addGold(GOLD_STEAL);
    }

    @Override
    public void use(BlindTag game, BTPlayer player) {
        placer = player;
        // Implemented in Task 8
    }
}
