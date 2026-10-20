package io.github.tiagofar78.grindstone.web.network;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.tiagofar78.grindstone.game.Rejoinable;
import io.github.tiagofar78.grindstone.web.PlayerState;
import io.github.tiagofar78.grindstone.web.WebGame;
import io.github.tiagofar78.grindstone.web.WebPlayer;
import io.github.tiagofar78.grindstone.web.playground.AvailableGame;
import io.github.tiagofar78.grindstone.web.playground.GameSession;

public class OutboundMessages {

    public static String buildLoginPage(String error) {
        Map<String, Object> msg = new LinkedHashMap<>();
        msg.put("type", "nickname");
        msg.put("emojis", LoginService.AVAILABLE_EMOJIS);
        if (error != null) {
            msg.put("error", error);
        }

        return WebServer.toJson(msg);
    }

    public static String buildLobbyPage(
            WebPlayer viewer,
            List<WebPlayer> playerRegistry,
            List<AvailableGame> availableGames,
            List<GameSession> gamesRunning
    ) {
        Map<String, Object> msg = new LinkedHashMap<>();
        msg.put("type", "lobby");
        msg.put("games", availableGames.stream().map(AvailableGame::displayName).toList());
        msg.put("running", buildRunning(gamesRunning));
        msg.put("online", buildOnline(playerRegistry, viewer));
        msg.put("canRejoin", canRejoin(viewer));
        return WebServer.toJson(msg);
    }

    public static String buildErrorToast(String text) {
        return WebServer.toJson(Map.of("type", "error", "text", text));
    }

    public static String buildGameStartedToast(GameSession session) {
        return WebServer.toJson(
                Map.of(
                        "type",
                        "game",
                        "gameId",
                        session.getId(),
                        "game",
                        session.getGame().getName()
                )
        );
    }

    // ################################################# Helpers ##################################################

    private static List<Map<String, Object>> buildRunning(List<GameSession> sessions) {
        List<Map<String, Object>> running = new ArrayList<>();
        for (GameSession session : sessions) {
            List<List<String>> teamNames = session.getTeams().stream().map(
                    team -> team.stream().map(WebPlayer::getName).toList()
            ).toList();

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("id", session.getId());
            entry.put("game", session.getGame().getName());
            entry.put("teams", teamNames);
            running.add(entry);
        }

        return running;
    }

    private static List<Map<String, Object>> buildOnline(List<WebPlayer> order, WebPlayer viewer) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (WebPlayer p : order) {
            Integer sessionId = null;
            if (p.getState() == PlayerState.IN_GAME) {
                sessionId = p.getCurrentGame().getId();
            }

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("name", p.getName());
            entry.put("emoji", p.getEmoji());
            entry.put("you", p == viewer);
            entry.put("game", sessionId);
            result.add(entry);
        }
        return result;
    }

    private static boolean canRejoin(WebPlayer viewer) {
        GameSession session = viewer.getCurrentGame();
        if (session == null) {
            return false;
        }

        WebGame game = session.getGame();
        return game != null && game instanceof Rejoinable;
    }

}
