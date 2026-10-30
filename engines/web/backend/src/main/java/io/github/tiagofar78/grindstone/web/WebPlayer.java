package io.github.tiagofar78.grindstone.web;

import io.github.tiagofar78.grindstone.enginebridge.player.Messager;
import io.github.tiagofar78.grindstone.game.MessagesChannel;
import io.github.tiagofar78.grindstone.game.Player;
import io.github.tiagofar78.grindstone.web.network.JsonFormatter;
import io.github.tiagofar78.grindstone.web.network.messages.Outbound;
import io.github.tiagofar78.grindstone.web.network.messages.WebMessage;
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
        if (!ctx.session.isOpen()) {
            return;
        }

        String json = message.startsWith("{") ? message : JsonFormatter.toJson(toOutboundMessage(message, channel));
        synchronized (ctx) {
            ctx.send(json);
        }
    }

    private Outbound toOutboundMessage(String msg, MessagesChannel channel) {
        if (channel == MessagesChannel.ACTION_BAR) {
            return new Outbound.Error(msg);
        }

        return new Outbound.Chat(msg);
    }

    public void sendMessage(WebMessage message) {
        sendMessage(JsonFormatter.toJson(message), MessagesChannel.CHAT);
    }

    public void updateConnection(WebPlayer player) {
        this.ctx = player.ctx;
    }

}
