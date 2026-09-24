package io.github.tiagofar78.grindstone.games.blindtag.debug;

import io.github.tiagofar78.grindstone.games.blindtag.map.BTMap;
import io.github.tiagofar78.grindstone.games.blindtag.map.BadCell;
import io.github.tiagofar78.grindstone.games.blindtag.map.Cell;
import io.github.tiagofar78.grindstone.games.blindtag.map.Direction;
import io.github.tiagofar78.grindstone.games.blindtag.map.GoldCell;
import io.github.tiagofar78.grindstone.games.blindtag.map.GoodCell;
import io.github.tiagofar78.grindstone.games.blindtag.map.MapGenerator;
import io.github.tiagofar78.grindstone.games.blindtag.map.ShopCell;
import io.github.tiagofar78.grindstone.games.blindtag.map.TeleportCell;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class MapViewer {

    private static final int MAX_VOID_LETTERS = 26;

    private record VoidMarks(Map<String, Character> exits, Map<String, Character> entries) {
    }

    public static void main(String[] args) {
        long seed = args.length > 0 ? Long.parseLong(args[0]) : new Random().nextLong();
        Random random = new Random(seed);
        BTMap map = MapGenerator.generate(random);

        System.out.println("Seed: " + seed);
        System.out.println();

        printLegend();
        System.out.println();

        printGrid(map);
    }

    // >----------------------{ Legend }----------------------<

    private static void printLegend() {
        System.out.println("Types:  G=Gold  $=Shop  +=Good  X=Bad  _=Blank  @=Teleport  .=Gap  *cell*=Spawn");
        System.out.println("Arrows: ^=N  v=S  <=W  >=E  /=NE or SW  \\=NW or SE");
        System.out.println("A lowercase letter marks where an arrow leaves the grid; the matching uppercase");
        System.out.println("letter marks where it re-enters, on the opposite side of the cell it lands on.");
    }

    // >----------------------{ Grid }----------------------<

    private static final int ROW_LABEL_WIDTH = 3;
    private static final int SIDE_WIDTH = 2;
    private static final int CENTER_WIDTH = 3;
    private static final int BLOCK_WIDTH = SIDE_WIDTH + 1 + CENTER_WIDTH + 1 + SIDE_WIDTH + 1;

    private static void printGrid(BTMap map) {
        Cell[][] grid = new Cell[BTMap.GRID_SIZE][BTMap.GRID_SIZE];
        for (Cell cell : map.getCells()) {
            grid[cell.getRow()][cell.getCol()] = cell;
        }

        VoidMarks marks = buildVoidMarks(map);

        Cell spawn = map.getSpawnCell();

        System.out.println(" ".repeat(ROW_LABEL_WIDTH) + columnHeader());

        for (int row = 0; row < BTMap.GRID_SIZE; row++) {
            StringBuilder top = new StringBuilder();
            StringBuilder mid = new StringBuilder();
            StringBuilder bottom = new StringBuilder();

            for (int col = 0; col < BTMap.GRID_SIZE; col++) {
                Cell cell = grid[row][col];
                appendBlock(top, mid, bottom, cell, cell == spawn, marks);
            }

            System.out.println(" ".repeat(ROW_LABEL_WIDTH) + top.toString().stripTrailing());
            System.out.println(pad(String.valueOf(row), ROW_LABEL_WIDTH) + mid.toString().stripTrailing());
            System.out.println(" ".repeat(ROW_LABEL_WIDTH) + bottom.toString().stripTrailing());

            if (row < BTMap.GRID_SIZE - 1) {
                System.out.println();
            }
        }
    }

    private static VoidMarks buildVoidMarks(BTMap map) {
        Map<String, Character> exits = new LinkedHashMap<>();
        Map<String, Character> entries = new LinkedHashMap<>();
        int nextLetter = 0;

        for (Cell cell : map.getCells()) {
            for (Direction dir : Direction.values()) {
                if (!cell.hasArrow(dir) || !isVoidDirection(cell.getRow(), cell.getCol(), dir)) {
                    continue;
                }

                if (nextLetter >= MAX_VOID_LETTERS) {
                    continue;
                }

                char letter = (char) ('a' + nextLetter);
                nextLetter++;

                exits.put(cell.getRow() + "," + cell.getCol() + "," + dir, letter);

                Cell destination = cell.getArrow(dir).destination();
                Direction entryDirection = opposite(dir);
                String entryKey = destination.getRow() + "," + destination.getCol() + "," + entryDirection;
                entries.putIfAbsent(entryKey, Character.toUpperCase(letter));
            }
        }

        return new VoidMarks(exits, entries);
    }

    private static Direction opposite(Direction direction) {
        return switch (direction) {
            case N -> Direction.S;
            case S -> Direction.N;
            case E -> Direction.W;
            case W -> Direction.E;
            case NE -> Direction.SW;
            case SW -> Direction.NE;
            case NW -> Direction.SE;
            case SE -> Direction.NW;
        };
    }

    private static boolean isVoidDirection(int row, int col, Direction dir) {
        int r = row + dir.rowDelta();
        int c = col + dir.colDelta();
        return r < 0 || r >= BTMap.GRID_SIZE || c < 0 || c >= BTMap.GRID_SIZE;
    }

    private static String columnHeader() {
        StringBuilder header = new StringBuilder();
        for (int col = 0; col < BTMap.GRID_SIZE; col++) {
            header.append(pad(String.valueOf(col), BLOCK_WIDTH));
        }

        return header.toString();
    }

    private static String pad(String text, int width) {
        StringBuilder builder = new StringBuilder(text);
        while (builder.length() < width) {
            builder.append(' ');
        }

        return builder.toString();
    }

    private static void appendBlock(StringBuilder top, StringBuilder mid, StringBuilder bottom, Cell cell, boolean isSpawn,
            VoidMarks marks) {
        if (cell == null) {
            top.append(" ".repeat(BLOCK_WIDTH));
            mid.append(" ".repeat(BLOCK_WIDTH));
            bottom.append(" ".repeat(BLOCK_WIDTH));
            return;
        }

        char open = isSpawn ? '*' : '[';
        char close = isSpawn ? '*' : ']';
        String label = open + String.valueOf(symbol(cell)) + close;

        top.append(slot(cell, Direction.NW, marks)).append(' ').append(centerSlot(cell, Direction.N, marks)).append(' ')
                .append(slot(cell, Direction.NE, marks)).append(' ');
        mid.append(slot(cell, Direction.W, marks)).append(' ').append(label).append(' ').append(slot(cell, Direction.E, marks))
                .append(' ');
        bottom.append(slot(cell, Direction.SW, marks)).append(' ').append(centerSlot(cell, Direction.S, marks)).append(' ')
                .append(slot(cell, Direction.SE, marks)).append(' ');
    }

    private static String slot(Cell cell, Direction direction, VoidMarks marks) {
        return pad(token(cell, direction, marks), SIDE_WIDTH);
    }

    private static String centerSlot(Cell cell, Direction direction, VoidMarks marks) {
        String text = token(cell, direction, marks);
        int leftPad = (CENTER_WIDTH - text.length()) / 2;
        return " ".repeat(leftPad) + pad(text, CENTER_WIDTH - leftPad);
    }

    private static String token(Cell cell, Direction direction, VoidMarks marks) {
        String key = cell.getRow() + "," + cell.getCol() + "," + direction;

        Character entryLetter = marks.entries().get(key);
        if (entryLetter != null) {
            return String.valueOf(entryLetter);
        }

        if (!cell.hasArrow(direction)) {
            return "";
        }

        Character exitLetter = marks.exits().get(key);
        if (exitLetter != null) {
            return String.valueOf(exitLetter);
        }

        return String.valueOf(arrowGlyph(direction));
    }

    private static char arrowGlyph(Direction direction) {
        return switch (direction) {
            case N -> '^';
            case S -> 'v';
            case W -> '<';
            case E -> '>';
            case NE, SW -> '/';
            case NW, SE -> '\\';
        };
    }

    private static char symbol(Cell cell) {
        if (cell instanceof GoldCell) {
            return 'G';
        }

        if (cell instanceof ShopCell) {
            return '$';
        }

        if (cell instanceof GoodCell) {
            return '+';
        }

        if (cell instanceof BadCell) {
            return 'X';
        }

        if (cell instanceof TeleportCell) {
            return '@';
        }

        return '_';
    }
}
