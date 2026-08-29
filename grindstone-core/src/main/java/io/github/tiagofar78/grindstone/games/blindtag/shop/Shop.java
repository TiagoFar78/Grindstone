package io.github.tiagofar78.grindstone.games.blindtag.shop;

import java.util.HashMap;
import java.util.Map;

import io.github.tiagofar78.grindstone.games.blindtag.items.Item;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

public class Shop {

    private static final int INITIAL_PRICE = 1;

    private Map<String, Integer> costs;
    
    public Shop() {
        costs = new HashMap<>();
        for (ShopItem item : ShopItem.values()) {
            costs.put(item.toString(), INITIAL_PRICE);
        }
    }

    public int costOf(String itemCode) {
        return costs.get(itemCode);
    }

    public void executeBuyItem(BTPlayer player, String itemCode) {
        if (player.isInventoryFull()) {
            return;
        }

        int itemCost = costOf(itemCode);
        if (player.gold() < itemCost) {
            return;
        }

        ShopItem item = ShopItem.valueOf(itemCode);
        player.giveItem(item.create());
        player.takeGold(itemCost);
        costs.put(itemCode, costs.get(itemCode) + 1);
    }
    
}
