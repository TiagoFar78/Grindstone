package io.github.tiagofar78.grindstone.enginebridge.game;

import io.github.tiagofar78.grindstone.game.Game;

public interface GameBridge {

    void disable(Game<?, ?, ?> game);

}
