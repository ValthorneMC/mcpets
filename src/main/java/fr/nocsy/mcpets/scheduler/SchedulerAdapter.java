package fr.nocsy.mcpets.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.concurrent.TimeUnit;

public final class SchedulerAdapter {

    private final Plugin plugin;
    private final boolean foliaServer;
    private final Object globalRegionScheduler;
    private final Object asyncScheduler;
    private final Object regionScheduler;

    public SchedulerAdapter(final Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.foliaServer = detectFoliaServer();
        this.globalRegionScheduler = foliaServer ? invokeStatic("getGlobalRegionScheduler") : null;
        this.asyncScheduler = foliaServer ? invokeStatic("getAsyncScheduler") : null;
        this.regionScheduler = foliaServer ? invokeStatic("getRegionScheduler") : null;
    }

    public void executeGlobal(final Runnable task) {
        if (foliaServer) {
            invoke(globalRegionScheduler, "execute", Plugin.class, Runnable.class, plugin, task);
            return;
        }

        Bukkit.getScheduler().runTask(plugin, task);
    }

    public SchedulerTask runGlobal(final Runnable task) {
        if (foliaServer) {
            return wrap(invoke(globalRegionScheduler, "run", Plugin.class, Consumer.class, plugin,
                    callback(task)));
        }

        return wrap(Bukkit.getScheduler().runTask(plugin, task));
    }

    public SchedulerTask runGlobalDelayed(final Runnable task, final long delayTicks) {
        if (foliaServer) {
            return wrap(invoke(globalRegionScheduler, "runDelayed", Plugin.class, Consumer.class, long.class,
                    plugin, callback(task), delayTicks));
        }

        return wrap(Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks));
    }

    public SchedulerTask runGlobalAtFixedRate(final Runnable task, final long initialDelayTicks, final long periodTicks) {
        if (foliaServer) {
            return wrap(invoke(globalRegionScheduler, "runAtFixedRate", Plugin.class, Consumer.class, long.class,
                    long.class, plugin, callback(task), initialDelayTicks, periodTicks));
        }

        return wrap(Bukkit.getScheduler().runTaskTimer(plugin, task, initialDelayTicks, periodTicks));
    }

    public SchedulerTask runAsync(final Runnable task) {
        if (foliaServer) {
            return wrap(invoke(asyncScheduler, "runNow", Plugin.class, Consumer.class, plugin, callback(task)));
        }

        return wrap(Bukkit.getScheduler().runTaskAsynchronously(plugin, task));
    }

    public SchedulerTask runAsyncDelayed(final Runnable task, final long delay, final TimeUnit timeUnit) {
        if (foliaServer) {
            return wrap(invoke(asyncScheduler, "runDelayed", Plugin.class, Consumer.class, long.class,
                    TimeUnit.class, plugin, callback(task), delay, timeUnit));
        }

        return wrap(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task,
                timeUnit.toMillis(delay) / 50L));
    }

    public SchedulerTask runAsyncAtFixedRate(final Runnable task, final long initialDelay, final long period,
                                             final TimeUnit timeUnit) {
        if (foliaServer) {
            return wrap(invoke(asyncScheduler, "runAtFixedRate", Plugin.class, Consumer.class, long.class,
                    long.class, TimeUnit.class, plugin, callback(task), initialDelay, period, timeUnit));
        }

        return wrap(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task,
                timeUnit.toMillis(initialDelay) / 50L, timeUnit.toMillis(period) / 50L));
    }

    public SchedulerTask runAtLocation(final Location location, final Runnable task) {
        if (foliaServer) {
            return wrap(invoke(regionScheduler, "run", Plugin.class, Location.class, Consumer.class, plugin,
                    location, callback(task)));
        }

        return wrap(Bukkit.getScheduler().runTask(plugin, task));
    }

    public SchedulerTask runAtLocationDelayed(final Location location, final Runnable task, final long delayTicks) {
        if (foliaServer) {
            return wrap(invoke(regionScheduler, "runDelayed", Plugin.class, Location.class, Consumer.class,
                    long.class, plugin, location, callback(task), delayTicks));
        }

        return wrap(Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks));
    }

    public SchedulerTask runAtLocationFixedRate(final Location location, final Runnable task,
                                                final long initialDelayTicks, final long periodTicks) {
        if (foliaServer) {
            return wrap(invoke(regionScheduler, "runAtFixedRate", Plugin.class, Location.class, Consumer.class,
                    long.class, long.class, plugin, location, callback(task), initialDelayTicks, periodTicks));
        }

        return wrap(Bukkit.getScheduler().runTaskTimer(plugin, task, initialDelayTicks, periodTicks));
    }

    public SchedulerTask runAtEntity(final Entity entity, final Runnable task) {
        if (foliaServer) {
            return wrap(invoke(entityScheduler(entity), "run",
                    new Class<?>[]{Plugin.class, Consumer.class, Runnable.class}, plugin, callback(task), (Runnable) () -> {
                    }));
        }

        return wrap(Bukkit.getScheduler().runTask(plugin, task));
    }

    public SchedulerTask runAtEntityDelayed(final Entity entity, final Runnable task, final long delayTicks) {
        if (foliaServer) {
            return wrap(invoke(entityScheduler(entity), "runDelayed",
                    new Class<?>[]{Plugin.class, Consumer.class, Runnable.class, long.class}, plugin, callback(task),
                    (Runnable) () -> {
                    }, delayTicks));
        }

        return wrap(Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks));
    }

    public SchedulerTask runAtEntityFixedRate(final Entity entity, final Runnable task,
                                              final long initialDelayTicks, final long periodTicks) {
        if (foliaServer) {
            return wrap(invoke(entityScheduler(entity), "runAtFixedRate",
                    new Class<?>[]{Plugin.class, Consumer.class, Runnable.class, long.class, long.class}, plugin,
                    callback(task), (Runnable) () -> {
                    }, initialDelayTicks, periodTicks));
        }

        return wrap(Bukkit.getScheduler().runTaskTimer(plugin, task, initialDelayTicks, periodTicks));
    }

    public boolean isFoliaServer() {
        return foliaServer;
    }

    private static boolean detectFoliaServer() {
        if ("Folia".equalsIgnoreCase(Bukkit.getName())) {
            return true;
        }

        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return "Folia".equalsIgnoreCase(Bukkit.getServer().getName());
        } catch (ClassNotFoundException exception) {
            return false;
        }
    }

    private static Object invokeStatic(final String methodName) {
        try {
            return Bukkit.class.getMethod(methodName).invoke(null);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Folia scheduler API is unavailable", exception);
        }
    }

    private static Object entityScheduler(final Entity entity) {
        try {
            return entity.getClass().getMethod("getScheduler").invoke(entity);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Entity scheduler API is unavailable", exception);
        }
    }

    private static Consumer<Object> callback(final Runnable task) {
        return scheduledTask -> task.run();
    }

    private static Object invoke(final Object target, final String methodName, final Class<?> firstParameterType,
                                 final Class<?> secondParameterType, final Object firstArgument,
                                 final Object secondArgument) {
        return invoke(target, methodName, new Class<?>[]{firstParameterType, secondParameterType},
                firstArgument, secondArgument);
    }

    private static Object invoke(final Object target, final String methodName, final Class<?> firstParameterType,
                                 final Class<?> secondParameterType, final Class<?> thirdParameterType,
                                 final Object firstArgument, final Object secondArgument,
                                 final Object thirdArgument) {
        return invoke(target, methodName, new Class<?>[]{firstParameterType, secondParameterType, thirdParameterType},
                firstArgument, secondArgument, thirdArgument);
    }

    private static Object invoke(final Object target, final String methodName, final Class<?> firstParameterType,
                                 final Class<?> secondParameterType, final Class<?> thirdParameterType,
                                 final Class<?> fourthParameterType, final Object firstArgument,
                                 final Object secondArgument, final Object thirdArgument, final Object fourthArgument) {
        return invoke(target, methodName,
                new Class<?>[]{firstParameterType, secondParameterType, thirdParameterType, fourthParameterType},
                firstArgument, secondArgument, thirdArgument, fourthArgument);
    }

    private static Object invoke(final Object target, final String methodName, final Class<?> firstParameterType,
                                 final Class<?> secondParameterType, final Class<?> thirdParameterType,
                                 final Class<?> fourthParameterType, final Class<?> fifthParameterType,
                                 final Object firstArgument, final Object secondArgument, final Object thirdArgument,
                                 final Object fourthArgument, final Object fifthArgument) {
        return invoke(target, methodName, new Class<?>[]{firstParameterType, secondParameterType, thirdParameterType,
                        fourthParameterType, fifthParameterType}, firstArgument, secondArgument, thirdArgument,
                fourthArgument, fifthArgument);
    }

    private static Object invoke(final Object target, final String methodName, final Class<?>[] parameterTypes,
                                 final Object... arguments) {
        try {
            final Method method = target.getClass().getMethod(methodName, parameterTypes);
            return method.invoke(target, arguments);
        } catch (InvocationTargetException exception) {
            throw new IllegalStateException("Failed to invoke scheduler method " + methodName,
                    exception.getCause());
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Scheduler method is unavailable: " + methodName, exception);
        }
    }

    private static SchedulerTask wrap(final Object task) {
        if (task == null) {
            return SchedulerTask.empty();
        }

        if (task instanceof BukkitTask bukkitTask) {
            return SchedulerTask.fromBukkitTask(bukkitTask);
        }

        return SchedulerTask.fromFoliaTask(task);
    }
}
