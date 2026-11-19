package io.github.tiagofar78.grindstone.web.player.state;

public class InLobby implements PlayerState {

    @Override
    public boolean isInLobby() {
        return true;
    }

    @Override
    public PlayerState connect() {
        throw new UnsupportedOperationException("Player is already connected.");
    }

    @Override
    public PlayerState disconnect() {
        return new Disconnected();
    }

    @Override
    public PlayerState gameStarted() {
        return new InGame();
    }

    @Override
    public PlayerState gameFinished() {
        throw new UnsupportedOperationException("Player removed from game they weren't playing");
    }

    @Override
    public PlayerState leave() {
        throw new UnsupportedOperationException("There is no game to leave from.");
    }

    @Override
    public PlayerState rejoin() {
        throw new UnsupportedOperationException("There is no game to rejoin to.");
    }

}
