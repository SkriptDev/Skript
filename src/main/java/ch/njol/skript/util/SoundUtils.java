package ch.njol.skript.util;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public abstract class SoundUtils {

	private static final boolean SOUND_IS_INTERFACE = Sound.class.isInterface();

	@SuppressWarnings({"unchecked", "rawtypes", "deprecation"})
	public static @Nullable NamespacedKey getSoundKeyFromEnum(String soundString) {
		soundString = soundString.toUpperCase(Locale.ENGLISH);
		// Sound.class is an Interface (rather than an enum) as of MC 1.21.3
		if (SOUND_IS_INTERFACE) {
			try {
				Sound sound = Sound.valueOf(soundString);
				return sound.getKey();
			} catch (IllegalArgumentException ignore) {
			}
		} else {
			try {
				Enum soundEnum = Enum.valueOf((Class) Sound.class, soundString);
				if (soundEnum instanceof Keyed keyed) {
					return keyed.getKey();
				}
			} catch (IllegalArgumentException ignore) {
			}
		}
		return null;
	}

}
