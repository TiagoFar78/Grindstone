package io.github.tiagofar78.grindstone.queue;

public enum DequeueResult {

    SUCCESS("grindstone.leavequeue.success"),
    NOT_ALLOWED_BY_PARTY("grindstone.leavequeue.not_allowed"),
    NOT_IN_QUEUE("grindstone.leavequeue.not_in_queue");

    private String messageKey;

    DequeueResult(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return this.messageKey;
    }

}
