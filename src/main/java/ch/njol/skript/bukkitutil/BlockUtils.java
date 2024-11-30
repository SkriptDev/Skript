package ch.njol.skript.bukkitutil;

import ch.njol.skript.bukkitutil.block.BlockCompat;
import ch.njol.skript.bukkitutil.block.BlockSetter;
import ch.njol.skript.bukkitutil.block.BlockValues;
import ch.njol.skript.util.DelayedChangeBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

public class BlockUtils {

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

		String errorData = new String(data);

		try {
			return Bukkit.createBlockData(data.startsWith("minecraft:") ? data : "minecraft:" + data);
		} catch (IllegalArgumentException ignored) {
			return null;
		}
	}

	/**
	 * Get the string version of a block, including type and location.
	 * ex: 'stone' at 1.5, 1.5, 1.5 in world 'world'
	 * World can be null if the Block is Skript's BlockStateBlock.
	 *
	 * @param block Block to get string of
	 * @param flags unknown
	 * @return String version of block
	 */
	@SuppressWarnings("unused")
	public static String blockToString(Block block, int flags) {
		String type = block.getType().getKey().getKey();
		Location location = block.getLocation();

		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();

		World world = location.getWorld();
		if (world == null)
			return String.format("'%s' at %s, %s, %s", type, x, y, z);
		return String.format("'%s' at %s, %s, %s in world '%s'", type, x, y, z, world.getName());
	}

	/**
	 * Extracts the actual CraftBukkit block from the given argument,
	 * by extracting the block from {@link DelayedChangeBlock} if the given argument is a {@link DelayedChangeBlock}.
	 *
	 * @return the actual CB block from the given argument
	 */
	public static Block extractBlock(Block block) {
		return block instanceof DelayedChangeBlock ? ((DelayedChangeBlock) block).getBlock() : block;
	}

}
