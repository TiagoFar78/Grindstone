package io.github.tiagofar78.grindstone.games.blindtag.phases;

import io.github.tiagofar78.grindstone.game.Game;
import io.github.tiagofar78.grindstone.game.phases.FinishedPhase;
import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

import java.time.Duration;
import java.util.List;

public class RevealPhase extends FinishedPhase {

    private static final Duration REVEAL_DURATION = Duration.ofSeconds(30);

    public RevealPhase(Game game) {
        super(game);
    }

    @Override
    public void start() {
        BlindTag game = (BlindTag) getGame();
        List<BTPlayer> players = game.getBTPlayers();

        game.onRevealPhaseStarted(players);

        game.getDependencies()
                .getSchedulerService()
                .runAfter(REVEAL_DURATION, () -> {
                    game.runGameOver();
                    game.archive();
                });
    }
}
