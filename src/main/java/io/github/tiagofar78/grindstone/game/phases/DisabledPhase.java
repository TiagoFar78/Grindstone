package io.github.tiagofar78.grindstone.game.phases;

import io.github.tiagofar78.grindstone.game.Minigame;
import io.github.tiagofar78.grindstone.game.MinigamePlayer;

public class DisabledPhase extends Phase {

    public DisabledPhase(Minigame game) {
        super(game);
    }

    @Override
    public Phase next() {
        return null;
    }

    @Override
    public boolean isClockStopped() {
        return true;
    }

    @Override
    public boolean hasGameStarted() {
        return true;
    }

    @Override
    public boolean hasGameEnded() {
        return true;
    }

    @Override
    public boolean isGameDisabled() {
        return true;
    }

    @Override
    public void start() {
        Minigame game = getGame();
        for (MinigamePlayer player : game.getPlayersOnLobby()) {
            game.removePlayerFromGame(player);
        }

        game.disable();

        // TODO unregister game GameManager.removeGame(game.getId());
    }

}
