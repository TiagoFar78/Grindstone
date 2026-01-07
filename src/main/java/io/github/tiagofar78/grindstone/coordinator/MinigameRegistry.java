package io.github.tiagofar78.grindstone.coordinator;

import io.github.tiagofar78.grindstone.game.MinigameMap;
import io.github.tiagofar78.grindstone.game.MinigameSettings;

import java.util.HashMap;
import java.util.Map;

public final class MinigameRegistry {

    public final Registry<MinigameFactory> MINIGAMES = new Registry<>();
    public final Registry<MinigameSettings> GAMEMODES = new Registry<>();
    public final Registry<MinigameMap> MAPS = new Registry<>();

    public final class Registry<T> {

        private final Map<String, T> registry = new HashMap<>();

        public T get(String id) {
            return registry.get(id);
        }

        public void register(String id, T element) {
            registry.put(id, element);
        }

        public void unregisterMinigame(String id) {
            registry.remove(id);
        }

    }

}
