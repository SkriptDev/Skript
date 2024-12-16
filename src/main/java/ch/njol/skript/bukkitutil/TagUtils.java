package ch.njol.skript.bukkitutil;

import ch.njol.skript.Skript;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class relating to {@link Tag}
 */
@SuppressWarnings("UnstableApiUsage")
public class TagUtils {

	// Tag classes were added in Paper 1.21
	public static boolean HAS_TAG = Skript.classExists("io.papermc.paper.registry.tag.Tag");

	/**
	 * Get a tag that was serialized
	 *
	 * @param registryKeyString Name of registry this tag is represented in
	 * @param tagKeyString      Key of tag
	 * @param <T>               Class of registry/tag
	 * @return Tag from serialization
	 */
	@SuppressWarnings({"UnstableApiUsage", "unchecked", "NullableProblems"})
	@Nullable
	public static <T extends Keyed> Tag<T> getSerializedTag(String registryKeyString, String tagKeyString) {
		RegistryKey<T> registryKey = (RegistryKey<T>) RegistryUtils.getRegistryKey(registryKeyString);
		if (registryKey == null) return null;

		return getTag(registryKey, tagKeyString);
	}

	@SuppressWarnings("NullableProblems")
	@Nullable
	public static <T extends Keyed> Tag<T> getTag(RegistryKey<T> registryKey, String tagKeyString) {
		Registry<T> registry = RegistryAccess.registryAccess().getRegistry(registryKey);
		Key key = getKey(tagKeyString);
		if (key == null) return null;

		TagKey<T> tagKey = TagKey.create(registryKey, key);
		if (!registry.hasTag(tagKey)) return null;
		return registry.getTag(tagKey);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static boolean isTagged(Keyed keyed, Tag tag) {
		RegistryKey registryKey = tag.registryKey();

		TypedKey typedKey = TypedKey.create(registryKey, keyed.key());
		return tag.contains(typedKey);
	}

	/**
	 * Get the values of a tag
	 * <p>
	 * Some tags may return types we aren't currently using.
	 * Ex: ItemType/BlockType, so instead we return Material
	 *
	 * @param tag Tag to get values from
	 * @return List of values from tag
	 */
	@SuppressWarnings("deprecation")
	public static List<Keyed> getTagValues(Tag<?> tag) {
		List<Keyed> values = new ArrayList<>();
		Registry<?> registry = RegistryAccess.registryAccess().getRegistry(tag.registryKey());
		for (TypedKey<?> typedKey : tag.values()) {
			Keyed object = registry.get(typedKey);
			if (object instanceof BlockType blockType) {
				values.add(blockType.asMaterial());
			} else if (object instanceof ItemType itemType) {
				values.add(itemType.asMaterial());
			} else {
				values.add(object);
			}
		}
		return values;
	}

	@SuppressWarnings("PatternValidation")
	private static Key getKey(String stringKey) {
		try {
			return Key.key(stringKey);
		} catch (InvalidKeyException ex) {
			Skript.debug("Invalid key: " + ex.getMessage());
			return null;
		}
	}

}
