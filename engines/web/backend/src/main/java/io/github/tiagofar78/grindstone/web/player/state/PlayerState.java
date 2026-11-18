package io.github.tiagofar78.grindstone.web.player.state;

/**
 *
 * See <a href="https://github.com/tiagofar78/grindstone/blob/main/engines/web/docs/PlayerState.md">PlayerState.md</a>
 */
public interface PlayerState {

    default boolean isInLobby() {
        return false;
    }

    default boolean isInGame() {
        return false;
    }

    default boolean canRejoin() {
        return false;
    }

    // State Transitions

    PlayerState connect();

    PlayerState disconnect();

    PlayerState gameStarted();

    PlayerState gameFinished();

    PlayerState leave();

    PlayerState rejoin();

}
