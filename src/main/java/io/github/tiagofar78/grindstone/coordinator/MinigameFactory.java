package io.github.tiagofar78.grindstone.coordinator;

import io.github.tiagofar78.grindstone.game.Minigame;
import io.github.tiagofar78.grindstone.game.MinigameMap;
import io.github.tiagofar78.grindstone.game.MinigamePlayer;
import io.github.tiagofar78.grindstone.game.MinigameSettings;

import java.util.List;

@FunctionalInterface
public interface MinigameFactory {

    Minigame create(MinigameMap map, MinigameSettings settings, List<List<? extends MinigamePlayer>> parties);

}
