package io.github.tiagofar78.grindstone.games.blindtag.map;

import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.items.Clone;
import io.github.tiagofar78.grindstone.games.blindtag.items.Trap;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class Cell {

    private int number;
    private final int row;
    private final int col;
    private final Map<Direction, Arrow> arrows = new EnumMap<>(Direction.class);
    private final List<Trap> traps = new ArrayList<>();
    private final List<Clone> clones = new ArrayList<>();

    protected Cell(int number, int row, int col) {
        this.number = number;
        this.row = row;
        this.col = col;
    }

    public int getNumber() {
        return number;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    // >----------------------{ Arrows }----------------------<

    public void putArrow(Arrow arrow) {
        arrows.put(arrow.direction(), arrow);
    }

    public void removeArrow(Direction direction) {
        arrows.remove(direction);
    }

    public Arrow getArrow(Direction direction) {
        return arrows.get(direction);
    }

    public boolean hasArrow(Direction direction) {
        return arrows.containsKey(direction);
    }

    public Map<Direction, Arrow> getArrows() {
        return Collections.unmodifiableMap(arrows);
    }

    // >----------------------{ Traps }----------------------<

    public void addTrap(Trap trap) {
        traps.add(trap);
    }

    public void triggerTraps(BTPlayer player) {
        for (int i = traps.size() - 1; i >= 0; i--) {
            Trap trap = traps.get(i);
            if (trap.getPlacer() == player) {
                continue;
            }

            trap.trigger(player);
            traps.remove(i);
        }
    }

    // >----------------------{ Clones }----------------------<

    public void addClone(Clone clone) {
        clones.add(clone);
    }

    public void removeClonesButFrom(BTPlayer tagger) {
        for (int i = clones.size() - 1; i >= 0; i--) {
            Clone clone = clones.get(i);
            if (clone.getCloned() == tagger) {
                continue;
            }

            clones.remove(i);
        }
    }

    public List<Clone> getClones() {
        return List.copyOf(clones);
    }

    // >------------------{ Cell Effect }------------------<

    public abstract void applyEffect(BlindTag game, BTPlayer player);
}
