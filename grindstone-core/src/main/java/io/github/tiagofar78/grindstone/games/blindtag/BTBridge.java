package io.github.tiagofar78.grindstone.games.blindtag;

import java.util.Collection;
import java.util.List;

import io.github.tiagofar78.grindstone.enginebridge.game.GameBridge;
import io.github.tiagofar78.grindstone.games.blindtag.items.Trap;
import io.github.tiagofar78.grindstone.games.blindtag.map.Arrow;
import io.github.tiagofar78.grindstone.games.blindtag.map.Cell;
import io.github.tiagofar78.grindstone.games.blindtag.map.Direction;

public interface BTBridge extends GameBridge {

    void onTurnStart(BlindTag game, BTPlayer player);

    void onRotationEnd(BlindTag game);

    void onTag(BlindTag game, BTPlayer newTagger, BTPlayer currentTagger);

    void onGoldGained(BlindTag game, BTPlayer player, int goldAmount);

    void onBlankCell(BlindTag game, BTPlayer player);

    void onTeleport(BlindTag game, BTPlayer player, Cell destination);

    void openShop(BlindTag game, BTPlayer player);

    void onPathReveal(BlindTag game, BTPlayer player, List<Direction> path);

    void onCompassUse(BlindTag game, BTPlayer player, Cell current, Collection<Arrow> values);

    void onHorseUse(BlindTag game, BTPlayer player);

    void onTrapPlace(BlindTag game, BTPlayer player, Cell currentCell);

    void onTrapTrigger(BlindTag game, BTPlayer player, List<Trap> triggeredTraps);

    void onClonePlace(BlindTag game, BTPlayer player, Cell cell);

    void onSixthSenseUse(BlindTag game, BTPlayer player, Direction hint);

    void onHawkEyeUse(BlindTag game, BTPlayer player, List<Direction> path);

    void onNeedyUse(BlindTag game, BTPlayer player);

    void onRevealPhaseStart(BlindTag game);

    void onSubmitGuessPhaseStart(BlindTag game);

}
