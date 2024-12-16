package ch.njol.skript.classes.data.defaults;

/**
 * Loader for default values for Skript
 * <p>
 * Will load:
 * - {@link BukkitEventValues}
 * - {@link DefaultComparators}
 * - {@link DefaultConverters}
 * - {@link DefaultFunctions}
 * - {@link DefaultOperations}
 */
public class DefaultValues {

	private static boolean INITIALIZED = false;

	private DefaultValues() {
	}

	public static void init() {
		if (INITIALIZED) {
			throw new IllegalStateException("DefaultValues have already been initialized");
		}
		INITIALIZED = true;
		BukkitEventValues.init();
		DefaultComparators.init();
		DefaultConverters.init();
		DefaultFunctions.init();
		DefaultOperations.init();
	}

}
