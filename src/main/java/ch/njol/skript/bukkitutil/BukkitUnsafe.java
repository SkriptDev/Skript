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

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.UnsafeValues;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Contains helpers for Bukkit's not so safe stuff.
 */
@SuppressWarnings("deprecation")
public class BukkitUnsafe {
	
	/**
	 * Bukkit's UnsafeValues allows us to do stuff that would otherwise
	 * require NMS.
	 *
	 * UnsafeValues' existence and behavior is not guaranteed across future versions.
	 */
	private static final UnsafeValues unsafe = Bukkit.getUnsafe();
	
	/**
	 * Vanilla material names to Bukkit materials.
	 */
	private static final Map<String, Material> materialMap = new HashMap<>();
	
	public static void initialize() {
		if (materialMap.isEmpty()) {
			
			for (Material material : Material.values()) {
				String key = material.getKey().toString();
				materialMap.put(key, material);
			}
		}
	}
	
	@Nullable
	public static Material getMaterialFromMinecraftId(String id) {
		if (materialMap.isEmpty()) {
			initialize();
		}
		
		if (materialMap.containsKey(id)) {
			return materialMap.get(id);
		}
		return null;
	}
	
	public static void modifyItemStack(ItemStack stack, String arguments) {
		unsafe.modifyItemStack(stack, arguments);
	}
	
}
