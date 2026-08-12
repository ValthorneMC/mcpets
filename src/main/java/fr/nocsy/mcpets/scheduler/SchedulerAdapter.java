package fr.nocsy.mcpets.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class SchedulerAdapter {

    private final Plugin plugin;

    public SchedulerAdapter(final Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    public void executeGlobal(final Runnable task) {
        Bukkit.getGlobalRegionScheduler().execute(plugin, task);
    }

    public ScheduledTask runGlobal(final Runnable task) {
        return Bukkit.getGlobalRegionScheduler().run(plugin, scheduledTask -> task.run());
    }

    public ScheduledTask runGlobalDelayed(final Runnable task, final long delayTicks) {
        return Bukkit.getGlobalRegionScheduler().runDelayed(plugin, scheduledTask -> task.run(), delayTicks);
    }

    public ScheduledTask runGlobalAtFixedRate(final Runnable task, final long initialDelayTicks, final long periodTicks) {
        return Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> task.run(), initialDelayTicks, periodTicks);
    }

    public ScheduledTask runAsync(final Runnable task) {
        return Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> task.run());
    }

    public ScheduledTask runAsyncDelayed(final Runnable task, final long delay, final TimeUnit timeUnit) {
        return Bukkit.getAsyncScheduler().runDelayed(plugin, scheduledTask -> task.run(), delay, timeUnit);
    }

    public ScheduledTask runAsyncAtFixedRate(final Runnable task, final long initialDelay, final long period,
                                             final TimeUnit timeUnit) {
        return Bukkit.getAsyncScheduler().runAtFixedRate(plugin, scheduledTask -> task.run(), initialDelay, period,
                timeUnit);
    }

    public ScheduledTask runAtLocation(final Location location, final Runnable task) {
        return Bukkit.getRegionScheduler().run(plugin, location, scheduledTask -> task.run());
    }

    public ScheduledTask runAtLocationDelayed(final Location location, final Runnable task, final long delayTicks) {
        return Bukkit.getRegionScheduler().runDelayed(plugin, location, scheduledTask -> task.run(), delayTicks);
    }

    public ScheduledTask runAtLocationFixedRate(final Location location, final Runnable task,
                                                final long initialDelayTicks, final long periodTicks) {
        return Bukkit.getRegionScheduler().runAtFixedRate(plugin, location, scheduledTask -> task.run(),
                initialDelayTicks, periodTicks);
    }

    public ScheduledTask runAtEntity(final Entity entity, final Runnable task) {
        return entity.getScheduler().run(plugin, scheduledTask -> task.run(), () -> {
        });
    }

    public ScheduledTask runAtEntityDelayed(final Entity entity, final Runnable task, final long delayTicks) {
        return entity.getScheduler().runDelayed(plugin, scheduledTask -> task.run(), () -> {
        }, delayTicks);
    }

    public ScheduledTask runAtEntityFixedRate(final Entity entity, final Runnable task,
                                              final long initialDelayTicks, final long periodTicks) {
        return entity.getScheduler().runAtFixedRate(plugin, scheduledTask -> task.run(), () -> {
        }, initialDelayTicks, periodTicks);
    }
}
