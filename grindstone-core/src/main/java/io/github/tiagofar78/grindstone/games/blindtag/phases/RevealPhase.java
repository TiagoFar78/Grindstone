package io.github.tiagofar78.grindstone.games.blindtag.phases;

import io.github.tiagofar78.grindstone.game.Game;
import io.github.tiagofar78.grindstone.game.phases.DisabledPhase;
import io.github.tiagofar78.grindstone.game.phases.FinishedPhase;
import io.github.tiagofar78.grindstone.game.phases.Phase;
import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

import java.time.Duration;
import java.util.List;

/**
 * The final phase: shows each player's guessed map side-by-side with the real map,
 * announces the winner, then ends the game after a timer.
 *
 * <p>Extends {@link FinishedPhase} so all FinishedPhase flags are inherited, and
 * {@link FinishedPhase#start()} logic (runGameOver + archive) is re-used.
 *
 * <p>Full implementation in Task 10.
 */
public class RevealPhase extends FinishedPhase {

    private static final Duration REVEAL_DURATION = Duration.ofSeconds(30);

    public RevealPhase(Game game) {
        super(game);
    }

    @Override
    public Phase next() {
        return new DisabledPhase(getGame());
    }

    @Override
    public void start() {
        BlindTag game = (BlindTag) getGame();
        List<BTPlayer> players = game.getBTPlayers();

        // Announce results and show guessed maps
        game.onRevealPhaseStarted(players);

        // Run standard game-over logic (victory/defeat messages, archive)
        // after the reveal timer expires
        game.getDependencies()
                .getSchedulerService()
                .runAfter(REVEAL_DURATION, () -> {
                    game.runGameOver();
                    game.archive();
                });
    }
}
