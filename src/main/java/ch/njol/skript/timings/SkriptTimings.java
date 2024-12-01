package ch.njol.skript.timings;

import ch.njol.skript.Skript;
import org.jetbrains.annotations.Nullable;

/**
 * Static utils for Skript timings.
 */
public class SkriptTimings {

	private static volatile boolean enabled;
	@SuppressWarnings("null")
	private static Skript skript; // Initialized on Skript load, before any timings would be used anyway

	@Nullable
	public static Object start(String name) {
		// We currently don't have any timings to return
		return null;
	}

	public static void stop(@Nullable Object timing) {
	}

	public static boolean enabled() {
		// We currently don't have any timings to return
		return false;
	}

	public static void setEnabled(boolean flag) {
		enabled = flag;
	}

	public static void setSkript(Skript plugin) {
		skript = plugin;
	}

}
