package io.github.tiagofar78.grindstone.web.playground;

import java.util.List;
import java.util.function.Function;

import io.github.tiagofar78.grindstone.web.WebGame;
import io.github.tiagofar78.grindstone.web.WebPlayer;

public record AvailableGame(
        String displayName,
        Function<List<List<WebPlayer>>, WebGame> factory,
        Function<List<List<WebPlayer>>, String> teamsChecker
) {
    // Empty
}
