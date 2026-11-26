package io.github.tiagofar78.grindstone.web.games.blindtag;

import java.util.Collection;
import java.util.List;

import io.github.tiagofar78.grindstone.game.Game;
import io.github.tiagofar78.grindstone.games.blindtag.BTBridge;
import io.github.tiagofar78.grindstone.games.blindtag.BTPlayer;
import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.games.blindtag.items.Trap;
import io.github.tiagofar78.grindstone.games.blindtag.map.Arrow;
import io.github.tiagofar78.grindstone.games.blindtag.map.Cell;
import io.github.tiagofar78.grindstone.games.blindtag.map.Direction;

public class WebBTBridge implements BTBridge {

    @Override
    public void disable(Game<?, ?, ?> game) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'disable'");
    }

    @Override
    public void onTurnStart(BlindTag game, BTPlayer player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onTurnStart'");
    }

    @Override
    public void onRotationEnd(BlindTag game) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onRotationEnd'");
    }

    @Override
    public void onTag(BlindTag game, BTPlayer newTagger, BTPlayer currentTagger) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onTag'");
    }

    @Override
    public void onGoldGained(BlindTag game, BTPlayer player, int goldAmount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onGoldGained'");
    }

    @Override
    public void onBlankCell(BlindTag game, BTPlayer player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onBlankCell'");
    }

    @Override
    public void onTeleport(BlindTag game, BTPlayer player, Cell destination) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onTeleport'");
    }

    @Override
    public void openShop(BlindTag game, BTPlayer player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'openShop'");
    }

    @Override
    public void onPathReveal(BlindTag game, BTPlayer player, List<Direction> path) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onPathReveal'");
    }

    @Override
    public void onCompassUse(BlindTag game, BTPlayer player, Cell current, Collection<Arrow> values) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onCompassUse'");
    }

    @Override
    public void onHorseUse(BlindTag game, BTPlayer player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onHorseUse'");
    }

    @Override
    public void onTrapPlace(BlindTag game, BTPlayer player, Cell currentCell) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onTrapPlace'");
    }

    @Override
    public void onTrapTrigger(BlindTag game, BTPlayer player, List<Trap> triggeredTraps) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onTrapTrigger'");
    }

    @Override
    public void onClonePlace(BlindTag game, BTPlayer player, Cell cell) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onClonePlace'");
    }

    @Override
    public void onSixthSenseUse(BlindTag game, BTPlayer player, Direction hint) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onSixthSenseUse'");
    }

    @Override
    public void onHawkEyeUse(BlindTag game, BTPlayer player, List<Direction> path) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onHawkEyeUse'");
    }

    @Override
    public void onNeedyUse(BlindTag game, BTPlayer player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onNeedyUse'");
    }

    @Override
    public void onRevealPhaseStart(BlindTag game) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onRevealPhaseStart'");
    }

    @Override
    public void onSubmitGuessPhaseStart(BlindTag game) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onSubmitGuessPhaseStart'");
    }

}
