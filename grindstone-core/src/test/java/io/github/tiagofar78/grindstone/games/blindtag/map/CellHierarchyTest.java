package io.github.tiagofar78.grindstone.games.blindtag.map;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Optional;

public class CellHierarchyTest {

    // >--------------------{ Direction helpers }--------------------<

    @Test
    public void testCardinalDirections() {
        Assert.assertTrue(Direction.N.isCardinal());
        Assert.assertTrue(Direction.E.isCardinal());
        Assert.assertTrue(Direction.S.isCardinal());
        Assert.assertTrue(Direction.W.isCardinal());
    }

    @Test
    public void testDiagonalDirectionsAreNotCardinal() {
        Assert.assertFalse(Direction.NE.isCardinal());
        Assert.assertFalse(Direction.SE.isCardinal());
        Assert.assertFalse(Direction.SW.isCardinal());
        Assert.assertFalse(Direction.NW.isCardinal());
    }

    @Test
    public void testDirectionDeltas() {
        Assert.assertEquals(Direction.N.rowDelta(), -1);
        Assert.assertEquals(Direction.N.colDelta(), 0);

        Assert.assertEquals(Direction.E.rowDelta(), 0);
        Assert.assertEquals(Direction.E.colDelta(), 1);

        Assert.assertEquals(Direction.S.rowDelta(), 1);
        Assert.assertEquals(Direction.S.colDelta(), 0);

        Assert.assertEquals(Direction.W.rowDelta(), 0);
        Assert.assertEquals(Direction.W.colDelta(), -1);

        Assert.assertEquals(Direction.NE.rowDelta(), -1);
        Assert.assertEquals(Direction.NE.colDelta(), 1);

        Assert.assertEquals(Direction.SW.rowDelta(), 1);
        Assert.assertEquals(Direction.SW.colDelta(), -1);
    }

    // >--------------------{ Arrow }--------------------<

    @Test
    public void testArrowOf() {
        BlankCell dest = new BlankCell(1);
        Arrow arrow = Arrow.of(Direction.N, dest);

        Assert.assertEquals(arrow.direction(), Direction.N);
        Assert.assertEquals(arrow.destination(), dest);
        Assert.assertFalse(arrow.selfReferencing());
        Assert.assertFalse(arrow.wrapColor().isPresent());
        Assert.assertFalse(arrow.isWrapAround());
    }

    @Test
    public void testArrowSelfReferencing() {
        BlankCell cell = new BlankCell(2);
        Arrow arrow = Arrow.self(Direction.S, cell);

        Assert.assertTrue(arrow.selfReferencing());
        Assert.assertEquals(arrow.destination(), cell);
    }

    @Test
    public void testArrowWrap() {
        BlankCell dest = new BlankCell(3);
        Arrow arrow = Arrow.wrap(Direction.N, dest, "red");

        Assert.assertTrue(arrow.isWrapAround());
        Assert.assertEquals(arrow.wrapColor(), Optional.of("red"));
    }

    // >--------------------{ Cell arrows }--------------------<

    @Test
    public void testAddAndGetArrow() {
        BlankCell cell = new BlankCell(1);
        BlankCell other = new BlankCell(2);
        Arrow arrow = Arrow.of(Direction.E, other);

        cell.addArrow(arrow);

        Assert.assertEquals(cell.getArrow(Direction.E), arrow);
        Assert.assertTrue(cell.hasArrow(Direction.E));
        Assert.assertFalse(cell.hasArrow(Direction.W));
    }

    @Test
    public void testGetArrowsIsUnmodifiable() {
        BlankCell cell = new BlankCell(1);
        Assert.assertThrows(
                UnsupportedOperationException.class,
                () -> cell.getArrows().put(Direction.N, Arrow.of(Direction.N, cell)));
    }

    @Test
    public void testArrowOverwritesSameDirection() {
        BlankCell cell = new BlankCell(1);
        BlankCell dest1 = new BlankCell(2);
        BlankCell dest2 = new BlankCell(3);

        cell.addArrow(Arrow.of(Direction.N, dest1));
        cell.addArrow(Arrow.of(Direction.N, dest2));

        Assert.assertEquals(cell.getArrow(Direction.N).destination(), dest2);
        Assert.assertEquals(cell.getArrows().size(), 1);
    }

    // >--------------------{ Cell types }--------------------<

    @Test
    public void testGoldCellNumber() {
        GoldCell cell = new GoldCell(5);
        Assert.assertEquals(cell.getNumber(), 5);
    }

    @Test
    public void testShopCellNumber() {
        ShopCell cell = new ShopCell(10);
        Assert.assertEquals(cell.getNumber(), 10);
    }

    @Test
    public void testGoodCellNumber() {
        GoodCell cell = new GoodCell(3);
        Assert.assertEquals(cell.getNumber(), 3);
    }

    @Test
    public void testBadCellNumber() {
        BadCell cell = new BadCell(7);
        Assert.assertEquals(cell.getNumber(), 7);
    }

    @Test
    public void testBlankCellNumber() {
        BlankCell cell = new BlankCell(15);
        Assert.assertEquals(cell.getNumber(), 15);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testTeleportCellGetNumberThrows() {
        TeleportCell cell = new TeleportCell();
        cell.getNumber();
    }

    // >--------------------{ Traps and Clones }--------------------<

    @Test
    public void testNoTrapsInitially() {
        BlankCell cell = new BlankCell(1);
        Assert.assertFalse(cell.hasTraps());
        Assert.assertTrue(cell.getTraps().isEmpty());
    }

    @Test
    public void testNoClonesInitially() {
        BlankCell cell = new BlankCell(1);
        Assert.assertFalse(cell.hasClones());
        Assert.assertTrue(cell.getClones().isEmpty());
    }

    @Test
    public void testGetTrapsIsSnapshot() {
        BlankCell cell = new BlankCell(1);
        // List.copyOf returns an unmodifiable list
        Assert.assertThrows(
                UnsupportedOperationException.class,
                () -> cell.getTraps().add(null));
    }

    @Test
    public void testGetClonesIsSnapshot() {
        BlankCell cell = new BlankCell(1);
        Assert.assertThrows(
                UnsupportedOperationException.class,
                () -> cell.getClones().add(null));
    }
}
