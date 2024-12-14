package ch.njol.skript.classes.data.bukkit;

/**
 * Loader for {@link ch.njol.skript.classes.ClassInfo ClassInfos} relating to {@link org.bukkit.Bukkit}
 */
public class BukkitClasses {

	private static boolean INITIALIZED = false;

	private BukkitClasses() {
	}

	public static void init() {
		if (INITIALIZED) {
			throw new IllegalStateException("BukkitClasses have already been initialized");
		}
		INITIALIZED = true;
		BlockClasses.init();
		EntityClasses.init();
		EventClasses.init();
		InventoryClasses.init();
		ServerClasses.init();
		WorldClasses.init();

		// Paper
		RegistryClasses.init();
	}

}
