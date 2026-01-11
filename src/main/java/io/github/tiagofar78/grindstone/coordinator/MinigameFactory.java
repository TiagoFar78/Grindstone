package io.github.tiagofar78.grindstone.coordinator;

import io.github.tiagofar78.grindstone.game.Minigame;
import io.github.tiagofar78.grindstone.game.MinigameMap;
import io.github.tiagofar78.grindstone.game.MinigameSettings;
import io.github.tiagofar78.grindstone.party.Party;

import java.util.List;

@FunctionalInterface
public interface MinigameFactory {

    Minigame create(MinigameMap map, MinigameSettings settings, List<Party> parties);

}
