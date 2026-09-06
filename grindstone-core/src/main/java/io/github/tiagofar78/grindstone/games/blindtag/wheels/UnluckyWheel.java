package io.github.tiagofar78.grindstone.games.blindtag.wheels;

import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.map.Direction;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

import java.util.List;
import java.util.Random;

public class UnluckyWheel {

    private static final int GIVE_GOLD_AMOUNT = 2;
    private static final int LOSE_GOLD_AMOUNT = 3;

    public static void spin(BlindTag game, BTPlayer player) {
        Random random = game.getRandom();
        int outcome = random.nextInt(6);

        switch (outcome) {
            case 0 -> {
                player.setCurrentCell(game.getMap().getSpawnCell());
            }
            case 1 -> {
                for (BTPlayer other : game.getBTPlayers()) {
                    if (other != player) {
                        player.takeGold(GIVE_GOLD_AMOUNT);
                        other.addGold(GIVE_GOLD_AMOUNT);
                    }
                }
            }
            case 2 -> {
                player.takeGold(LOSE_GOLD_AMOUNT);
            }
            case 3 -> {
                BTPlayer target = pathRevealTarget(game, player);
                if (target != null) {
                    List<Direction> path = game.getMap().findShortestPath(
                            player.getCurrentCell(), target.getCurrentCell());
                    game.onPathReveal(player, path);
                }
            }
            case 4 -> {
                game.getMap().addArrow(random);
                game.getMap().addArrow(random);
            }
            case 5 -> {
                spin(game, player);
                spin(game, player);
            }
        }
    }

    private static BTPlayer pathRevealTarget(BlindTag game, BTPlayer player) {
        if (player == game.getTagger()) {
            return nearestPlayer(game, player);
        }

        return game.getTagger();
    }

    private static BTPlayer nearestPlayer(BlindTag game, BTPlayer player) {
        BTPlayer nearest = null;
        int minDist = Integer.MAX_VALUE;
        for (BTPlayer other : game.getBTPlayers()) {
            if (other == player) {
                continue;
            }

            int dist = game.getMap().findShortestPath(
                    player.getCurrentCell(), other.getCurrentCell()).size();
            if (dist < minDist) {
                minDist = dist;
                nearest = other;
            }
        }

        return nearest;
    }
}
