package io.github.tiagofar78.grindstone.games.blindtag.items;

import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

public class Trap extends Item {

    public static final int GOLD_STEAL = 3;

    private BTPlayer placer;

    public BTPlayer getPlacer() {
        return placer;
    }

    public void trigger(BTPlayer victim) {
        if (victim == placer) {
            return;
        }
        victim.takeGold(GOLD_STEAL);
        placer.addGold(GOLD_STEAL);
    }

    @Override
    public void use(BlindTag game, BTPlayer player) {
        placer = player;
        player.getCurrentCell().addTrap(this);
        game.onTrapPlaced(player, player.getCurrentCell());
    }
}
