package io.github.tiagofar78.grindstone.queue;

public enum DequeueResult {

    SUCCESS("leavequeue.success"),
    NOT_ALLOWED_BY_PARTY("leavequeue.not_allowed"),
    NOT_IN_QUEUE("leavequeue.not_in_queue");

    private String messageKey;

    DequeueResult(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return this.messageKey;
    }

}
