package io.github.tiagofar78.grindstone.cli.playground;

import java.util.List;
import java.util.function.Function;

import io.github.tiagofar78.grindstone.cli.CLIGame;
import io.github.tiagofar78.grindstone.cli.CLIPlayer;

public record AvailableGame(String displayName, Function<List<List<CLIPlayer>>, CLIGame> factory) {
}
