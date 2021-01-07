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

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.util.StringUtils;

/**
 * Util class for managing {@link Enchantment Enchantments}
 */
public class EnchantmentUtils {
	
	public static String getKey(Enchantment ench) {
		return ench.getKey().getKey();
	}
	
	@Nullable
	public static Enchantment getByKey(String key) {
		return Enchantment.getByKey(NamespacedKey.minecraft(key));
	}
	
	public static String getNames() {
		List<String> enchants = new ArrayList<>();
		for (Enchantment enchantment : Enchantment.values()) {
			String key = getName(enchantment);
			enchants.add(key);
		}
		return StringUtils.join(enchants, ", ");
	}
	
	public static String getName(Enchantment enchantment) {
		return getKey(enchantment).replace("_", " ");
	}
	
}
