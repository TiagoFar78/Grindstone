package io.github.tiagofar78.grindstone.games.blindtag.items;

import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.map.Direction;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

import java.util.List;

public class HawkEye extends Item {

    @Override
    public void use(BlindTag game, BTPlayer player) {
        BTPlayer target;
        if (player == game.getTagger()) {
            target = nearestOtherPlayer(game, player);
        } else {
            target = game.getTagger();
        }

        if (target == null) {
            return;
        }

        List<Direction> path = game.getMap().findShortestPath(
                player.getCurrentCell(), target.getCurrentCell());
        game.onHawkEyeUsed(player, path);
    }

    private BTPlayer nearestOtherPlayer(BlindTag game, BTPlayer player) {
        BTPlayer nearest = null;
        int minPath = Integer.MAX_VALUE;
        for (BTPlayer other : game.getBTPlayers()) {
            if (other == player) {
                continue;
            }

            int dist = game.getMap().findShortestPath(
                    player.getCurrentCell(), other.getCurrentCell()).size();
            if (dist < minPath) {
                minPath = dist;
                nearest = other;
            }
        }

        return nearest;
    }
}
