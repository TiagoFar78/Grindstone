package io.github.tiagofar78.grindstone.web.player.state;

public class LeftGameAndDisconnected implements PlayerState {

    @Override
    public PlayerState connect() {
        return new LeftGame();
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
        return new Disconnected();
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
