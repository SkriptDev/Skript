package ch.njol.skript.classes.registry;

import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.util.StringUtils;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A parser based on a {@link Registry} used to parse data from a string or turn data into a string.
 *
 * @param <R> Registry class
 */
public class RegistryParser<R extends Keyed> extends Parser<R> {

	private final Registry<R> registry;

	private final Map<R, String> names = new HashMap<>();
	private final Map<String, R> parseMap = new HashMap<>();

	public RegistryParser(Registry<R> registry) {
		this.registry = registry;
		refresh();
	}

	private void refresh() {
		names.clear();
		parseMap.clear();
		for (R registryObject : registry) {
			NamespacedKey namespacedKey = registryObject.getKey();
			String namespace = namespacedKey.getNamespace();
			String key = namespacedKey.getKey();
			String keyWithSpaces = key.replace("_", " ");

			String namespacedKeyString = namespacedKey.toString();
			// Put the full namespaced key as a pattern
			parseMap.put(namespacedKeyString, registryObject);

			// If the object is a vanilla Minecraft object, we'll add the key with spaces as a pattern
			if (namespace.equalsIgnoreCase(NamespacedKey.MINECRAFT)) {
				parseMap.put(keyWithSpaces, registryObject);
				parseMap.put(key, registryObject);
				names.put(registryObject, keyWithSpaces);
			} else {
				names.put(registryObject, namespacedKeyString);
			}
		}
	}

	/**
	 * This method attempts to match the string input against one of the string representations of the registry.
	 *
	 * @param input   a string to attempt to match against one in the registry.
	 * @param context of parsing, may not be null
	 * @return The registry object matching the input, or null if no match could be made.
	 */
	@Override
	public @Nullable R parse(String input, @NotNull ParseContext context) {
		return parseMap.get(input.toLowerCase(Locale.ENGLISH));
	}

	/**
	 * This method returns the string representation of a registry.
	 *
	 * @param object The object to represent as a string.
	 * @param flags  not currently used
	 * @return A string representation of the registry object.
	 */
	@Override
	public @NotNull String toString(R object, int flags) {
		return names.get(object);
	}

	/**
	 * Returns a registry object's string representation in a variable name.
	 *
	 * @param object Object to represent in a variable name.
	 * @return The given object's representation in a variable name.
	 */
	@Override
	public @NotNull String toVariableNameString(R object) {
		return toString(object, 0);
	}

	/**
	 * @return A comma-separated string containing a list of all names representing the registry.
	 * Note that some entries may represent the same registry object.
	 */
	public String getAllNames() {
		List<String> strings = parseMap.keySet().stream().filter(s -> !s.startsWith("minecraft:")).sorted().collect(Collectors.toList());
		return StringUtils.join(strings, ", ");
	}

}
