package io.github.tiagofar78.grindstone.games.blindtag.map;

public enum Direction {

    N,
    NE,
    E,
    SE,
    S,
    SW,
    W,
    NW;

    public boolean isCardinal() {
        return this == N || this == E || this == S || this == W;
    }

    public int rowDelta() {
        return switch (this) {
            case N, NE, NW -> -1;
            case S, SE, SW -> 1;
            default -> 0;
        };
    }

    public int colDelta() {
        return switch (this) {
            case E, NE, SE -> 1;
            case W, NW, SW -> -1;
            default -> 0;
        };
    }
}
