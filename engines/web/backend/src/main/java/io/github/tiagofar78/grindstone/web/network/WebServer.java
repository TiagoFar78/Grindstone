package io.github.tiagofar78.grindstone.web.network;

import java.time.Duration;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.tiagofar78.grindstone.web.playground.LoginService;
import io.github.tiagofar78.grindstone.web.playground.Playground;
import io.javalin.Javalin;

public class WebServer {

    private static final int PORT = 8080;

    public static final Playground playground = new Playground();
    private static final LoginService loginService = new LoginService(playground);

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.jetty.modifyWebSocketServletFactory(
                    factory -> factory.setIdleTimeout(Duration.ofMinutes(10))
            );
        });

        app.ws("/ws", ws -> {
            ws.onConnect(ctx -> {
                loginService.onConnect(ctx.sessionId(), ctx);
            });

            ws.onMessage(ctx -> {
                JsonNode message = JsonFormatter.toJsonNode(ctx.message());
                if (message == null) {
                    ctx.send(Map.of("type", "error", "error", "Invalid JSON format. Report this to an administrator."));
                    return;
                }

                String sessionId = ctx.sessionId();
                if (!loginService.isLoggedIn(sessionId)) {
                    loginService.process(sessionId, ctx, message);
                    return;
                }

                playground.process(loginService.getLoggedIn(sessionId), message);
            });

            ws.onClose(ctx -> {
                String sessionId = ctx.sessionId();
                if (!loginService.isLoggedIn(sessionId)) {
                    loginService.cancel(sessionId);
                    return;
                }

                String name = loginService.cancel(sessionId);
                if (name != null) {
                    playground.disconnect(name);
                }
            });
        });

        app.start(PORT);
        System.out.println("Server started on http://localhost:" + PORT);
    }

}
