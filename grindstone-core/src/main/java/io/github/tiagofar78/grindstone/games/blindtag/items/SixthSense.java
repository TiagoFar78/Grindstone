package io.github.tiagofar78.grindstone.games.blindtag.items;

import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.map.Cell;
import io.github.tiagofar78.grindstone.games.blindtag.map.Direction;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

public class SixthSense extends Item {

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

        Direction hint = directionToward(player.getCurrentCell(), target.getCurrentCell());
        game.onSixthSenseUsed(player, hint);
    }

    private BTPlayer nearestOtherPlayer(BlindTag game, BTPlayer player) {
        BTPlayer nearest = null;
        int minDist = Integer.MAX_VALUE;
        for (BTPlayer other : game.getBTPlayers()) {
            if (other == player) {
                continue;
            }

            int dist = gridDist(player.getCurrentCell(), other.getCurrentCell());
            if (dist < minDist) {
                minDist = dist;
                nearest = other;
            }
        }

        return nearest;
    }

    private int gridDist(Cell a, Cell b) {
        return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
    }

    private Direction directionToward(Cell from, Cell to) {
        int dr = Integer.signum(to.getRow() - from.getRow());
        int dc = Integer.signum(to.getCol() - from.getCol());
        for (Direction d : Direction.values()) {
            if (d.rowDelta() == dr && d.colDelta() == dc) {
                return d;
            }
        }

        return Direction.N;
    }
}
