package io.github.tiagofar78.grindstone.queue;

public enum EnqueueResult {

    SUCCESS("grindstone.joinqueue.success"),
    NOT_ALLOWED_BY_PARTY("grindstone.not_allowed"),
    ALREADY_IN_QUEUE("grindstone.already_in_queue"),
    STILL_IN_GAME("grindstone.still_in_game"),
    DISCONNECTED_FROM_ONGOING_GAME("grindstone.disconnect_penalty"),
    PARTY_TOO_LARGE("grindstone.large_party");

    private String messageKey;

    EnqueueResult(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return this.messageKey;
    }

}
