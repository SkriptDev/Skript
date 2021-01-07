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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.aliases.ItemData;
import ch.njol.skript.bukkitutil.block.BlockCompat;
import ch.njol.skript.bukkitutil.block.BlockSetter;
import ch.njol.skript.bukkitutil.block.BlockValues;

/**
 * Util class to manage {@link Block Blocks}
 *
 * @author Peter Güttinger
 */
public abstract class BlockUtils {
	
	/**
	 * Sets the given block.
	 *
	 * @param block        Block to set.
	 * @param type         New type of the block.
	 * @param blockValues  Block values to apply after setting the type.
	 * @param applyPhysics Whether physics should be applied or not.
	 * @return Whether setting block succeeded or not (currently always true).
	 */
	public static boolean set(Block block, Material type, @Nullable BlockValues blockValues, boolean applyPhysics) {
		int flags = BlockSetter.ROTATE | BlockSetter.ROTATE_FIX_TYPE | BlockSetter.MULTIPART;
		if (applyPhysics)
			flags |= BlockSetter.APPLY_PHYSICS;
		BlockCompat.SETTER.setBlock(block, type, blockValues, flags);
		return true;
	}
	
	public static boolean set(Block block, ItemData type, boolean applyPhysics) {
		return set(block, type.getType(), type.getBlockValues(), applyPhysics);
	}
	
	public static void sendBlockChange(Player player, Location location, Material type, @Nullable BlockValues blockValues) {
		BlockCompat.SETTER.sendBlockChange(player, location, type, blockValues);
	}
	
	/**
	 * Get BlockData as a string
	 * <p>Commas will be replaced with semicolons.</p>
	 *
	 * @param blockData BlockData to stringify
	 * @return BlockData as string
	 */
	public static String getAsString(BlockData blockData) {
		String data = blockData.getAsString(true);
		data = data.replace("minecraft:", "");
		data = data.replace(",", ";");
		return data;
	}
	
	@Nullable
	public static BlockData createBlockData(String dataString) {
		// Skript uses a comma to separate lists, so we use a semi colon as a delimiter
		// Here we are just replacing it back to a comma to create a new block data
		String data = dataString.replace(";", ",");
		// Remove white space within square brackets ([ lit = false] -> [lit=false])
		data = data.replaceAll(" (?=[^\\[]*])", "");
		// Remove white space between last word and square bracket
		data = data.replaceAll("\\s+\\[", "[");
		// And replace white space between namespace with underscores
		data = data.replace(" ", "_");
		
		try {
			return Bukkit.createBlockData(data.startsWith("minecraft:") ? data : "minecraft:" + data);
		} catch (IllegalArgumentException ignore) {
			return null;
		}
	}
	
}
