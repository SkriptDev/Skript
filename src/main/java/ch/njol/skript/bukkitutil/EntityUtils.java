package ch.njol.skript.bukkitutil;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * Utility class for quick {@link Entity} methods
 */
public class EntityUtils {
	/**
	 * Teleports the given entity to the given location.
	 * Teleports to the given location in the entity's world if the location's world is null.
	 */
	public static void teleport(Entity entity, Location location) {
		if (location.getWorld() == null) {
			location = location.clone();
			location.setWorld(entity.getWorld());
		}

		entity.teleport(location);
	}

}
