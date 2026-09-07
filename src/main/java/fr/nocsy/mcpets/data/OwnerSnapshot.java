package fr.nocsy.mcpets.data;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;

public final class OwnerSnapshot {

    @NotNull
    private final Location location;
    private final boolean dead;
    private final boolean flying;
    private final boolean gliding;
    private final boolean onGround;
    private final boolean hasPermission;

    public OwnerSnapshot(@NotNull final Location location, final boolean dead, final boolean flying,
                         final boolean gliding, final boolean onGround, final boolean hasPermission) {
        this.location = location;
        this.dead = dead;
        this.flying = flying;
        this.gliding = gliding;
        this.onGround = onGround;
        this.hasPermission = hasPermission;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isFlying() {
        return flying;
    }

    public boolean isGliding() {
        return gliding;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public boolean hasPermission() {
        return hasPermission;
    }
}
