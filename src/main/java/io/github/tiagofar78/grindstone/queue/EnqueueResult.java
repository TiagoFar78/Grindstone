package io.github.tiagofar78.grindstone.queue;

public enum EnqueueResult {

    SUCCESS("joinqueue.success"),
    NOT_ALLOWED_BY_PARTY("not_allowed"),
    ALREADY_IN_QUEUE("already_in_queue"),
    STILL_IN_GAME("still_in_game"),
    DISCONNECTED_FROM_ONGOING_GAME("disconnect_penalty"),
    PARTY_TOO_LARGE("large_party");

    private String messageKey;

    EnqueueResult(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return this.messageKey;
    }

}
