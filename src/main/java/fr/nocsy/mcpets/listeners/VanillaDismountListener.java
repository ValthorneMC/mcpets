package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.MCPets;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;

import java.util.UUID;

public class VanillaDismountListener implements Listener {

	@EventHandler
	public void onDismount(EntityDismountEvent e) {
		var entity = e.getEntity();
		if (!(entity instanceof Player))
			return;

		final UUID petUUID = e.getDismounted().getUniqueId();
		MCPets.getInstance().getSchedulerAdapter().runAtEntity(e.getDismounted(),
				() -> MCPets.getModeler().handleVanillaDismount(petUUID, entity));
	}
}
