package io.github.tiagofar78.grindstone.web.player.state;

public class Disconnected implements PlayerState {

    @Override
    public PlayerState connect() {
        return new InLobby();
    }

    @Override
    public PlayerState disconnect() {
        throw new UnsupportedOperationException("Player is already disconnected.");
    }

    @Override
    public PlayerState gameStarted() {
        throw new UnsupportedOperationException("Player is not connected.");
    }

    @Override
    public PlayerState gameFinished() {
        throw new UnsupportedOperationException("Player was not in a game.");
    }

    @Override
    public PlayerState leave() {
        throw new UnsupportedOperationException("Player is not connected.");
    }

    @Override
    public PlayerState rejoin() {
        throw new UnsupportedOperationException("Player is not connected.");
    }

}
