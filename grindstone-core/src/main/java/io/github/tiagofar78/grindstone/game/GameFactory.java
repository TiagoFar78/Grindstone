package io.github.tiagofar78.grindstone.game;

import java.util.Collection;
import java.util.List;

@FunctionalInterface
public interface GameFactory {

    Game create(GameMap map, GameSettings settings, List<Collection<String>> parties, boolean keepTeams);

}
