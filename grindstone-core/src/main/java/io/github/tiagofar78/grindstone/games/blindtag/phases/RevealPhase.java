package io.github.tiagofar78.grindstone.games.blindtag.phases;

import io.github.tiagofar78.grindstone.game.phases.FinishedPhase;
import io.github.tiagofar78.grindstone.games.blindtag.BTPlayer;
import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;

import java.time.Duration;
import java.util.List;

public class RevealPhase extends FinishedPhase<BlindTag> {

    private static final int REVEAL_DURATION_SECONDS = 30;

    @Override
    public Duration duration() {
        return Duration.ofSeconds(REVEAL_DURATION_SECONDS);
    }

    public RevealPhase(BlindTag game) {
        super(game);
    }

    @Override
    public void start() {
        BlindTag game = getGame();
        List<BTPlayer> players = game.getBTPlayers();

        game.onRevealPhaseStarted(players);

        super.start();
    }
}
