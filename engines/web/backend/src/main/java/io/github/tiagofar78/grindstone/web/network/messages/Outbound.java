package io.github.tiagofar78.grindstone.web.network.messages;

import java.util.List;

public sealed interface Outbound extends WebMessage {

    record Lobby(
            List<String> games,
            List<RunningGame> running,
            List<OnlinePlayer> online,
            boolean canRejoin
    ) implements Outbound {

        public record RunningGame(int id, String game, List<List<String>> teams) {}

        // gameSessionId is null if in lobby
        public record OnlinePlayer(String name, String emoji, boolean you, Integer gameSessionId) {}

    }

    record GameStarted(int gameId) implements Outbound {}

    record GameUpdate(GameUpdateData update) implements Outbound {}

    non-sealed interface GameUpdateData extends Outbound {}

    record Chat(String text) implements Outbound {}

    record Error(String text) implements Outbound {}

}
