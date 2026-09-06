package io.github.tiagofar78.grindstone.games.blindtag.wheels;

import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

import java.util.Random;

public class LuckyWheel {

    private static final int STEAL_GOLD_AMOUNT = 1;
    private static final int GAIN_GOLD_AMOUNT = 3;

    public static void spin(BlindTag game, BTPlayer player) {
        Random random = game.getRandom();
        int outcome = random.nextInt(6);

        switch (outcome) {
            case 0 -> {
                player.addGold(player.gold());
            }
            case 1 -> {
                for (BTPlayer other : game.getBTPlayers()) {
                    if (other != player) {
                        other.takeGold(STEAL_GOLD_AMOUNT);
                        player.addGold(STEAL_GOLD_AMOUNT);
                    }
                }
            }
            case 2 -> {
                player.addGold(GAIN_GOLD_AMOUNT);
            }
            case 3 -> {
                game.openShop(player);
            }
            case 4 -> {
                game.onHorseUsed(player);
            }
            case 5 -> {
                UnluckyWheel.spin(game, player);
            }
        }
    }
}
