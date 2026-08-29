package io.github.tiagofar78.grindstone.games.blindtag.player;

import io.github.tiagofar78.grindstone.game.Player;
import io.github.tiagofar78.grindstone.games.blindtag.items.Item;
import io.github.tiagofar78.grindstone.games.blindtag.map.Cell;
import io.github.tiagofar78.grindstone.games.blindtag.map.Direction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class BTPlayer extends Player {

    public static final int MAX_ITEMS = 4;

    private Cell currentCell;
    private int gold = 0;
    private int score = 0;
    private final List<Item> items = new ArrayList<>();

    public BTPlayer(UUID uuid) {
        super(uuid);
    }

    // >--------------------{ Position }--------------------<

    public Cell getCurrentCell() {
        return currentCell;
    }

    public void setCurrentCell(Cell cell) {
        this.currentCell = cell;
    }

    // >---------------------{ Gold }---------------------<

    public int getGold() {
        return gold;
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public void deductGold(int amount) {
        gold -= amount;
    }

    // >--------------------{ Score }--------------------<

    public int getScore() {
        return score;
    }

    public void increaseScore(int amount) {
        score += amount;
    }

    // >--------------------{ Items }--------------------<

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(Item item) {
        if (items.size() >= MAX_ITEMS) {
            return;
        }

        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

}
