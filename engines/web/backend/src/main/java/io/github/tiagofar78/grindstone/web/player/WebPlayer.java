package io.github.tiagofar78.grindstone.web.player;

import io.github.tiagofar78.grindstone.enginebridge.player.Messager;
import io.github.tiagofar78.grindstone.game.MessagesChannel;
import io.github.tiagofar78.grindstone.game.Player;
import io.github.tiagofar78.grindstone.web.network.JsonFormatter;
import io.github.tiagofar78.grindstone.web.network.messages.Outbound;
import io.github.tiagofar78.grindstone.web.network.messages.WebMessage;
import io.github.tiagofar78.grindstone.web.player.state.InLobby;
import io.github.tiagofar78.grindstone.web.player.state.PlayerState;
import io.github.tiagofar78.grindstone.web.playground.GameSession;
import io.javalin.websocket.WsContext;

public class WebPlayer implements Messager {

    private WsContext ctx;

    private String name;
    private String emoji;
    private PlayerState state;

    private GameSession currentSession;
    private Player<?> corePlayer;

    public WebPlayer(String name, String emoji, WsContext ctx) {
        this.name = name;
        this.emoji = emoji;
        this.ctx = ctx;
        this.state = new InLobby();
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

    public GameSession getCurrentSession() {
        return currentSession;
    }

    public Player<?> getCorePlayer() {
        return corePlayer;
    }

    /**
     * !!! THIS IS ONLY TO BE USED BY GAME FACTORIES !!!<br>
     * <br>
     * This method is required because in StartGameCommand#excute we can't
     * easily map each WebPlayer to the corresponding Player from grindstone-core.
     * The perfect solution would be to not have this method and have the corePlayer
     * assignment in the gameStarted method
     */
    public void setCorePlayer(Player<?> corePlayer) {
        this.corePlayer = corePlayer;
    }

    // State

    protected void setState(PlayerState state) {
        this.state = state;
    }

    public boolean isOnline() {
        return state.isInLobby() || state.isInGame();
    }

    public boolean isInLobby() {
        return state.isInLobby();
    }

    public boolean isInGame() {
        return state.isInGame();
    }

    public boolean canRejoin() {
        return state.canRejoin();
    }

    public void connect(WsContext ctx) {
        this.ctx = ctx;
        this.state = state.connect();
    }

    public void disconnect() {
        this.state = state.disconnect();
    }

    public void gameStarted(GameSession session) {
        this.currentSession = session;
        this.state = state.gameStarted();
    }

    public void gameFinished() {
        this.state = state.gameFinished();
    }

    public void leaveGame() {
        this.state = state.leave();
    }

    public void rejoin() {
        this.state = state.rejoin();
    }

    // Messages

    @Override
    public void sendMessage(String message, MessagesChannel channel) {
        if (!ctx.session.isOpen()) {
            return;
        }

        String json = message.startsWith("{") ? message : JsonFormatter.toJson(new Outbound.Error(message));
        synchronized (ctx) {
            ctx.send(json);
        }
    }

    public void sendMessage(WebMessage message) {
        sendMessage(JsonFormatter.toJson(message), MessagesChannel.CHAT);
    }

}
