package fr.nocsy.mcpets.utils;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import fr.nocsy.mcpets.scheduler.SchedulerTask;

public class PetTimer {

    @Getter
    private static final ConcurrentHashMap<PetTimer, SchedulerTask> runningTimers = new ConcurrentHashMap<>();

    @Getter
    private int cooldown;
    @Getter
    private int remainingTime;
    private long frequency;

    @Nullable
    private SchedulerTask task;

    private final Pet pet;

    private final Runnable endingRunnable;

    /**
     * Constructor
     * Frequency giving the tick when repeating the task
     */
    public PetTimer(final Pet pet, int cooldown, long frequency, Runnable endingRunnable) {
        this.pet = pet;
        this.cooldown = cooldown;
        this.remainingTime = 0;
        this.frequency = frequency;
        this.endingRunnable = endingRunnable;
    }

    public void launch(Runnable runnable) {
        // If it's running then cancel the current scheduler
        if (isRunning())
            stop(null);
        remainingTime = cooldown;
        final Runnable timerTask = () -> {
            if (cooldown != Integer.MAX_VALUE)
                remainingTime--;
            if (remainingTime <= 0)
                stop(endingRunnable);

            if (runnable != null)
                runnable.run();
        };

        if (pet.getActiveMob() != null && pet.getActiveMob().getEntity().getBukkitEntity() != null) {
            task = MCPets.getInstance().getSchedulerAdapter().runAtEntityFixedRate(
                    pet.getActiveMob().getEntity().getBukkitEntity(), timerTask, 0L, frequency);
        } else {
            final Player owner = pet.getOwner() == null ? null : Bukkit.getPlayer(pet.getOwner());
            if (owner != null) {
                task = MCPets.getInstance().getSchedulerAdapter().runAtEntityFixedRate(
                        owner, timerTask, 0L, frequency);
            } else {
                task = MCPets.getInstance().getSchedulerAdapter().runGlobalAtFixedRate(timerTask, 0L, frequency);
            }
        }
        runningTimers.put(this, task);
    }

    public void stop(Runnable runnable) {
        if (task != null) {
            task.cancel();
            task = null;
        }
        runningTimers.remove(this);
        remainingTime = 0;
        if (runnable != null)
            runnable.run();
    }

    public boolean isRunning() {
        return remainingTime > 0;
    }

    /**
     * Cancel all running timers
     */
    public static void cancelAllTimers() {
        for (SchedulerTask task : runningTimers.values()) {
            if (task != null) {
                task.cancel();
            }
        }
        runningTimers.clear();
    }
}
