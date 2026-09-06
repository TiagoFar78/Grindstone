package io.github.tiagofar78.grindstone.games.blindtag.items;

import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.map.Cell;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

import java.util.List;

public class Witch extends Item {

    @Override
    public void use(BlindTag game, BTPlayer player) {
        List<BTPlayer> others = game.getBTPlayers().stream()
                .filter(p -> p != player)
                .toList();
        BTPlayer target = game.onWitchTargetNeeded(player, others);

        if (target == null) {
            return;
        }

        Cell destination = game.getMap().getRandomNonTeleportCell(game.getRandom());
        game.moveAndApplyEffects(player, destination);
    }
}
