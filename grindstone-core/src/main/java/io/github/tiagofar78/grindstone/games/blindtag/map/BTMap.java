package io.github.tiagofar78.grindstone.games.blindtag.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
public class BTMap {

    public static final int GRID_SIZE = 5;

    private final List<Cell> cells;
    private final Cell spawnCell;
    private final int teleportCellsCount;

    public BTMap(List<Cell> cells, Cell spawnCell, int teleportCellsCount) {
        this.cells = cells;
        this.spawnCell = spawnCell;
        this.teleportCellsCount = teleportCellsCount;
    }

    public Cell getCell(int number) {
        return cells.get(number - 1);
    }

    public List<Cell> getCells() {
        return Collections.unmodifiableList(cells);
    }

    public Cell getSpawnCell() {
        return spawnCell;
    }

    public Cell getRandomNonTeleportCell(Random random) {
        return cells.get(random.nextInt(cells.size() - teleportCellsCount));
    }

    public void addArrow(Random random) {
        List<Cell> sources = cells.subList(0, cells.size() - teleportCellsCount);
        Collections.shuffle(sources, random);

        for (Cell source : sources) {
            List<Direction> freeDirections = new ArrayList<>();
            for (Direction d : Direction.values()) {
                if (!source.hasArrow(d)) {
                    freeDirections.add(d);
                }
            }

            if (freeDirections.isEmpty()) {
                continue;
            }

            List<Cell> destinations = new ArrayList<>(cells);
            destinations.remove(source);
            if (destinations.isEmpty()) {
                continue;
            }

            Direction dir = freeDirections.get(random.nextInt(freeDirections.size()));
            Cell dest = destinations.get(random.nextInt(destinations.size()));
            source.putArrow(new Arrow(dir, dest));
            return;
        }
    }

    public List<Direction> findShortestPath(Cell from, Cell to) {
        if (from == to) {
            return List.of();
        }

        Map<Cell, Cell> predecessor = new HashMap<>();
        Map<Cell, Direction> incomingDir = new HashMap<>();
        Queue<Cell> queue = new LinkedList<>();

        queue.add(from);
        predecessor.put(from, null);

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            for (Arrow arrow : current.getArrows().values()) {
                Cell next = arrow.destination();
                if (predecessor.containsKey(next)) {
                    continue;
                }

                predecessor.put(next, current);
                incomingDir.put(next, arrow.direction());
                if (next == to) {
                    return buildPath(predecessor, incomingDir, from, to);
                }

                queue.add(next);
            }
        }

        return List.of();
    }

    private List<Direction> buildPath(Map<Cell, Cell> predecessor, Map<Cell, Direction> incomingDir, Cell from, Cell to) {
        List<Direction> path = new ArrayList<>();
        Cell current = to;
        while (current != from) {
            path.add(incomingDir.get(current));
            current = predecessor.get(current);
        }

        Collections.reverse(path);
        return Collections.unmodifiableList(path);
    }

    public Direction directionToward(Cell from, Cell to) {
        int dr = Integer.signum(to.getRow() - from.getRow());
        int dc = Integer.signum(to.getCol() - from.getCol());
        for (Direction d : Direction.values()) {
            if (d.rowDelta() == dr && d.colDelta() == dc) {
                return d;
            }
        }

        return Direction.N;
    }

    public int similarityScore(Cell[][] guessed) {
        int correct = 0;

        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                Cell real = cellAt(r, c);
                Cell guess = guessed[r][c];

                if (real == null || guess == null) {
                    continue;
                }

                if (real.getClass() == guess.getClass()) {
                    correct++;
                }

                if (!(real instanceof TeleportCell) && real.getNumber() == guess.getNumber()) {
                    correct++;
                }

                for (Direction dir : Direction.values()) {
                    Arrow realArrow = real.getArrow(dir);
                    Arrow guessArrow = guess.getArrow(dir);

                    if (realArrow == null || guessArrow == null) {
                        continue;
                    }

                    Cell realDest = realArrow.destination();
                    Cell guessDest = guessArrow.destination();

                    if (realDest.getRow() == guessDest.getRow()
                            && realDest.getCol() == guessDest.getCol()) {
                        correct++;
                    }
                }
            }
        }

        return correct * 2;
    }

    private Cell cellAt(int row, int col) {
        for (Cell c : cells) {
            if (c.getRow() == row && c.getCol() == col) {
                return c;
            }
        }

        return null;
    }
}
