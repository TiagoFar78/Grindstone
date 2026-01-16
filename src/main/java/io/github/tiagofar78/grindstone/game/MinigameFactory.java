package io.github.tiagofar78.grindstone.game;

import java.util.Collection;
import java.util.List;

@FunctionalInterface
public interface MinigameFactory {

    Minigame create(MinigameMap map, MinigameSettings settings, List<Collection<String>> parties, boolean keepTeams);

}
