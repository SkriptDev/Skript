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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.util.StringUtils;

/**
 * Util class for managing {@link Enchantment Enchantments}
 */
public class EnchantmentUtils {
	
	private static final Map<String,Enchantment> ENCHANT_MAP = new HashMap<>();
	private static final Map<Enchantment,String> KEY_MAP = new HashMap<>();
	private static final Map<Enchantment,String> NAME_MAP = new HashMap<>();
	
	static {
		for (Enchantment enchantment : Enchantment.values()) {
			String key = enchantment.getKey().getKey();
			ENCHANT_MAP.put(key, enchantment);
			KEY_MAP.put(enchantment, key);
			NAME_MAP.put(enchantment, key.replace("_", " "));
		}
	}
	
	public static String getKey(Enchantment ench) {
		return KEY_MAP.get(ench);
	}
	
	@Nullable
	public static Enchantment getByKey(String key) {
		return ENCHANT_MAP.get(key);
	}
	
	public static String getNames() {
		List<String> enchants = new ArrayList<>(NAME_MAP.values());
		Collections.sort(enchants);
		return StringUtils.join(enchants, ", ");
	}
	
	public static String getName(Enchantment enchantment) {
		return NAME_MAP.get(enchantment);
	}
	
}
