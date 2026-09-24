package io.github.tiagofar78.grindstone.games.blindtag.map;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MapGenerator {

    private static final int GOLD_MIN = 1, GOLD_MAX = 2;
    private static final int SHOP_MIN = 2, SHOP_MAX = 3;
    private static final int GOOD_MIN = 1, GOOD_MAX = 3;
    private static final int BAD_MIN = 3, BAD_MAX = 5;
    private static final int GAP_MIN = 2, GAP_MAX = 5;
    private static final int TELEPORT_MIN = 1, TELEPORT_MAX = 1;

    private static final double EXPECTED_PATHS_PER_CELL = 0.4;
    private static final double EXPECTED_VOID_PATHS_PER_CELL = 0.1;

    public static BTMap generate(Random random) {
        List<int[]> randomizePositions = randomizePositions(random);
        int teleportCount = between(random, TELEPORT_MIN, TELEPORT_MAX);
        List<Cell> cells = randomizeCellTypes(random, randomizePositions, teleportCount);
        Cell[][] grid = buildGrid(random, cells);

        double[] pathProbabilities = calculatePathProbabilities(grid);

        boolean[][][] incomingArrows = new boolean[BTMap.GRID_SIZE][BTMap.GRID_SIZE][Direction.values().length];
        for (Cell cell : cells) {
            generateCellArrows(random, grid, cell, pathProbabilities[0], pathProbabilities[1], incomingArrows);
        }

        addMinimumPathsToMakeGridStronglyConnected(random, grid, incomingArrows);

        Cell spawnCell = cells.get(random.nextInt(cells.size() - teleportCount));
        return new BTMap(cells, spawnCell, teleportCount);
    }

    private static int between(Random random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    private static List<int[]> randomizePositions(Random random) {
        List<int[]> positions = new ArrayList<>();
        for (int r = 0; r < BTMap.GRID_SIZE; r++) {
            for (int c = 0; c < BTMap.GRID_SIZE; c++) {
                positions.add(new int[]{r, c});
            }
        }

        Collections.shuffle(positions, random);
        int gaps = between(random, GAP_MIN, GAP_MAX);
        for (int i = 0; i < gaps; i++) {
            positions.removeLast();
        }

        return positions;
    }

    private static List<Cell> randomizeCellTypes(Random random, List<int[]> positions, int teleport) {
        int gold = between(random, GOLD_MIN, GOLD_MAX);
        int shop = between(random, SHOP_MIN, SHOP_MAX);
        int good = between(random, GOOD_MIN, GOOD_MAX);
        int bad = between(random, BAD_MIN, BAD_MAX);
        int blank = positions.size() - gold - shop - good - bad - teleport;

        List<String> types = new ArrayList<>();
        for (int i = 0; i < gold;     i++) types.add("GOLD");
        for (int i = 0; i < shop;     i++) types.add("SHOP");
        for (int i = 0; i < good;     i++) types.add("GOOD");
        for (int i = 0; i < bad;      i++) types.add("BAD");
        for (int i = 0; i < blank;    i++) types.add("BLANK");
        Collections.shuffle(types, random);
        for (int i = 0; i < teleport; i++) types.add("TELEPORT");

        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < positions.size(); i++) {
            int[] pos = positions.get(i);
            cells.add(buildCell(types.get(i), i + 1, pos[0], pos[1]));
        }

        return cells;
    }

    private static Cell buildCell(String type, int number, int r, int c) {
        return switch (type) {
            case "TELEPORT" -> new TeleportCell(r, c);
            case "GOLD"     -> new GoldCell (number, r, c);
            case "SHOP"     -> new ShopCell (number, r, c);
            case "GOOD"     -> new GoodCell (number, r, c);
            case "BAD"      -> new BadCell  (number, r, c);
            default         -> new BlankCell(number, r, c);
        };
    }

    private static Cell[][] buildGrid(Random random, List<Cell> cells) {
        Cell[][] grid = new Cell[BTMap.GRID_SIZE][BTMap.GRID_SIZE];
        for (Cell cell : cells) {
            grid[cell.getRow()][cell.getCol()] = cell;
        }

        return grid;
    }

    private static double[] calculatePathProbabilities(Cell[][] grid) {
        int voidDirections = 0;
        int directDirections = 0;
        for (Cell[] row : grid) {
            for (Cell cell : row) {
                if (cell == null) {
                    continue;
                }

                for (Direction dir : Direction.values()) {
                    if (isDirectPath(grid, cell.getRow(), cell.getCol(), dir)) {
                        directDirections++;
                    }
                    else {
                        voidDirections++;
                    }
                }
            }
        }

        double voidProbability = EXPECTED_VOID_PATHS_PER_CELL;
        double directProbability = 0;
        if (directDirections > 0) {
            double totalDirections = directDirections + voidDirections;
            double expectedDirectPaths = EXPECTED_PATHS_PER_CELL * totalDirections - voidProbability * voidDirections;
            directProbability = Math.max(0, Math.min(1, expectedDirectPaths / directDirections));
        }

        return new double[] { directProbability, voidProbability };
    }

    private static boolean isDirectPath(Cell[][] grid, int r, int c, Direction dir) {
        return findDirectTarget(grid, r, c, dir) != null;
    }

    private static Cell findDirectTarget(Cell[][] grid, int r, int c, Direction dir) {
        int row = r + dir.rowDelta();
        int col = c + dir.colDelta();

        while (row >= 0 && row < grid.length && col >= 0 && col < grid[row].length) {
            if (grid[row][col] != null) {
                return grid[row][col];
            }
            row += dir.rowDelta();
            col += dir.colDelta();
        }

        return null;
    }

    private static void generateCellArrows(Random random, Cell[][] grid, Cell cell,
            double directPathChance, double voidPathChance, boolean[][][] incoming) {
        if (cell == null || cell instanceof TeleportCell) {
            return;
        }

        for (Direction dir : Direction.values()) {
            double generationChance = isDirectPath(grid, cell.getRow(), cell.getCol(), dir)
                    ? directPathChance
                    : voidPathChance;

            if (random.nextDouble() <= generationChance) {
                generateArrow(random, grid, cell, dir, incoming);
            }
        }
    }

    private static void generateArrow(Random random, Cell[][] grid, Cell cell, Direction dir, boolean[][][] incoming) {
        Cell target = findDirectTarget(grid, cell.getRow(), cell.getCol(), dir);
        Direction incomingSide = dir.opposite();

        if (target == null) {
            List<Cell> candidates = findVoidCandidates(grid, cell, incomingSide, incoming);
            if (candidates.isEmpty()) {
                return;
            }
            target = candidates.get(random.nextInt(candidates.size()));
        }

        incoming[target.getRow()][target.getCol()][incomingSide.ordinal()] = true;
        cell.putArrow(new Arrow(dir, target));
    }

    private static List<Cell> findVoidCandidates(Cell[][] grid, Cell source, Direction incomingSide, boolean[][][] incoming) {
        List<Cell> candidates = new ArrayList<>();
        for (Cell[] row : grid) {
            for (Cell c : row) {
                if (c != null && c != source &&
                        !incoming[c.getRow()][c.getCol()][incomingSide.ordinal()] &&
                        !isDirectPath(grid, c.getRow(), c.getCol(), incomingSide)) {
                    candidates.add(c);
                }
            }
        }
        return candidates;
    }

    private static void addMinimumPathsToMakeGridStronglyConnected(Random random, Cell[][] grid, boolean[][][] incoming) {
        List<Cell> cells = new ArrayList<>();
        for (Cell[] row : grid) {
            for (Cell cell : row) {
                if (cell != null) {
                    cells.add(cell);
                }
            }
        }
        if (cells.size() <= 1) {
            return;
        }

        List<List<Cell>> sccs = findStronglyConnectedComponents(cells);
        if (sccs.size() == 1) {
            return;
        }

        Map<Cell, Integer> componentOf = new HashMap<>();
        for (int i = 0; i < sccs.size(); i++) {
            for (Cell cell : sccs.get(i)) {
                componentOf.put(cell, i);
            }
        }

        boolean[] hasOutsideOutgoing = new boolean[sccs.size()];
        boolean[] hasOutsideIncoming = new boolean[sccs.size()];
        for (Cell cell : cells) {
            int from = componentOf.get(cell);
            for (Arrow arrow : cell.getArrows().values()) {
                int to = componentOf.get(arrow.destination());
                if (from != to) {
                    hasOutsideOutgoing[from] = true;
                    hasOutsideIncoming[to] = true;
                }
            }
        }

        List<Integer> sources = new ArrayList<>();
        List<Integer> sinks = new ArrayList<>();
        for (int i = 0; i < sccs.size(); i++) {
            if (!hasOutsideIncoming[i]) sources.add(i);
            if (!hasOutsideOutgoing[i]) sinks.add(i);
        }
        Collections.shuffle(sources, random);
        Collections.shuffle(sinks, random);

        int linksNeeded = Math.max(sources.size(), sinks.size());
        for (int i = 0; i < linksNeeded; i++) {
            List<Cell> from = sccs.get(sinks.get(i % sinks.size()));
            List<Cell> to = sccs.get(sources.get((i + 1) % sources.size()));
            connectComponents(random, from, to, incoming);
        }
    }

    private static void connectComponents(Random random, List<Cell> from, List<Cell> to, boolean[][][] incoming) {
        List<Cell> fromShuffled = new ArrayList<>(from);
        List<Cell> toShuffled = new ArrayList<>(to);
        Collections.shuffle(fromShuffled, random);
        Collections.shuffle(toShuffled, random);

        for (Cell source : fromShuffled) {
            Direction outDir = freeOutgoingDirection(random, source);
            if (outDir == null) {
                continue;
            }
            Direction inDir = outDir.opposite();
            for (Cell target : toShuffled) {
                if (!incoming[target.getRow()][target.getCol()][inDir.ordinal()]) {
                    source.putArrow(new Arrow(outDir, target));
                    incoming[target.getRow()][target.getCol()][inDir.ordinal()] = true;
                    return;
                }
            }
        }
    }

    private static Direction freeOutgoingDirection(Random random, Cell cell) {
        List<Direction> dirs = new ArrayList<>(Arrays.asList(Direction.values()));
        Collections.shuffle(dirs, random);
        for (Direction dir : dirs) {
            if (cell.getArrow(dir) == null) {
                return dir;
            }
        }

        return null;
    }

    private static List<List<Cell>> findStronglyConnectedComponents(List<Cell> cells) {
        Map<Cell, Integer> index = new HashMap<>();
        Map<Cell, Integer> lowlink = new HashMap<>();
        Set<Cell> onStack = new HashSet<>();
        Deque<Cell> stack = new ArrayDeque<>();
        List<List<Cell>> result = new ArrayList<>();
        int[] counter = {0};

        for (Cell cell : cells) {
            if (!index.containsKey(cell)) {
                tarjan(cell, index, lowlink, onStack, stack, counter, result);
            }
        }
        return result;
    }

    private static void tarjan(Cell cell, Map<Cell, Integer> index, Map<Cell, Integer> lowlink,
            Set<Cell> onStack, Deque<Cell> stack, int[] counter, List<List<Cell>> result) {
        index.put(cell, counter[0]);
        lowlink.put(cell, counter[0]);
        counter[0]++;
        stack.push(cell);
        onStack.add(cell);

        for (Arrow arrow : cell.getArrows().values()) {
            Cell next = arrow.destination();
            if (!index.containsKey(next)) {
                tarjan(next, index, lowlink, onStack, stack, counter, result);
                lowlink.put(cell, Math.min(lowlink.get(cell), lowlink.get(next)));
            }
            else if (onStack.contains(next)) {
                lowlink.put(cell, Math.min(lowlink.get(cell), index.get(next)));
            }
        }

        if (lowlink.get(cell).equals(index.get(cell))) {
            List<Cell> component = new ArrayList<>();
            Cell member;
            do {
                member = stack.pop();
                onStack.remove(member);
                component.add(member);
            } while (member != cell);
            result.add(component);
        }
    }
}
