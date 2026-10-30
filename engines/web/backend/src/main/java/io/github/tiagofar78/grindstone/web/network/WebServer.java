package io.github.tiagofar78.grindstone.web.network;

import java.time.Duration;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.tiagofar78.grindstone.web.WebPlayer;
import io.github.tiagofar78.grindstone.web.playground.Playground;
import io.github.tiagofar78.grindstone.web.playground.PlaygroundManager;
import io.javalin.Javalin;

public class WebServer {

    private static final int PORT = 8080;

    public static final PlaygroundManager playgroundManager = new PlaygroundManager();
    public static final Playground playground = new Playground();

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.jetty.modifyWebSocketServletFactory(
                    factory -> factory.setIdleTimeout(Duration.ofMinutes(10))
            );
        });

        app.ws("/ws", ws -> {
            ws.onConnect(ctx -> {
                String playgroundId = null; // Change when multiple playgrounds are expected
                WebPlayer player = new WebPlayer(ctx.queryParam("name"), ctx.queryParam("emoji"), ctx);
                String error = playgroundManager.joinPlayground(playgroundId, ctx.sessionId(), player);
                if (error != null) {
                    ctx.closeSession(4000, error);
                }
            });

            ws.onMessage(ctx -> {
                JsonNode message = JsonFormatter.toJsonNode(ctx.message());
                if (message == null) {
                    ctx.send(Map.of("type", "error", "error", "Invalid JSON format."));
                    return;
                }

                playgroundManager.receivePlayerMessage(ctx.sessionId(), message);
            });

            ws.onClose(ctx -> playgroundManager.deleteConnection(ctx.sessionId()));
        });

        app.start(PORT);
        System.out.println("Server started on http://localhost:" + PORT);
    }

}
