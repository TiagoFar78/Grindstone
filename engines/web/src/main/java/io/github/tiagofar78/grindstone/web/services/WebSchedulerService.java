package io.github.tiagofar78.grindstone.web.services;

import java.time.Duration;

import io.github.tiagofar78.grindstone.services.SchedulerService;

public class WebSchedulerService implements SchedulerService {

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
