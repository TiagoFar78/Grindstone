package io.github.tiagofar78.grindstone.web.network;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.tiagofar78.grindstone.web.playground.Playground;
import io.javalin.websocket.WsContext;

public class LoginService {

    private static final int MAX_NAME_LENGTH = 16;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static final List<String> AVAILABLE_EMOJIS = List.of(
            "😀",
            "😎",
            "🤓",
            "🤠",
            "🥳",
            "🤖",
            "👻",
            "👽",
            "🐱",
            "🐶",
            "🦊",
            "🐼",
            "🐸",
            "🐵",
            "🦄",
            "🐙",
            "🦁",
            "🐯",
            "🐧",
            "🦉",
            "🍕",
            "🍔",
            "🍩",
            "⚽",
            "🎮",
            "🚀",
            "🌵",
            "🔥",
            "⭐",
            "💎"
    );

    private final Playground playground;
    private final Set<String> pendingSessions = new HashSet<>();
    private static final Map<String, String> loggedInSessions = new ConcurrentHashMap<>();

    public LoginService(Playground playground) {
        this.playground = playground;
    }

    public void onConnect(String sessionId, WsContext ctx) {
        pendingSessions.add(sessionId);
        ctx.send(OutboundMessages.buildLoginPage(null));
    }

    public void onMessage(String sessionId, WsContext ctx, String raw) {
        JsonNode node;
        try {
            node = MAPPER.readTree(raw);
        } catch (Exception e) {
            ctx.send(OutboundMessages.buildLoginPage("Invalid message."));
            return;
        }

        if (!node.path("type").asText("").equals("profile")) {
            ctx.send(OutboundMessages.buildLoginPage(null));
            return;
        }

        String name = node.path("name").asText("").trim();
        if (name.isEmpty() || name.length() > MAX_NAME_LENGTH) {
            ctx.send(
                    OutboundMessages.buildLoginPage(
                            "Nickname must have between 1 and " + MAX_NAME_LENGTH + " characters."
                    )
            );
            return;
        }

        String rawEmoji = node.path("emoji").asText("");
        String emoji = AVAILABLE_EMOJIS.contains(rawEmoji)
                ? rawEmoji
                : AVAILABLE_EMOJIS.get(
                        ThreadLocalRandom.current().nextInt(AVAILABLE_EMOJIS.size())
                );

        String error = playground.join(name, emoji, ctx);
        if (error != null) {
            ctx.send(OutboundMessages.buildLoginPage(error));
            return;
        }

        pendingSessions.remove(sessionId);
        loggedInSessions.put(sessionId, name);
    }

    public String cancel(String sessionId) {
        pendingSessions.remove(sessionId);
        return loggedInSessions.remove(sessionId);
    }

    public boolean isLoggedIn(String sessionId) {
        return loggedInSessions.containsKey(sessionId);
    }

    public String getLoggedIn(String sessionId) {
        return loggedInSessions.get(sessionId);
    }

}
