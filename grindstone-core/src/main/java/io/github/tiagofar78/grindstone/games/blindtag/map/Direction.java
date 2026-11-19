package io.github.tiagofar78.grindstone.games.blindtag.map;

public enum Direction {

    N(-1, 0),
    NE(-1, 1),
    E(0, 1),
    SE(1, 1),
    S(1, 0),
    SW(1, -1),
    W(0, -1),
    NW(-1, -1);

    private int rowDelta;
    private int colDelta;

    private Direction(int rowDelta, int colDelta) {
        this.rowDelta = rowDelta;
        this.colDelta = colDelta;
    }

    public boolean isCardinal() {
        return this == N || this == E || this == S || this == W;
    }

    public int rowDelta() {
        return rowDelta;
    }

    public int colDelta() {
        return colDelta;
    }

    public Direction opposite() {
        Direction[] values = values();
        return values[(ordinal() + values.length / 2) % values.length];
    }
}
