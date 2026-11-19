package io.github.tiagofar78.grindstone.games.blindtag.phases;

import io.github.tiagofar78.grindstone.game.phases.OngoingPhase;
import io.github.tiagofar78.grindstone.game.phases.Phase;
import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;

public class SubmitGuessPhase extends OngoingPhase<BlindTag> {

    public SubmitGuessPhase(BlindTag game) {
        super(game);
    }

    @Override
    public Phase<BlindTag> next() {
        return new RevealPhase(getGame());
    }

    @Override
    public void start() {
        getGame().onSubmitGuessStarted(getGame().getBTPlayers());
    }
}
