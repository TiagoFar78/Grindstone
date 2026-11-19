package io.github.tiagofar78.grindstone.web.player.state;

public class LeftGame implements PlayerState {

    @Override
    public boolean isInLobby() {
        return true;
    }

    @Override
    public boolean canRejoin() {
        return true;
    }

    @Override
    public PlayerState connect() {
        throw new UnsupportedOperationException("Player is already connected.");
    }

    @Override
    public PlayerState disconnect() {
        return new LeftGameAndDisconnected();
    }

    @Override
    public PlayerState gameStarted() {
        return new InGame();
    }

    @Override
    public PlayerState gameFinished() {
        return new InLobby();
    }

    @Override
    public PlayerState leave() {
        throw new UnsupportedOperationException("Player is not in a game.");
    }

    @Override
    public PlayerState rejoin() {
        return new InGame();
    }

}
