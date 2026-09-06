package io.github.tiagofar78.grindstone.games.blindtag.phases;

import io.github.tiagofar78.grindstone.game.Game;
import io.github.tiagofar78.grindstone.game.phases.Phase;
import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;

public class SubmitGuessPhase extends Phase {

    public SubmitGuessPhase(Game game) {
        super(game);
    }

    @Override
    public Phase next() {
        return new RevealPhase(getGame());
    }

    @Override public boolean isClockStopped() { return true;  }
    @Override public boolean hasGameStarted()  { return true;  }
    @Override public boolean hasGameEnded()    { return false; }
    @Override public boolean isGameDisabled()  { return false; }

    @Override
    public void start() {
        BlindTag game = (BlindTag) getGame();
        game.onSubmitGuessStarted(game.getBTPlayers());
    }
}
