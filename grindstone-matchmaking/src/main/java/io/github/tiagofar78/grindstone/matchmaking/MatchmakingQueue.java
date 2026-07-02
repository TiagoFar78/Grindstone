package io.github.tiagofar78.grindstone.matchmaking;

public interface MatchmakingQueue<T> {

    boolean enqueue(Party<T> party);

    boolean dequeue(Party<T> party);

}
