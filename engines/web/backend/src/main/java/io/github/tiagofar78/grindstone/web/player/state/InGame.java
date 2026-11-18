package io.github.tiagofar78.grindstone.web.player.state;

public class InGame implements PlayerState {

    @Override
    public boolean isInGame() {
        return true;
    }

    @Override
    public PlayerState connect() {
        throw new UnsupportedOperationException("Player didn't disconnect.");
    }

    @Override
    public PlayerState disconnect() {
        return new DisconnectedFromGame();
    }

    @Override
    public PlayerState gameStarted() {
        throw new UnsupportedOperationException("Player is already in a game.");
    }

    @Override
    public PlayerState gameFinished() {
        return new InLobby();
    }

    @Override
    public PlayerState leave() {
        return new LeftGame();
    }

    @Override
    public PlayerState rejoin() {
        throw new UnsupportedOperationException("Player didn't leave the game.");
    }

}
