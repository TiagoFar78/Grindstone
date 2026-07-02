package io.github.tiagofar78.grindstone.games.turnbasedduel;

import io.github.tiagofar78.grindstone.game.Game;
import io.github.tiagofar78.grindstone.game.MessagesChannel;
import io.github.tiagofar78.grindstone.game.phases.FinishedPhase;
import io.github.tiagofar78.grindstone.game.phases.Phase;

public class TurnBasedDuelPhase extends Phase {

    public TurnBasedDuelPhase(Game game) {
        super(game);
    }

    @Override
    public Phase next() {
        return new FinishedPhase(getGame());
    }

    @Override
    public boolean isClockStopped() {
        return false;
    }

    @Override
    public boolean hasGameStarted() {
        return true;
    }

    @Override
    public boolean hasGameEnded() {
        return false;
    }

    @Override
    public boolean isGameDisabled() {
        return false;
    }

    @Override
    public void start() {
        TurnBasedDuel g = (TurnBasedDuel) getGame();
        g.getPlayer(0).sendMessage(Messages.YOUR_TURN, MessagesChannel.TOP_BAR);
        g.getPlayer(1).sendMessage(Messages.OTHER_TURN, MessagesChannel.TOP_BAR, g.getPlayer(0));
    }

}
