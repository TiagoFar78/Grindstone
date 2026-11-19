package io.github.tiagofar78.grindstone.games.blindtag.phases;

import io.github.tiagofar78.grindstone.game.phases.OngoingPhase;
import io.github.tiagofar78.grindstone.game.phases.Phase;
import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;

public class PlayPhase extends OngoingPhase<BlindTag> {

    public PlayPhase(BlindTag game) {
        super(game);
    }

    @Override
    public Phase<BlindTag> next() {
        return new SubmitGuessPhase(getGame());
    }

    @Override
    public void start() {
        getGame().startTurns();
    }
}
