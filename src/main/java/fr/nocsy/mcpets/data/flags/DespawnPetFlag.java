package fr.nocsy.mcpets.data.flags;

import java.util.UUID;

import org.bukkit.entity.Player;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.scheduler.SchedulerTask;
import fr.nocsy.mcpets.utils.EntityAccessHelper;

public class DespawnPetFlag extends AbstractFlag implements StoppableFlag {

    SchedulerTask task;

    public static String NAME = "mcpets-despawn";

    public DespawnPetFlag(final MCPets instance) {
        super(NAME, false, instance);
    }

    @Override
    public void register() {
        super.register();
    }

    @Override
    public void launch() {
        if (getFlag() == null) {
            MCPets.getLog().warning("Flag " + getFlagName() + " couldn't not be launched as it's null. Please contact Nocsy.");
            return;
        }

        MCPets.getLog().info("Starting flag " + getFlagName() + ".");

        task = MCPets.getInstance().getSchedulerAdapter().runGlobalAtFixedRate(() -> {
            if (MCPets.getMythicMobs() == null) return;

            for (UUID owner : Pet.getActivePets().keySet()) {
                EntityAccessHelper.withPlayer(owner, pl -> {
                    if (!testState(pl.getLocation())) return;

                    for (Pet pet : Pet.getActivePetsForOwner(owner)) {
                        pet.scheduleDespawn(PetDespawnReason.TELEPORT);
                    }

                    Language.CANT_FOLLOW_HERE.sendMessage(pl);
                });
            }
        }, 0L, 20L);
    }

    @Override
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

}
