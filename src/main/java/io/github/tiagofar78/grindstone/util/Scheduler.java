package io.github.tiagofar78.grindstone.util;

import io.github.tiagofar78.grindstone.Grindstone;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public final class Scheduler {

    private Scheduler() {
    }

    private static final int TICKS_PER_SECOND = 20;

    public static BukkitTask runLater(int seconds, Runnable runnable) {
        return Bukkit.getScheduler().runTaskLater(Grindstone.getInstance(), runnable, seconds * TICKS_PER_SECOND);
    }


}
