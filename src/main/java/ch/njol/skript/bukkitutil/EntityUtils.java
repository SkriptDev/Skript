package ch.njol.skript.bukkitutil;

import ch.njol.skript.Skript;
import io.papermc.paper.world.flag.FeatureDependant;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * Utility class for quick {@link Entity} methods
 */
public class EntityUtils {

	// Added in Paper 1.21.3 (possibly)
	private static final boolean HAS_FEATURE_FLAG = Skript.classExists("io.papermc.paper.world.flag.FeatureDependant");

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

	/**
	 * Check if an EntityType can spawn in a world
	 * <br>
	 * This may be due to a Bukkit restriction or a Minecraft FeatureFlag
	 *
	 * @param entityType EntityType to check for spawning
	 * @param world      World to check if it can spawn
	 * @return True if the type can spawn
	 */
	@SuppressWarnings({"removal", "ConstantValue"})
	public static boolean canSpawn(Object entityType, World world) {
		// Don't get me started on how stupid this is
		// For some reason it errors for the FeatureDependant class if using EntityType
		if (!(entityType instanceof EntityType et)) {
			throw new IllegalArgumentException("This object should only ever be an EntityType");
		}
		if (!et.isSpawnable()) return false;
		if (HAS_FEATURE_FLAG && entityType instanceof FeatureDependant dependant) {
			return world.isEnabled(dependant);
		} else {
			return et.isEnabledByFeature(world);
		}
	}

}
