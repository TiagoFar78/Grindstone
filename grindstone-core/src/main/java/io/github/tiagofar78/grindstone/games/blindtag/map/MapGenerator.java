package io.github.tiagofar78.grindstone.games.blindtag.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MapGenerator {

    // Cell-type count bounds [min, max] (inclusive)
    private static final int GOLD_MIN = 1, GOLD_MAX = 2;
    private static final int SHOP_MIN = 2, SHOP_MAX = 3;
    private static final int GOOD_MIN = 1, GOOD_MAX = 3;
    private static final int BAD_MIN = 3, BAD_MAX = 5;
    private static final int TELEPORT_COUNT = 1;

    // Gap bounds: how many of the 25 positions may be gaps
    private static final int GAP_MIN = 0;
    private static final int GAP_MAX = 5;

    public static BTMap generate(Random random) { // TODO Make as simple as possible while keeping in mind the constraints of every cell having at least one arrow out and every cell is reachable by any other cell (except for TeleportCells). I suggest you represent a map as a Cell[5][5] and then, in the end you generate a BTMap, where the list is a random order for the cells.
        BTMap map = new BTMap();

        // 1. Decide number of gaps and which positions are real
        int gapCount = GAP_MIN + random.nextInt(GAP_MAX - GAP_MIN + 1);
        List<int[]> allPositions = allPositions();
        Collections.shuffle(allPositions, random);
        List<int[]> realPositions = allPositions.subList(gapCount, allPositions.size());

        int realCount = realPositions.size(); // 25 - gapCount

        // 2. Decide cell-type counts
        int goldCount = GOLD_MIN + random.nextInt(GOLD_MAX - GOLD_MIN + 1);
        int shopCount = SHOP_MIN + random.nextInt(SHOP_MAX - SHOP_MIN + 1);
        int goodCount = GOOD_MIN + random.nextInt(GOOD_MAX - GOOD_MIN + 1);
        int badCount = BAD_MIN + random.nextInt(BAD_MAX - BAD_MIN + 1);
        int teleportCount = TELEPORT_COUNT;

        int specialCount = goldCount + shopCount + goodCount + badCount + teleportCount;

        // If we don't have enough real cells for all special types, trim starting from
        // the least important (bad, good, shop) until we fit.
        while (specialCount > realCount && badCount > BAD_MIN) { badCount--; specialCount--; }
        while (specialCount > realCount && goodCount > GOOD_MIN) { goodCount--; specialCount--; }
        while (specialCount > realCount && shopCount > SHOP_MIN) { shopCount--; specialCount--; }
        while (specialCount > realCount && goldCount > GOLD_MIN) { goldCount--; specialCount--; }
        // If still over (extremely sparse map), drop teleport last
        if (specialCount > realCount) { teleportCount = 0; specialCount--; }

        int blankCount = realCount - specialCount;

        // 3. Build a shuffled list of cell types
        List<String> types = new ArrayList<>();
        for (int i = 0; i < goldCount; i++) types.add("GOLD");
        for (int i = 0; i < shopCount; i++) types.add("SHOP");
        for (int i = 0; i < goodCount; i++) types.add("GOOD");
        for (int i = 0; i < badCount; i++) types.add("BAD");
        for (int i = 0; i < teleportCount; i++) types.add("TELEPORT");
        for (int i = 0; i < blankCount; i++) types.add("BLANK");
        Collections.shuffle(types, random);

        // 4. Assign shuffled numbers 1..realCount to non-Teleport cells
        //    (TeleportCell gets no number)
        List<Integer> numbers = new ArrayList<>();
        for (int n = 1; n <= realCount; n++) numbers.add(n);
        Collections.shuffle(numbers, random);

        // 5. Place cells on the grid
        List<Cell> placedCells = new ArrayList<>();
        int numIdx = 0;
        for (int i = 0; i < realCount; i++) {
            int[] pos = realPositions.get(i);
            String type = types.get(i);
            Cell cell;
            if (type.equals("TELEPORT")) {
                cell = new TeleportCell();
            } else {
                int number = numbers.get(numIdx++);
                cell = switch (type) {
                    case "GOLD"  -> new GoldCell(number);
                    case "SHOP"  -> new ShopCell(number);
                    case "GOOD"  -> new GoodCell(number);
                    case "BAD"   -> new BadCell(number);
                    default      -> new BlankCell(number);
                };
            }
            map.setCell(pos[0], pos[1], cell);
            placedCells.add(cell);
        }

        // 6. Generate arrows â€” every real cell gets at least one outgoing arrow
        generateArrows(map, realPositions, placedCells);

        // 7. Choose spawn cell: random non-Teleport real cell
        List<Cell> spawnCandidates = new ArrayList<>();
        for (Cell c : placedCells) {
            if (!(c instanceof TeleportCell)) {
                spawnCandidates.add(c);
            }
        }
        map.setSpawnCell(spawnCandidates.get(random.nextInt(spawnCandidates.size())));

        return map;
    }

    // >-------------------{ Arrow generation }-------------------<

    /**
     * Generates arrows for the map. Strategy:
     * <ol>
     *   <li>For each real cell, pick at least one outgoing direction whose neighbour
     *       (found by scanning in that direction, wrapping and skipping gaps) is another
     *       real cell. A random subset of additional directions are also added.</li>
     *   <li>Wrap-around arrows are only placed on cardinal directions (N/E/S/W) and get
     *       a generated color string.</li>
     * </ol>
     */
    private void generateArrows(BTMap map, List<int[]> realPositions, List<Cell> placedCells) {
        // Build a fast position-to-cell lookup
        Cell[][] grid = buildGrid(map);

        for (int i = 0; i < realPositions.size(); i++) {
            int[] pos = realPositions.get(i);
            int row = pos[0], col = pos[1];
            Cell source = placedCells.get(i);

            // Try all 8 directions; collect those that lead to a real cell
            List<Direction> validDirs = new ArrayList<>();
            for (Direction dir : Direction.values()) {
                Cell dest = findDestination(grid, row, col, dir);
                if (dest != null && dest != source) {
                    validDirs.add(dir);
                }
            }

            if (validDirs.isEmpty()) {
                // Isolated cell â€” self-referencing arrow so it's not stuck
                Direction selfDir = Direction.N;
                source.addArrow(Arrow.self(selfDir, source));
                continue;
            }

            // Guarantee at least one arrow
            Collections.shuffle(validDirs, random);
            addArrowForDirection(source, grid, row, col, validDirs.get(0));

            // Randomly add more arrows (50% chance each additional direction)
            for (int d = 1; d < validDirs.size(); d++) {
                if (random.nextBoolean()) {
                    addArrowForDirection(source, grid, row, col, validDirs.get(d));
                }
            }
        }
    }

    /**
     * Creates and adds one arrow from {@code source} in {@code dir} to the
     * appropriate destination, handling wrap-around.
     */
    private void addArrowForDirection(Cell source, Cell[][] grid, int row, int col, Direction dir) {
        Cell dest = findDestination(grid, row, col, dir);
        if (dest == null) {
            return;
        }

        // Determine if this is a wrap-around arrow:
        // It wraps if the straight-line neighbour is out of bounds or a gap,
        // and the destination was found by wrapping.
        boolean isWrap = isWrappingArrow(grid, row, col, dir);

        if (isWrap && dir.isCardinal()) {
            String color = colorForDirection(dir);
            source.addArrow(Arrow.wrap(dir, dest, color));
        } else {
            source.addArrow(Arrow.of(dir, dest));
        }
    }

    /**
     * Returns the destination cell in {@code dir} from {@code (row, col)},
     * scanning past gaps and wrapping around the grid boundary.
     * Returns {@code null} if no real cell is reachable.
     */
    private Cell findDestination(Cell[][] grid, int row, int col, Direction dir) {
        int dRow = dir.rowDelta();
        int dCol = dir.colDelta();
        int steps = 0;
        int maxSteps = BTMap.GRID_SIZE * 2; // generous cap for wrap-around scans

        int r = row + dRow;
        int c = col + dCol;

        while (steps < maxSteps) {
            // Wrap coordinates
            r = Math.floorMod(r, BTMap.GRID_SIZE);
            c = Math.floorMod(c, BTMap.GRID_SIZE);

            // Back at origin â€” only real cell in this direction would be a self-loop
            if (r == row && c == col) {
                return null;
            }

            if (grid[r][c] != null) {
                return grid[r][c];
            }

            r += dRow;
            c += dCol;
            steps++;
        }

        return null;
    }

    /**
     * Returns true if the arrow from {@code (row, col)} in {@code dir} wraps around
     * the grid edge or skips at least one gap.
     */
    private boolean isWrappingArrow(Cell[][] grid, int row, int col, Direction dir) {
        int newRow = row + dir.rowDelta();
        int newCol = col + dir.colDelta();
        // Out of bounds â†’ definitely wraps
        if (isOutOfBounds(newRow, newCol)) {
            return true;
        }
        // In bounds but a gap â†’ wraps (skips gap)
        if (grid[newRow][newCol] == null) {
            return true;
        }
        return false;
    }

    // >-------------------{ Helpers }-------------------<

    private Cell[][] buildGrid(BTMap map) {
        Cell[][] g = new Cell[BTMap.GRID_SIZE][BTMap.GRID_SIZE];
        for (int r = 0; r < BTMap.GRID_SIZE; r++) {
            for (int c = 0; c < BTMap.GRID_SIZE; c++) {
                g[r][c] = map.getCell(r, c);
            }
        }
        return g;
    }

    private List<int[]> allPositions() {
        List<int[]> positions = new ArrayList<>();
        for (int r = 0; r < BTMap.GRID_SIZE; r++) {
            for (int c = 0; c < BTMap.GRID_SIZE; c++) {
                positions.add(new int[]{r, c});
            }
        }
        return positions;
    }

    private boolean isOutOfBounds(int row, int col) {
        return row < 0 || row >= BTMap.GRID_SIZE || col < 0 || col >= BTMap.GRID_SIZE;
    }

    /** A stable color name based on the cardinal direction. */
    private String colorForDirection(Direction dir) {
        return switch (dir) {
            case N -> "blue";
            case E -> "red";
            case S -> "green";
            case W -> "yellow";
            default -> "white";
        };
    }
}
