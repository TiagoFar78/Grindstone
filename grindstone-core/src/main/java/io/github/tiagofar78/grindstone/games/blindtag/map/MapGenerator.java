package io.github.tiagofar78.grindstone.games.blindtag.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MapGenerator {

    private static final int GOLD_MIN = 1, GOLD_MAX = 2;
    private static final int SHOP_MIN = 2, SHOP_MAX = 3;
    private static final int GOOD_MIN = 1, GOOD_MAX = 3;
    private static final int BAD_MIN = 3, BAD_MAX = 5;
    private static final int GAP_MIN = 0, GAP_MAX = 5;

    public static BTMap generate(Random random) {
        Cell[][] grid = new Cell[BTMap.GRID_SIZE][BTMap.GRID_SIZE];

        List<int[]> allPositions = allPositions();
        Collections.shuffle(allPositions, random);
        int gapCount = GAP_MIN + random.nextInt(GAP_MAX - GAP_MIN + 1);
        List<int[]> realPositions = new ArrayList<>(allPositions.subList(gapCount, allPositions.size()));

        int realCount = realPositions.size();

        int gold     = between(random, GOLD_MIN, GOLD_MAX);
        int shop     = between(random, SHOP_MIN, SHOP_MAX);
        int good     = between(random, GOOD_MIN, GOOD_MAX);
        int bad      = between(random, BAD_MIN, BAD_MAX);
        int teleport = 1;

        int special = gold + shop + good + bad + teleport;
        while (special > realCount && bad > BAD_MIN)   { bad--;      special--; }
        while (special > realCount && good > GOOD_MIN) { good--;     special--; }
        while (special > realCount && shop > SHOP_MIN) { shop--;     special--; }
        while (special > realCount && gold > GOLD_MIN) { gold--;     special--; }
        if    (special > realCount)                    { teleport = 0; special--; }

        int blank = realCount - special;

        List<String> types = new ArrayList<>();
        for (int i = 0; i < gold;     i++) types.add("GOLD");
        for (int i = 0; i < shop;     i++) types.add("SHOP");
        for (int i = 0; i < good;     i++) types.add("GOOD");
        for (int i = 0; i < bad;      i++) types.add("BAD");
        for (int i = 0; i < blank;    i++) types.add("BLANK");
        Collections.shuffle(types, random);
        for (int i = 0; i < teleport; i++) types.add("TELEPORT");

        for (int i = 0; i < realCount; i++) {
            int[] pos = realPositions.get(i);
            String type = types.get(i);
            Cell cell = switch (type) {
                case "TELEPORT" -> new TeleportCell(pos[0], pos[1]);
                case "GOLD"     -> new GoldCell (0, pos[0], pos[1]);
                case "SHOP"     -> new ShopCell (0, pos[0], pos[1]);
                case "GOOD"     -> new GoodCell (0, pos[0], pos[1]);
                case "BAD"      -> new BadCell  (0, pos[0], pos[1]);
                default         -> new BlankCell(0, pos[0], pos[1]);
            };
            grid[pos[0]][pos[1]] = cell;
        }

        generateArrows(random, grid, realPositions, teleport > 0);

        int nonTeleportCount = realCount - teleport;
        Cell[] indexed = new Cell[realCount];
        Cell teleportCellRef = null;

        List<Cell> nonTeleportCells = new ArrayList<>();
        for (int[] pos : realPositions) {
            Cell c = grid[pos[0]][pos[1]];
            if (c instanceof TeleportCell) {
                teleportCellRef = c;
            } else {
                nonTeleportCells.add(c);
            }
        }

        Collections.shuffle(nonTeleportCells, random);
        for (int i = 0; i < nonTeleportCells.size(); i++) {
            nonTeleportCells.get(i).setNumber(i + 1);
            indexed[i] = nonTeleportCells.get(i);
        }

        if (teleportCellRef != null) {
            indexed[nonTeleportCount] = teleportCellRef;
        }

        List<Cell> cells = new ArrayList<>(Arrays.asList(indexed));

        Cell spawnCell = nonTeleportCells.get(random.nextInt(nonTeleportCells.size()));

        return new BTMap(cells, spawnCell, teleport);
    }

    private static void generateArrows(Random random, Cell[][] grid, List<int[]> realPositions, boolean hasTeleport) {
        List<int[]> nonTpPositions = new ArrayList<>();
        int[] tpPosition = null;
        for (int[] pos : realPositions) {
            if (grid[pos[0]][pos[1]] instanceof TeleportCell) {
                tpPosition = pos;
            } else {
                nonTpPositions.add(pos);
            }
        }

        if (nonTpPositions.isEmpty()) {
            return;
        }

        List<int[]> inTree = new ArrayList<>();
        List<int[]> notInTree = new ArrayList<>(nonTpPositions);
        Collections.shuffle(notInTree, random);
        inTree.add(notInTree.remove(0));

        while (!notInTree.isEmpty()) {
            int[] from = inTree.get(random.nextInt(inTree.size()));
            int   idx  = random.nextInt(notInTree.size());
            int[] to   = notInTree.remove(idx);

            Cell src  = grid[from[0]][from[1]];
            Cell dest = grid[to[0]][to[1]];

            Direction dir = pickFreeDirection(random, src);
            if (dir != null) {
                src.putArrow(new Arrow(dir, dest));
            }

            Direction backDir = pickFreeDirection(random, dest);
            if (backDir != null) {
                dest.putArrow(new Arrow(backDir, src));
            }

            inTree.add(to);
        }

        for (int[] pos : nonTpPositions) {
            Cell source = grid[pos[0]][pos[1]];
            for (Direction dir : Direction.values()) {
                if (source.hasArrow(dir)) {
                    continue;
                }

                if (!random.nextBoolean()) {
                    continue;
                }

                Cell dest = findNearestInDirection(grid, pos[0], pos[1], dir);
                if (dest != null) {
                    source.putArrow(new Arrow(dir, dest));
                }
            }
        }

        if (hasTeleport && tpPosition != null) {
            Cell tp = grid[tpPosition[0]][tpPosition[1]];
            for (int[] pos : nonTpPositions) {
                Cell source = grid[pos[0]][pos[1]];
                Direction dir = pickFreeDirection(random, source);
                if (dir != null) {
                    source.putArrow(new Arrow(dir, tp));
                }
            }
        }
    }

    private static Direction pickFreeDirection(Random random, Cell cell) {
        List<Direction> free = new ArrayList<>();
        for (Direction d : Direction.values()) {
            if (!cell.hasArrow(d)) {
                free.add(d);
            }
        }

        if (free.isEmpty()) {
            return null;
        }

        return free.get(random.nextInt(free.size()));
    }

    private static Cell findNearestInDirection(Cell[][] grid, int row, int col, Direction dir) {
        int r = Math.floorMod(row + dir.rowDelta(), BTMap.GRID_SIZE);
        int c = Math.floorMod(col + dir.colDelta(), BTMap.GRID_SIZE);
        int steps = 0;
        while (steps < BTMap.GRID_SIZE * 2) {
            if (r == row && c == col) {
                return null;
            }

            if (grid[r][c] != null) {
                return grid[r][c];
            }

            r = Math.floorMod(r + dir.rowDelta(), BTMap.GRID_SIZE);
            c = Math.floorMod(c + dir.colDelta(), BTMap.GRID_SIZE);
            steps++;
        }

        return null;
    }

    private static int between(Random random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    private static List<int[]> allPositions() {
        List<int[]> positions = new ArrayList<>();
        for (int r = 0; r < BTMap.GRID_SIZE; r++) {
            for (int c = 0; c < BTMap.GRID_SIZE; c++) {
                positions.add(new int[]{r, c});
            }
        }
        return positions;
    }
}
