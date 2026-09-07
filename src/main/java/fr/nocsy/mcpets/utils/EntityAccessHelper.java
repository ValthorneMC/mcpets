package fr.nocsy.mcpets.utils;

import fr.nocsy.mcpets.MCPets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Helper class for safe entity access in Folia's regionized environment.
 * Provides methods that ensure entity operations happen in the correct region.
 */
public final class EntityAccessHelper {

    private EntityAccessHelper() {
        // Utility class
    }

    /**
     * Safely execute an operation on an entity if it exists and is accessible.
     * The operation will be scheduled on the entity's region if needed.
     *
     * @param entityUuid The UUID of the entity to access
     * @param operation  The operation to perform on the entity
     */
    public static void withEntity(@NotNull UUID entityUuid, @NotNull Consumer<Entity> operation) {
        Entity entity = Bukkit.getEntity(entityUuid);
        if (entity == null) {
            return;
        }

        if (Bukkit.isOwnedByCurrentRegion(entity)) {
            operation.accept(entity);
        } else {
            MCPets.getInstance().getSchedulerAdapter().runAtEntity(entity, () -> operation.accept(entity));
        }
    }

    /**
     * Safely execute an operation on a player if they exist and are accessible.
     * The operation will be scheduled on the player's region if needed.
     *
     * @param playerUuid The UUID of the player to access
     * @param operation  The operation to perform on the player
     */
    public static void withPlayer(@NotNull UUID playerUuid, @NotNull Consumer<Player> operation) {
        Player player = Bukkit.getPlayer(playerUuid);
        if (player == null) {
            return;
        }

        if (Bukkit.isOwnedByCurrentRegion(player)) {
            operation.accept(player);
        } else {
            MCPets.getInstance().getSchedulerAdapter().runAtEntity(player, () -> operation.accept(player));
        }
    }

    /**
     * Check if an entity exists and is currently accessible.
     *
     * @param entityUuid The UUID of the entity to check
     * @return true if the entity exists and is in the current region
     */
    public static boolean isEntityAccessible(@NotNull UUID entityUuid) {
        Entity entity = Bukkit.getEntity(entityUuid);
        return entity != null && Bukkit.isOwnedByCurrentRegion(entity);
    }

    /**
     * Check if a player exists and is currently accessible.
     *
     * @param playerUuid The UUID of the player to check
     * @return true if the player exists and is in the current region
     */
    public static boolean isPlayerAccessible(@NotNull UUID playerUuid) {
        Player player = Bukkit.getPlayer(playerUuid);
        return player != null && Bukkit.isOwnedByCurrentRegion(player);
    }

    /**
     * Get an entity safely, returning null if not accessible.
     *
     * @param entityUuid The UUID of the entity to get
     * @return The entity if it exists and is in the current region, null otherwise
     */
    @Nullable
    public static Entity getEntitySafe(@NotNull UUID entityUuid) {
        Entity entity = Bukkit.getEntity(entityUuid);
        if (entity != null && Bukkit.isOwnedByCurrentRegion(entity)) {
            return entity;
        }
        return null;
    }

    /**
     * Get a player safely, returning null if not accessible.
     *
     * @param playerUuid The UUID of the player to get
     * @return The player if they exist and are in the current region, null otherwise
     */
    @Nullable
    public static Player getPlayerSafe(@NotNull UUID playerUuid) {
        Player player = Bukkit.getPlayer(playerUuid);
        if (player != null && Bukkit.isOwnedByCurrentRegion(player)) {
            return player;
        }
        return null;
    }
}
