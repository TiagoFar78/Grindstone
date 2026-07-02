package io.github.tiagofar78.grindstone.cli;

import java.time.Duration;

import io.github.tiagofar78.grindstone.game.SchedulerService;

public class CLISchedulerService implements SchedulerService {

    @Override
    public void runAfter(Duration delay, Runnable task) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            task.run();
        }).start();
    }

}
