package io.github.tiagofar78.grindstone.game;

import java.time.Duration;

public interface SchedulerService {
    
    void runAfter(Duration delay, Runnable task);

}
