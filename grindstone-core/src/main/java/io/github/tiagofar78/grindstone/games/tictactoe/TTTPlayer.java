package io.github.tiagofar78.grindstone.games.tictactoe;

import java.util.UUID;

import io.github.tiagofar78.grindstone.enginebridge.player.Messager;
import io.github.tiagofar78.grindstone.game.Player;

public class TTTPlayer extends Player<Messager> {

    private int index;

    public TTTPlayer(Messager bridge, UUID uuid, int index) {
        super(bridge, uuid);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
    
}
