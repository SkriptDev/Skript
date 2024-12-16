package ch.njol.skript.bukkitutil;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.StringUtils;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Villager;
import org.bukkit.generator.structure.Structure;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Utility class relating to {@link org.bukkit.Registry} and {@link RegistryKey}
 */
@SuppressWarnings({"UnstableApiUsage", "rawtypes"})
public class RegistryUtils {

	// REGISTRY
	private static final Map<String, RegistryKey> REGISTRY_KEYS = new HashMap<>();
	private static final Map<RegistryKey<?>, String> REGISTRY_KEYS_NAMES = new HashMap<>();
	private static final Map<String, RegistryKey<?>> REGISTRY_KEYS_BY_NAME = new HashMap<>();
	private static final Map<RegistryKey<?>, Class<?>> REGISTRY_KEYS_CLASSES = new HashMap<>();

	static {
		// These are the registries that Skript currently supports
		registerRegistryKey(RegistryKey.ATTRIBUTE, Attribute.class);
		registerRegistryKey(RegistryKey.BIOME, Biome.class);
		registerRegistryKey(RegistryKey.BLOCK, Material.class);
		registerRegistryKey(RegistryKey.CAT_VARIANT, Cat.Type.class);
		registerRegistryKey(RegistryKey.DAMAGE_TYPE, DamageType.class);
		registerRegistryKey(RegistryKey.ENCHANTMENT, Enchantment.class);
		registerRegistryKey(RegistryKey.ENTITY_TYPE, EntityType.class);
		registerRegistryKey(RegistryKey.FROG_VARIANT, Frog.Variant.class);
		registerRegistryKey(RegistryKey.ITEM, Material.class);
		registerRegistryKey(RegistryKey.MOB_EFFECT, PotionEffectType.class);
		registerRegistryKey(RegistryKey.PARTICLE_TYPE, Particle.class);
		registerRegistryKey(RegistryKey.STRUCTURE, Structure.class);
		registerRegistryKey(RegistryKey.VILLAGER_PROFESSION, Villager.Profession.class);
		registerRegistryKey(RegistryKey.VILLAGER_TYPE, Villager.Type.class);
	}

	@SuppressWarnings("unused")
	private static void registerRegistryKey(final RegistryKey<?> registryKey, Class<? extends Keyed> registryClass) {
		String key = registryKey.key().toString();
		REGISTRY_KEYS.put(key, registryKey);
		REGISTRY_KEYS_CLASSES.put(registryKey, registryClass);
		key = key.substring(key.lastIndexOf('/') + 1) + " registry";
		key = key.replace("minecraft:", "").replace("_", " ");
		REGISTRY_KEYS_NAMES.put(registryKey, key);
		REGISTRY_KEYS_BY_NAME.put(key, registryKey);
	}

	@Nullable
	public static RegistryKey<?> getRegistryKey(String key) {
		return REGISTRY_KEYS.get(key);
	}

	@Nullable
	public static RegistryKey<?> getRegistryKeyByName(String name) {
		return REGISTRY_KEYS_BY_NAME.get(name);
	}

	public static Class<?> getRegistryClass(RegistryKey<?> key) {
		return REGISTRY_KEYS_CLASSES.get(key);
	}

	public static String toString(RegistryKey<?> registryKey) {
		return REGISTRY_KEYS_NAMES.get(registryKey);
	}

	/**
	 * Get a String of all registy keys for docs
	 *
	 * @return Separated string of names for docs
	 */
	public static String getAllNames() {
		List<String> names = new ArrayList<>();
		REGISTRY_KEYS_NAMES.forEach((registryKey, s) -> {
			Class<?> aClass = REGISTRY_KEYS_CLASSES.get(registryKey);
			ClassInfo<?> classInfo = Classes.getExactClassInfo(aClass);
			String className = classInfo != null ? "<a href='#" + classInfo.getCodeName() + "'>" + classInfo.getDocName() + "</a>" : "unsupported";
			names.add(s + " [" + className + "]");
		});
		Collections.sort(names);
		return StringUtils.join(names, "<br>");
	}

	/**
	 * Get a supplier for {@link ClassInfo} registration
	 *
	 * @return Supplier for class info
	 */
	public static Supplier<Iterator<RegistryKey>> getSupplier() {
		return () -> REGISTRY_KEYS.values().iterator();
	}

}
