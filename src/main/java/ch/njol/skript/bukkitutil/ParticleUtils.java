package ch.njol.skript.bukkitutil;

import ch.njol.skript.Skript;
import ch.njol.util.StringUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Vibration;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Utility class for {@link Particle Particles}
 */
public class ParticleUtils {

	private ParticleUtils() {
	}

	private static final Map<String, Particle> PARTICLES = new HashMap<>();
	private static final Map<Particle, String> PARTICLE_NAMES = new HashMap<>();
	private static final boolean HAS_PLAYER_FORCE = Skript.methodExists(Player.class, "spawnParticle",
		Particle.class, Location.class, int.class, double.class, double.class, double.class, double.class, Object.class, boolean.class);
	// Added in Minecraft 1.21.4
	public static final boolean HAS_TRAIL = Skript.classExists("org.bukkit.Particle$Trail");

	static {
		Registry.PARTICLE_TYPE.forEach(particle -> {
			String key = particle.getKey().getKey();
			PARTICLES.put(key, particle);
			PARTICLE_NAMES.put(particle, key);
		});
	}

	/**
	 * Returns a string for docs of all names of particles
	 *
	 * @return Names of all particles in one long string
	 */
	public static String getNamesAsString() {
		List<String> names = new ArrayList<>();
		PARTICLES.forEach((s, particle) -> {
			String name = s;

			if (particle.getDataType() != Void.class) {
				name = name + " [" + getDataType(particle) + "]";
			}
			names.add(name);
		});
		Collections.sort(names);
		return StringUtils.join(names, ", ");
	}

	/**
	 * Get the Minecraft name of a particle
	 *
	 * @param particle Particle to get name of
	 * @return Minecraft name of particle
	 */
	public static String getName(Particle particle) {
		return PARTICLE_NAMES.get(particle);
	}

	/**
	 * Parse a particle by its Minecraft name
	 *
	 * @param key Minecraft name of particle
	 * @return Bukkit particle from Minecraft name (null if not available)
	 */
	@Nullable
	public static Particle parse(String key) {
		key = key.toLowerCase(Locale.ENGLISH).replace(" ", "_");
		if (PARTICLES.containsKey(key)) {
			return PARTICLES.get(key);
		}
		return null;
	}

	@SuppressWarnings("UnstableApiUsage")
	private static String getDataType(Particle particle) {
		Class<?> dataType = particle.getDataType();
		if (dataType == ItemStack.class) {
			return "itemstack/material";
		} else if (dataType == Particle.DustOptions.class) {
			return "dust-option";
		} else if (dataType == BlockData.class) {
			return "blockdata/material";
		} else if (dataType == Particle.DustTransition.class) {
			return "dust-transition";
		} else if (dataType == Vibration.class) {
			return "vibration";
		} else if (dataType == Integer.class) {
			return "number(int)";
		} else if (dataType == Float.class) {
			return "number(float)";
		} else if (dataType == Color.class) {
			return "color";
		} else if (HAS_TRAIL && dataType == Particle.Trail.class) {
			return "trail";
		}
		// For future particle data additions that haven't been added here yet
		Skript.error("Missing particle data type: '&e" + dataType.getName() + "&7'");
		return "UNKNOWN";
	}

	public static void spawnParticle(@NotNull Particle particle, @Nullable Player[] players, @NotNull Location location, int count, Object data, Vector offset, double extra, boolean force) {
		Object particleData = getData(particle, data);
		if (particle.getDataType() != Void.class && particleData == null)
			return;

		double x = offset.getX();
		double y = offset.getY();
		double z = offset.getZ();
		if (players == null) {
			World world = location.getWorld();
			if (world == null) return;
			world.spawnParticle(particle, location, count, x, y, z, extra, particleData, force);
		} else {
			for (Player player : players) {
				assert player != null;
				if (HAS_PLAYER_FORCE) {
					player.spawnParticle(particle, location, count, x, y, z, extra, particleData, force);
				} else {
					player.spawnParticle(particle, location, count, x, y, z, extra, particleData);
				}
			}
		}
	}

	@SuppressWarnings("UnstableApiUsage")
	@Nullable
	private static Object getData(Particle particle, Object data) {
		Class<?> dataType = particle.getDataType();
		if (dataType == Void.class) {
			return null;
		} else if (dataType == Float.class && data instanceof Number number) {
			return number.floatValue();
		} else if (dataType == Integer.class && data instanceof Number number) {
			return number.intValue();
		} else if (dataType == ItemStack.class) {
			if (data instanceof ItemStack itemStack) {
				return itemStack;
			} else if (data instanceof Material material && material.isItem()) {
				return new ItemStack(material);
			}
		} else if (dataType == Particle.DustOptions.class && data instanceof Particle.DustOptions) {
			return data;
		} else if (dataType == Particle.DustTransition.class && data instanceof Particle.DustTransition) {
			return data;
		} else if (dataType == Vibration.class && data instanceof Vibration) {
			return data;
		} else if (dataType == Color.class && data instanceof ch.njol.skript.util.Color skriptColor) {
			return skriptColor.asBukkitColor();
		} else if (dataType == Color.class && data instanceof Color) {
			return data;
		} else if (dataType == BlockData.class) {
			if (data instanceof BlockData) {
				return data;
			} else if (data instanceof Material material && material.isBlock()) {
				return material.createBlockData();
			}
		} else if (HAS_TRAIL && dataType == Particle.Trail.class && data instanceof Particle.Trail) {
			return data;
		}
		return null;
	}

}
