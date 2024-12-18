package ch.njol.skript.util;

import ch.njol.skript.localization.Language;
import ch.njol.skript.localization.Noun;
import ch.njol.util.NonNullPair;
import ch.njol.util.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * A language utility class to be used for easily handling language values representing an Enum.
 *
 * @param <E> Generic representing the Enum.
 * @see ch.njol.skript.classes.EnumClassInfo
 */
public final class EnumUtils<E extends Enum<E>> {

	private final Class<E> enumClass;
	private final String languageNode;

	private String[] names;
	private final List<String> docNames = new ArrayList<>();
	private final HashMap<String, E> parseMap = new HashMap<>();

	public EnumUtils(Class<E> enumClass, String languageNode) {
		assert enumClass.isEnum() : enumClass;
		assert !languageNode.isEmpty() && !languageNode.endsWith(".") : languageNode;

		this.enumClass = enumClass;
		this.languageNode = languageNode;

		refresh();

		Language.addListener(this::refresh);
	}

	/**
	 * Refreshes the representation of this Enum based on the currently stored language entries.
	 */
	void refresh() {
		E[] constants = enumClass.getEnumConstants();
		names = new String[constants.length];
		parseMap.clear();
		this.docNames.clear();
		for (E constant : constants) {
			String key = languageNode + "." + constant.name();
			int ordinal = constant.ordinal();

			String[] options = Language.getList(key);
			for (String option : options) {
				option = option.toLowerCase(Locale.ENGLISH);
				if (options.length == 1 && option.equals(key.toLowerCase(Locale.ENGLISH))) {
					// Use enum name when lang entry is missing
					String tempName = constant.name().toLowerCase(Locale.ENGLISH).replace("_", " ");
					names[ordinal] = tempName;
					parseMap.put(tempName, constant);
					this.docNames.add(tempName);

					// Create articles for entries
					String start = switch (tempName.charAt(0)) {
						case 'a', 'e', 'i', 'o', 'u' -> "an";
						default -> "a";
					};
					parseMap.put(start + " " + tempName, constant);
					continue;
				}

				// Isolate the gender if one is present
				NonNullPair<String, Integer> strippedOption = Noun.stripGender(option, key);
				String first = strippedOption.getFirst();
				Integer second = strippedOption.getSecond();

				if (names[ordinal] == null) { // Add to name array if needed
					names[ordinal] = first;
				}

				parseMap.put(first, constant);
				this.docNames.add(first);
				if (second != null && second != -1) { // There is a gender present
					String s = Noun.getArticleWithSpace(second, Language.F_INDEFINITE_ARTICLE) + first;
					parseMap.put(s, constant);
					this.docNames.add(s);
				}
			}
		}
	}

	/**
	 * This method attempts to match the string input against one of the string representations of the enumerators.
	 *
	 * @param input a string to attempt to match against one the enumerators.
	 * @return The enumerator matching the input, or null if no match could be made.
	 */
	@Nullable
	public E parse(String input) {
		return parseMap.get(input.toLowerCase(Locale.ENGLISH));
	}

	/**
	 * This method returns the string representation of an enumerator.
	 *
	 * @param enumerator The enumerator to represent as a string.
	 * @param flags      not currently used
	 * @return A string representation of the enumerator.
	 */
	@SuppressWarnings("unused")
	public String toString(E enumerator, int flags) {
		String s = names[enumerator.ordinal()];
		return s != null ? s : enumerator.name();
	}

	/**
	 * @return A comma-separated string containing a list of all names representing the enumerators.
	 * Note that some entries may represent the same enumerator.
	 */
	public String getAllNames() {
		return StringUtils.join(this.docNames.stream().sorted().toList(), ", ");
	}

}
