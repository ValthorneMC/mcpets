package fr.nocsy.mcpets.api;

import java.util.Map;
import java.util.List;
import java.util.UUID;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import fr.nocsy.mcpets.scheduler.SchedulerTask;

import java.util.function.IntConsumer;

public class MCPetsAPI {

    /**
     * Get plugin instance
     */
    public static MCPets getPluginInstance() {
        return MCPets.getInstance();
    }

    /**
     * Returns pet object instance | not an active pet instance
     */
    public static Pet getObjectPet(final String id) {
        return Pet.getFromId(id);
    }

    /**
     * Returns the first active pet for the player if they have one.
     * Returns null if no pet is attached to the player.
     * @deprecated Use getActivePetsForPlayer() for multiple pets support
     */
    @Deprecated
    public static Pet getActivePet(final UUID playerUUID) {
        final List<Pet> pets = Pet.getActivePets().get(playerUUID);
        return (pets != null && !pets.isEmpty()) ? pets.getFirst() : null;
    }

    /**
     * Returns all active pets for the player.
     */
    public static List<Pet> getActivePetsForPlayer(final UUID playerUUID) {
        return Pet.getActivePetsForOwner(playerUUID);
    }

    /**
     * Returns the map where Key=UuidPlayer | Value=List<Pet>
     * @deprecated API signature changed - returns Map<UUID, List<Pet>> now
     */
    @Deprecated
    public static Map<UUID, List<Pet>> getActivePets() {
        return Pet.getActivePets();
    }

    /**
     * Returns a list of object pet instance | not active pets
     */
    public static List<Pet> getObjectPets() {
        return Pet.getObjectPets();
    }

    /**
     * Get the list of pet that are available to the specified player (permission based)
     */
    public static List<Pet> getAvailablePets(final Player p) {
        return Pet.getAvailablePets(p);
    }

    /**
     * Set the active pet of the player.
     * This synchronous method must be called from the player's entity scheduler.
     * Returns a value giving what happened after calling the method.
     */
    public static int setActivePet(@NotNull final Pet pet, @NotNull final Player player,
                                   final boolean checkPermission) {
        pet.setCheckPermission(checkPermission);
        return pet.spawn(player.getLocation(), true);
    }

    /**
     * Set the active pet of the player from any thread and report the result on the player's entity scheduler.
     */
    @NotNull
    public static SchedulerTask setActivePetAsync(@NotNull final Pet pet, @NotNull final Player player,
                                                  final boolean checkPermission,
                                                  @NotNull final IntConsumer resultHandler) {
        return MCPets.getInstance().getSchedulerAdapter().runAtEntity(player, () -> {
            pet.setCheckPermission(checkPermission);
            resultHandler.accept(pet.spawn(player.getLocation(), true));
        });
    }
}
