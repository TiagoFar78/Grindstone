package io.github.tiagofar78.grindstone.games.blindtag.items;

import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.map.Arrow;
import io.github.tiagofar78.grindstone.games.blindtag.map.Cell;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Needy extends Item {

    @Override
    public void use(BlindTag game, BTPlayer player) {
        List<BTPlayer> others = game.getBTPlayers().stream()
                .filter(p -> p != player)
                .toList();

        if (others.isEmpty()) {
            return;
        }

        Random random = game.getRandom();
        BTPlayer chosen = others.get(random.nextInt(others.size()));

        List<Cell> reachable = new ArrayList<>();
        for (Arrow arrow : chosen.getCurrentCell().getArrows().values()) {
            Cell dest = arrow.destination();
            if (!reachable.contains(dest)) {
                reachable.add(dest);
            }
        }

        if (reachable.isEmpty()) {
            return;
        }

        game.onNeedyUsed(player);
        Cell destination = reachable.get(random.nextInt(reachable.size()));
        game.moveAndApplyEffects(player, destination);
    }
}
