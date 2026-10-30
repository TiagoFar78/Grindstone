package io.github.tiagofar78.grindstone.web.playground;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.tiagofar78.grindstone.web.WebPlayer;

public class PlaygroundManager {
    
    private static final int MAX_PLAYER_NAME_LENGTH = 16;

    private record ConnectionInfo(Playground playground, WebPlayer player) {}

    private Map<String, ConnectionInfo> connections = new HashMap<>();

    private Playground playground = new Playground(); // This must be changed when more than one playground is required

    public String joinPlayground(String playgroundId, String connectionId, WebPlayer player) {
        String name = player.getName();
        if (name.isEmpty() || name.length() > MAX_PLAYER_NAME_LENGTH) {
            return "Nickname must have between 1 and " + MAX_PLAYER_NAME_LENGTH + " characters.";
        }

        String emoji = player.getEmoji();
        if (!PlayerEmoji.emojiExists(emoji)) {
            player.setEmoji(PlayerEmoji.random().code());
        }

        if (playground.isNameTaken(name)) {
            return "There is already a player with that nickname in the playground.";
        }

        WebPlayer resultingPlayer = playground.join(player);
        connections.put(connectionId, new ConnectionInfo(playground, resultingPlayer));
        return null;
    }

    public void receivePlayerMessage(String connectiondId, JsonNode message) {
        ConnectionInfo infos = connections.get(connectiondId);
        infos.playground().process(infos.player(), message);
    }

    public void deleteConnection(String connectionId) {
        ConnectionInfo infos = connections.remove(connectionId);
        if (infos != null) {
            infos.playground().disconnect(infos.player());
        }
    }
    
}
