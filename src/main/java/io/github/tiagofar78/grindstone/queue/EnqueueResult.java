package io.github.tiagofar78.grindstone.queue;

public enum EnqueueResult {

    SUCCESS,
    NOT_ALLOWED_BY_PARTY,
    ALREADY_IN_QUEUE,
    STILL_IN_GAME,
    DISCONNECTED_FROM_ONGOING_GAME,
    PARTY_TOO_LARGE

}
