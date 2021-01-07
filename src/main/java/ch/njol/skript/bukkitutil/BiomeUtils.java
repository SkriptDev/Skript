/**
 * This file is part of Skript.
 *
 * Skript is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Skript is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.bukkitutil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.block.Biome;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.util.StringUtils;

/**
 * Utility class for managing {@link Biome} enums
 *
 * @author Peter Güttinger
 */
public abstract class BiomeUtils {
	private BiomeUtils() {
	}
	
	@Nullable
	public static Biome parse(final String s) {
		String b = s.toUpperCase(Locale.ROOT).replace(" ", "_");
		try {
			return Biome.valueOf(b);
		} catch (IllegalArgumentException ignore) {
		}
		return null;
	}
	
	public static String toString(final Biome b, final int flags) {
		return b.name().toLowerCase(Locale.ROOT).replace("_", " ");
	}
	
	public static String getAllNames() { // This is hack for class loading order...
		List<String> biomes = new ArrayList<>();
		for (Biome biome : Biome.values()) {
			biomes.add(toString(biome, 0));
		}
		return StringUtils.join(biomes, ", ");
	}
	
}
