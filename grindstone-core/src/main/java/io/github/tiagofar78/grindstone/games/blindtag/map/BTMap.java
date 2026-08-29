package io.github.tiagofar78.grindstone.games.blindtag.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class BTMap {

    public static final int GRID_SIZE = 5;

    private final List<Cell> cells;
    private Cell spawnCell;

    public BTMap(List<Cell> cells, Cell spawnCell) {
        this.cells = cells;
        this.spawnCell = spawnCell;
    }

    // >---------------------{ Grid access }---------------------<

    public Cell getCell(int number) {
        return cells.get(number - 1);
    }

    public Cell getRandomBlankCell(Random random) {
        List<Cell> blankCells = cells.stream().filter(c -> c instanceof BlankCell).toList();
        return blankCells.get(random.nextInt(blankCells.size()));
    }

    public Cell getSpawnCell() {
        return spawnCell;
    }

    // >-----------------{ Arrow mutation }----------------<

    public void addArrow(Random random) { // TODO make this method much simpler now that you know it will only need to add an arrow
        List<Cell> realCells = getRealCells();
        if (realCells.size() < 2) {
            return;
        }

        // Collect cells that have more than one arrow (safe to remove from)
        List<Cell> removableCandidates = new ArrayList<>();
        for (Cell cell : realCells) {
            if (cell.getArrows().size() > 1) {
                removableCandidates.add(cell);
            }
        }

        if (!removableCandidates.isEmpty()) {
            Cell sourceCell = removableCandidates.get(random.nextInt(removableCandidates.size()));
            List<Direction> directions = new ArrayList<>(sourceCell.getArrows().keySet());
            Direction dirToRemove = directions.get(random.nextInt(directions.size()));
            // Remove by replacing with an empty-direction map entry; Cell doesn't expose
            // a remove â€” we rebuild via a new arrow map by overwriting with null, but
            // Cell's map is EnumMap. We need to expose removal.
            sourceCell.removeArrow(dirToRemove);
        }

        // Add one new valid arrow: pick a random real cell as source and a different
        // real cell as destination, using a direction not already occupied.
        List<Cell> shuffled = new ArrayList<>(realCells);
        Collections.shuffle(shuffled, random);

        for (Cell source : shuffled) {
            List<Direction> freeDirections = freeDirections(source);
            if (freeDirections.isEmpty()) {
                continue;
            }
            // Pick a destination that is a different real cell
            List<Cell> destinations = new ArrayList<>(realCells);
            destinations.remove(source);
            if (destinations.isEmpty()) {
                continue;
            }
            Direction dir = freeDirections.get(random.nextInt(freeDirections.size()));
            Cell dest = destinations.get(random.nextInt(destinations.size()));
            source.addArrow(Arrow.of(dir, dest));
            return;
        }
    }

    private List<Direction> freeDirections(Cell cell) {
        List<Direction> free = new ArrayList<>();
        for (Direction d : Direction.values()) {
            if (!cell.hasArrow(d)) {
                free.add(d);
            }
        }
        return free;
    }

    // >-----------------{ BFS shortest path }----------------<

    public List<Cell> findShortestPath(Cell from, Cell to) { // TODO adapt this method to return List<Direction> instead
        if (from == to) {
            return List.of();
        }

        Map<Cell, Cell> predecessor = new HashMap<>();
        Queue<Cell> queue = new LinkedList<>();
        Set<Cell> visited = new HashSet<>();

        queue.add(from);
        visited.add(from);
        predecessor.put(from, null);

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            for (Arrow arrow : current.getArrows().values()) {
                Cell next = arrow.destination();
                if (visited.contains(next)) {
                    continue;
                }
                visited.add(next);
                predecessor.put(next, current);
                if (next == to) {
                    return buildPath(predecessor, from, to);
                }
                queue.add(next);
            }
        }

        throw new IllegalStateException("Could not find a path between the two cells.");
    }

    private List<Cell> buildPath(Map<Cell, Cell> predecessor, Cell from, Cell to) {
        List<Cell> path = new ArrayList<>();
        Cell current = to;
        while (current != null) {
            path.add(current);
            current = predecessor.get(current);
        }
        Collections.reverse(path);
        return Collections.unmodifiableList(path);
    }
}
