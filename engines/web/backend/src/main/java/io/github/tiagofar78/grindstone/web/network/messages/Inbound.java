package io.github.tiagofar78.grindstone.web.network.messages;

import java.util.List;

public non-sealed interface Inbound extends WebMessage {

    record EditProfile(String emoji) implements Inbound {}

    record Start(int gameIndex, List<List<Integer>> teams) implements Inbound {}

    record Leave() implements Inbound {}

    record Rejoin() implements Inbound {}

    record GameCommand(GameCommandArgs commandArgs) implements Inbound {}

    interface GameCommandArgs extends Inbound {};

}
