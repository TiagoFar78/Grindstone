package io.github.tiagofar78.grindstone.games.blindtag.player;

import io.github.tiagofar78.grindstone.games.blindtag.items.Item;
import io.github.tiagofar78.grindstone.games.blindtag.map.BlankCell;
import io.github.tiagofar78.grindstone.games.blindtag.map.BTMap;
import io.github.tiagofar78.grindstone.games.blindtag.map.Cell;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.UUID;

public class BTPlayerTest {

    private BTPlayer player;

    /** Minimal Item stub â€” just records whether use() was called. */
    private static Item stubItem(int cost) {
        return new Item(cost) {
            @Override
            public void use(io.github.tiagofar78.grindstone.games.blindtag.BlindTag game,
                            BTPlayer player) {
                // no-op for test
            }
        };
    }

    @BeforeMethod
    public void setUp() {
        player = new BTPlayer(UUID.randomUUID());
    }

    // >--------------------{ Initial state }--------------------<

    @Test
    public void testInitialGoldIsZero() {
        Assert.assertEquals(player.getGold(), 0);
    }

    @Test
    public void testInitialScoreIsZero() {
        Assert.assertEquals(player.getScore(), 0);
    }

    @Test
    public void testInitialItemsEmpty() {
        Assert.assertTrue(player.getItems().isEmpty());
    }

    @Test
    public void testInitialGuessedMapIsNull() {
        Assert.assertNull(player.getGuessedMap());
    }

    @Test
    public void testInitialCurrentCellIsNull() {
        Assert.assertNull(player.getCurrentCell());
    }

    // >--------------------{ moveTo }--------------------<

    @Test
    public void testMoveTo() {
        Cell cell = new BlankCell(1);
        player.moveTo(cell);
        Assert.assertEquals(player.getCurrentCell(), cell);
    }

    @Test
    public void testMoveToUpdatesOnSubsequentMoves() {
        Cell cell1 = new BlankCell(1);
        Cell cell2 = new BlankCell(2);
        player.moveTo(cell1);
        player.moveTo(cell2);
        Assert.assertEquals(player.getCurrentCell(), cell2);
    }

    @Test
    public void testMoveToNull() {
        Cell cell = new BlankCell(1);
        player.moveTo(cell);
        player.moveTo(null);
        Assert.assertNull(player.getCurrentCell());
    }

    // >--------------------{ Gold }--------------------<

    @Test
    public void testAddGold() {
        player.addGold(5);
        Assert.assertEquals(player.getGold(), 5);
    }

    @Test
    public void testAddGoldAccumulates() {
        player.addGold(5);
        player.addGold(3);
        Assert.assertEquals(player.getGold(), 8);
    }

    @Test
    public void testDeductGold() {
        player.addGold(10);
        player.deductGold(4);
        Assert.assertEquals(player.getGold(), 6);
    }

    @Test
    public void testDeductGoldCanGoNegative() {
        player.deductGold(3);
        Assert.assertEquals(player.getGold(), -3);
    }

    @Test
    public void testDeductGoldLargeAmount() {
        player.addGold(2);
        player.deductGold(10);
        Assert.assertEquals(player.getGold(), -8);
    }

    @Test
    public void testAddZeroGold() {
        player.addGold(0);
        Assert.assertEquals(player.getGold(), 0);
    }

    // >--------------------{ Score }--------------------<

    @Test
    public void testAddScore() {
        player.addScore(10);
        Assert.assertEquals(player.getScore(), 10);
    }

    @Test
    public void testAddScoreAccumulates() {
        player.addScore(10);
        player.addScore(10);
        Assert.assertEquals(player.getScore(), 20);
    }

    // >--------------------{ Items â€” addItem }--------------------<

    @Test
    public void testAddItemSucceeds() {
        boolean result = player.addItem(stubItem(5));
        Assert.assertTrue(result);
        Assert.assertEquals(player.getItems().size(), 1);
    }

    @Test
    public void testAddUpToFourItems() {
        for (int i = 0; i < BTPlayer.MAX_ITEMS; i++) {
            Assert.assertTrue(player.addItem(stubItem(1)));
        }
        Assert.assertEquals(player.getItems().size(), BTPlayer.MAX_ITEMS);
    }

    @Test
    public void testAddFifthItemRejected() {
        for (int i = 0; i < BTPlayer.MAX_ITEMS; i++) {
            player.addItem(stubItem(1));
        }
        boolean result = player.addItem(stubItem(1));
        Assert.assertFalse(result);
        Assert.assertEquals(player.getItems().size(), BTPlayer.MAX_ITEMS);
    }

    @Test
    public void testAddFifthItemDoesNotChangeInventory() {
        Item[] items = new Item[BTPlayer.MAX_ITEMS];
        for (int i = 0; i < BTPlayer.MAX_ITEMS; i++) {
            items[i] = stubItem(i);
            player.addItem(items[i]);
        }
        Item extra = stubItem(99);
        player.addItem(extra);

        Assert.assertFalse(player.getItems().contains(extra));
    }

    // >--------------------{ Items â€” removeItem }--------------------<

    @Test
    public void testRemoveItem() {
        Item item = stubItem(5);
        player.addItem(item);
        player.removeItem(item);
        Assert.assertTrue(player.getItems().isEmpty());
    }

    @Test
    public void testRemoveItemAllowsAddingMore() {
        for (int i = 0; i < BTPlayer.MAX_ITEMS; i++) {
            player.addItem(stubItem(1));
        }
        Item extra = stubItem(2);
        player.removeItem(player.getItems().get(0));
        boolean result = player.addItem(extra);
        Assert.assertTrue(result);
        Assert.assertEquals(player.getItems().size(), BTPlayer.MAX_ITEMS);
    }

    @Test
    public void testRemoveNonExistentItemIsNoop() {
        Item item = stubItem(5);
        // Never added â€” should not throw
        player.removeItem(item);
        Assert.assertTrue(player.getItems().isEmpty());
    }

    // >--------------------{ Items â€” getItems unmodifiable }--------------------<

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testGetItemsIsUnmodifiable() {
        player.getItems().add(stubItem(1));
    }

    // >--------------------{ Guessed map }--------------------<

    @Test
    public void testSetGuessedMap() {
        BTMap guessed = new BTMap();
        player.setGuessedMap(guessed);
        Assert.assertEquals(player.getGuessedMap(), guessed);
    }

    @Test
    public void testSetGuessedMapCanBeSetToNull() {
        BTMap guessed = new BTMap();
        player.setGuessedMap(guessed);
        player.setGuessedMap(null);
        Assert.assertNull(player.getGuessedMap());
    }

    // >--------------------{ UUID }--------------------<

    @Test
    public void testUUIDIsPreserved() {
        UUID uuid = UUID.randomUUID();
        BTPlayer p = new BTPlayer(uuid);
        Assert.assertEquals(p.getUUID(), uuid);
    }
}
