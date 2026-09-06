package io.github.tiagofar78.grindstone.games.blindtag.player;

import io.github.tiagofar78.grindstone.game.Player;
import io.github.tiagofar78.grindstone.games.blindtag.items.Item;
import io.github.tiagofar78.grindstone.games.blindtag.map.BTMap;
import io.github.tiagofar78.grindstone.games.blindtag.map.Cell;

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
    private BTMap guessedMap = null;

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

    public int gold() {
        return gold;
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public void takeGold(int amount) {
        gold -= amount;
    }

    // >--------------------{ Score }--------------------<

    public int getScore() {
        return score;
    }

    public void addScore(int amount) {
        score += amount;
    }

    // >--------------------{ Items }--------------------<

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public boolean isInventoryFull() {
        return items.size() >= MAX_ITEMS;
    }

    public void giveItem(Item item) {
        if (isInventoryFull()) {
            return;
        }
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    // >------------------{ Guessed Map }------------------<

    public BTMap getGuessedMap() {
        return guessedMap;
    }

    public void setGuessedMap(BTMap guessedMap) {
        this.guessedMap = guessedMap;
    }
}
