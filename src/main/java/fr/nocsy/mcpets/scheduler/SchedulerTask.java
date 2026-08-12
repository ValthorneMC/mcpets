package fr.nocsy.mcpets.scheduler;

import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class SchedulerTask {

    private final BukkitTask bukkitTask;
    private final Object foliaTask;

    private SchedulerTask(final BukkitTask bukkitTask, final Object foliaTask) {
        this.bukkitTask = bukkitTask;
        this.foliaTask = foliaTask;
    }

    public static SchedulerTask empty() {
        return new SchedulerTask(null, null);
    }

    public static SchedulerTask fromBukkitTask(final BukkitTask task) {
        return new SchedulerTask(task, null);
    }

    public static SchedulerTask fromFoliaTask(final Object task) {
        return new SchedulerTask(null, task);
    }

    public void cancel() {
        if (bukkitTask != null) {
            bukkitTask.cancel();
            return;
        }

        if (foliaTask != null) {
            invoke("cancel");
        }
    }

    public boolean isCancelled() {
        if (bukkitTask != null) {
            return bukkitTask.isCancelled();
        }

        return foliaTask == null || (boolean) invoke("isCancelled");
    }

    private Object invoke(final String methodName) {
        try {
            final Method method = foliaTask.getClass().getMethod(methodName);
            return method.invoke(foliaTask);
        } catch (InvocationTargetException exception) {
            throw new IllegalStateException("Failed to invoke scheduler task method " + methodName,
                    exception.getCause());
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Scheduler task method is unavailable: " + methodName, exception);
        }
    }
}
