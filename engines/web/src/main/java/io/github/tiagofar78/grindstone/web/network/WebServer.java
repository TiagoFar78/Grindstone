package io.github.tiagofar78.grindstone.web.network;

import java.time.Duration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.tiagofar78.grindstone.web.playground.Playground;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class WebServer {

    private static final int PORT = 8080;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static final Playground playground = new Playground();
    private static final LoginService loginService = new LoginService(playground);

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/web", Location.CLASSPATH);
            config.jetty.modifyWebSocketServletFactory(
                    factory -> factory.setIdleTimeout(Duration.ofMinutes(10))
            );
        });

        app.ws("/ws", ws -> {
            ws.onConnect(ctx -> {
                loginService.onConnect(ctx.sessionId(), ctx);
            });

            ws.onMessage(ctx -> {
                String sessionId = ctx.sessionId();
                if (!loginService.isLoggedIn(sessionId)) {
                    loginService.onMessage(sessionId, ctx, ctx.message());
                    return;
                }

                playground.process(loginService.getLoggedIn(sessionId), ctx.message());
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

    // ################################################# Helpers ##################################################

    public static String toJson(Object payload) {
        try {
            return MAPPER.writeValueAsString(payload);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode parseJson(String raw) throws Exception {
        return MAPPER.readTree(raw);
    }

}
