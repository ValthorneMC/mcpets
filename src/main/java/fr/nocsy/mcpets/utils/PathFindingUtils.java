package fr.nocsy.mcpets.utils;

import fr.nocsy.mcpets.MCPets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PathFindingUtils {

    private static final ConcurrentHashMap<UUID, AbstractLocation> registry = new ConcurrentHashMap<>();

    /**
     * Move the entity to the specified location
     * Must be called from the entity's region thread
     */
    public static void moveTo(AbstractEntity entity, AbstractLocation destination) {
        if (entity == null || destination == null) {
            return;
        }

        // Verify we're in the correct region for this entity
        if (!Bukkit.isOwnedByCurrentRegion(entity.getBukkitEntity())) {
            return;
        }

        MCPets.getMythicMobs().getVolatileCodeHandler().getAIHandler().navigateToLocation(entity, destination, 1);
    }

    /**
     * Stop the entity at its location
     * Must be called from the entity's region thread
     */
    public static void stop(AbstractEntity entity, UUID owner) {
        if (entity == null || owner == null) {
            return;
        }

        // Verify we're in the correct region for this entity
        if (!Bukkit.isOwnedByCurrentRegion(entity.getBukkitEntity())) {
            return;
        }

        AbstractLocation currentLoc = registry.get(owner);
        if (currentLoc != null) {
            AbstractLocation entityLoc = entity.getLocation();
            if (currentLoc.getBlockX() == entityLoc.getBlockX() &&
                    currentLoc.getBlockY() == entityLoc.getBlockY() &&
                    currentLoc.getBlockZ() == entityLoc.getBlockZ()) {
                return;
            }
        }

        moveTo(entity, entity.getLocation());
        registry.put(owner, entity.getLocation());
    }

    /**
     * Clear the registry entry for an owner
     */
    public static void clearRegistry(UUID owner) {
        if (owner != null) {
            registry.remove(owner);
        }
    }
}
