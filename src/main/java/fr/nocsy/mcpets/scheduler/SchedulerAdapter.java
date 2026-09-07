package fr.nocsy.mcpets.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class SchedulerAdapter {

    private final Plugin plugin;
    private final boolean foliaServer;

    public SchedulerAdapter(@NotNull final Plugin plugin) {
        this.plugin = plugin;
        this.foliaServer = detectFoliaServer();
    }

    public void executeGlobal(@NotNull final Runnable task) {
        if (foliaServer) {
            Bukkit.getGlobalRegionScheduler().execute(plugin, task);
            return;
        }

        Bukkit.getScheduler().runTask(plugin, task);
    }

    @NotNull
    public SchedulerTask runGlobal(@NotNull final Runnable task) {
        if (foliaServer) {
            return SchedulerTask.fromFoliaTask(Bukkit.getGlobalRegionScheduler()
                    .run(plugin, callback(task)));
        }

        return SchedulerTask.fromBukkitTask(Bukkit.getScheduler().runTask(plugin, task));
    }

    @NotNull
    public SchedulerTask runGlobalDelayed(@NotNull final Runnable task, final long delayTicks) {
        if (foliaServer) {
            return SchedulerTask.fromFoliaTask(Bukkit.getGlobalRegionScheduler()
                    .runDelayed(plugin, callback(task), delayTicks));
        }

        return SchedulerTask.fromBukkitTask(Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks));
    }

    @NotNull
    public SchedulerTask runGlobalAtFixedRate(@NotNull final Runnable task, final long initialDelayTicks,
                                              final long periodTicks) {
        if (foliaServer) {
            return SchedulerTask.fromFoliaTask(Bukkit.getGlobalRegionScheduler()
                    .runAtFixedRate(plugin, callback(task), initialDelayTicks, periodTicks));
        }

        return SchedulerTask.fromBukkitTask(Bukkit.getScheduler()
                .runTaskTimer(plugin, task, initialDelayTicks, periodTicks));
    }

    @NotNull
    public SchedulerTask runAsync(@NotNull final Runnable task) {
        if (foliaServer) {
            return SchedulerTask.fromFoliaTask(Bukkit.getAsyncScheduler().runNow(plugin, callback(task)));
        }

        return SchedulerTask.fromBukkitTask(Bukkit.getScheduler().runTaskAsynchronously(plugin, task));
    }

    @NotNull
    public SchedulerTask runAsyncDelayed(@NotNull final Runnable task, final long delay,
                                         @NotNull final TimeUnit timeUnit) {
        if (foliaServer) {
            return SchedulerTask.fromFoliaTask(Bukkit.getAsyncScheduler()
                    .runDelayed(plugin, callback(task), delay, timeUnit));
        }

        return SchedulerTask.fromBukkitTask(Bukkit.getScheduler()
                .runTaskLaterAsynchronously(plugin, task, toTicks(delay, timeUnit)));
    }

    @NotNull
    public SchedulerTask runAsyncAtFixedRate(@NotNull final Runnable task, final long initialDelay,
                                             final long period, @NotNull final TimeUnit timeUnit) {
        if (foliaServer) {
            return SchedulerTask.fromFoliaTask(Bukkit.getAsyncScheduler()
                    .runAtFixedRate(plugin, callback(task), initialDelay, period, timeUnit));
        }

        return SchedulerTask.fromBukkitTask(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task,
                toTicks(initialDelay, timeUnit), toTicks(period, timeUnit)));
    }

    @NotNull
    public SchedulerTask runAtLocation(@NotNull final Location location, @NotNull final Runnable task) {
        if (foliaServer) {
            return SchedulerTask.fromFoliaTask(Bukkit.getRegionScheduler()
                    .run(plugin, location, callback(task)));
        }

        return SchedulerTask.fromBukkitTask(Bukkit.getScheduler().runTask(plugin, task));
    }

    @NotNull
    public SchedulerTask runAtLocationDelayed(@NotNull final Location location, @NotNull final Runnable task,
                                               final long delayTicks) {
        if (foliaServer) {
            return SchedulerTask.fromFoliaTask(Bukkit.getRegionScheduler()
                    .runDelayed(plugin, location, callback(task), delayTicks));
        }

        return SchedulerTask.fromBukkitTask(Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks));
    }

    @NotNull
    public SchedulerTask runAtLocationFixedRate(@NotNull final Location location, @NotNull final Runnable task,
                                                final long initialDelayTicks, final long periodTicks) {
        if (foliaServer) {
            return SchedulerTask.fromFoliaTask(Bukkit.getRegionScheduler()
                    .runAtFixedRate(plugin, location, callback(task), initialDelayTicks, periodTicks));
        }

        return SchedulerTask.fromBukkitTask(Bukkit.getScheduler()
                .runTaskTimer(plugin, task, initialDelayTicks, periodTicks));
    }

    @NotNull
    public SchedulerTask runAtEntity(@NotNull final Entity entity, @NotNull final Runnable task) {
        if (foliaServer) {
            return SchedulerTask.fromFoliaTask(entity.getScheduler().run(plugin, callback(task), () -> {
            }));
        }

        return SchedulerTask.fromBukkitTask(Bukkit.getScheduler().runTask(plugin, task));
    }

    @NotNull
    public SchedulerTask runAtEntityDelayed(@NotNull final Entity entity, @NotNull final Runnable task,
                                             final long delayTicks) {
        if (foliaServer) {
            return SchedulerTask.fromFoliaTask(entity.getScheduler().runDelayed(plugin, callback(task), () -> {
            }, delayTicks));
        }

        return SchedulerTask.fromBukkitTask(Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks));
    }

    @NotNull
    public SchedulerTask runAtEntityFixedRate(@NotNull final Entity entity, @NotNull final Runnable task,
                                              final long initialDelayTicks, final long periodTicks) {
        if (foliaServer) {
            return SchedulerTask.fromFoliaTask(entity.getScheduler()
                    .runAtFixedRate(plugin, callback(task), () -> {
                    }, initialDelayTicks, periodTicks));
        }

        return SchedulerTask.fromBukkitTask(Bukkit.getScheduler()
                .runTaskTimer(plugin, task, initialDelayTicks, periodTicks));
    }

    public boolean isFoliaServer() {
        return foliaServer;
    }

    /**
     * Teleport an entity to a location in a thread-safe way.
     * Uses teleportAsync() which works correctly in both Paper and Folia.
     * In Folia, this is required when teleporting across regions.
     *
     * @param entity The entity to teleport
     * @param location The destination location
     */
    public void teleportEntity(@NotNull final Entity entity, @NotNull final Location location) {
        entity.teleportAsync(location);
    }

    private static boolean detectFoliaServer() {
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @NotNull
    private static Consumer<ScheduledTask> callback(@NotNull final Runnable task) {
        return scheduledTask -> task.run();
    }

    private static long toTicks(final long duration, @NotNull final TimeUnit timeUnit) {
        if (duration <= 0L) {
            return 0L;
        }

        return Math.max(1L, timeUnit.toMillis(duration) / 50L);
    }
}
