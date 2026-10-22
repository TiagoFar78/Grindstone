package io.github.tiagofar78.grindstone.web;

import java.util.Map;

import io.github.tiagofar78.grindstone.enginebridge.player.Messager;
import io.github.tiagofar78.grindstone.game.MessagesChannel;
import io.github.tiagofar78.grindstone.game.Player;
import io.github.tiagofar78.grindstone.web.network.WebServer;
import io.github.tiagofar78.grindstone.web.playground.GameSession;
import io.javalin.websocket.WsContext;

public class WebPlayer implements Messager {

    private WsContext ctx;

    private String name;
    private String emoji;
    private PlayerState state;
    private boolean isOnline;

    private GameSession currentGame;
    private Player<?> corePlayer;

    public WebPlayer(String name, String emoji, WsContext ctx) {
        this.name = name;
        this.emoji = emoji;
        this.ctx = ctx;
        this.state = PlayerState.IN_LOBBY;
    }

    public void setCtx(WsContext ctx) {
        this.ctx = ctx;
    }

    public WsContext getCtx() {
        return ctx;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public PlayerState getState() {
        return state;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public GameSession getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(GameSession game) {
        currentGame = game;
    }

    public Player<?> getCorePlayer() {
        return corePlayer;
    }

    public void setCorePlayer(Player<?> corePlayer) {
        this.corePlayer = corePlayer;
    }

    @Override
    public void sendMessage(String message, MessagesChannel channel) {
        String json = message.startsWith("{") ? message : WebServer.toJson(Map.of("type", "chat", "text", message));
        synchronized (ctx) {
            ctx.send(json);
        }
    }

    public void sendMessage(String message) {
        sendMessage(message, MessagesChannel.CHAT);
    }

}
