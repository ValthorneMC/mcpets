package fr.nocsy.mcpets.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

public final class SchedulerTask {

    @Nullable
    private final BukkitTask bukkitTask;

    @Nullable
    private final ScheduledTask foliaTask;

    private SchedulerTask(@Nullable final BukkitTask bukkitTask, @Nullable final ScheduledTask foliaTask) {
        this.bukkitTask = bukkitTask;
        this.foliaTask = foliaTask;
    }

    public static SchedulerTask fromBukkitTask(@Nullable final BukkitTask task) {
        return new SchedulerTask(task, null);
    }

    public static SchedulerTask fromFoliaTask(@Nullable final ScheduledTask task) {
        return new SchedulerTask(null, task);
    }

    public void cancel() {
        if (bukkitTask != null) {
            bukkitTask.cancel();
        } else if (foliaTask != null) {
            foliaTask.cancel();
        }
    }

    public boolean isCancelled() {
        if (bukkitTask != null) {
            return bukkitTask.isCancelled();
        }

        return foliaTask == null || foliaTask.isCancelled();
    }
}
