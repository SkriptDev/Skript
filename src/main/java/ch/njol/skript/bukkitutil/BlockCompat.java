/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.bukkitutil;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.MatchQuality;

/**
 * 1.13+ block compat.
 */
public class BlockCompat {
	
	/**
	 * Instance of BlockCompat for current Minecraft version.
	 */
	public static final BlockCompat INSTANCE = new BlockCompat();
	
	public static final BlockSetter SETTER = INSTANCE.getSetter();
	
	private BlockCompat() {}
	
	public static class BlockValues {

		Material type;
		BlockData data;
		boolean isDefault;
		
		public BlockValues(Material type, BlockData data, boolean isDefault) {
			this.type = type;
			this.data = data;
			this.isDefault = isDefault;
		}
		
		public boolean isDefault() {
			return isDefault;
		}

		@Override
		public boolean equals(@Nullable Object other) {
			if (!(other instanceof BlockValues))
				return false;
			BlockValues n = (BlockValues) other;
			return (data.matches(n.data) || n.data.matches(data)) && type.equals(n.type);
		}

		@SuppressWarnings("null")
		@Override
		public int hashCode() {
			int prime = 31;
			int result = 1;
			result = prime * result + (data == null ? 0 : data.hashCode());
			result = prime * result + type.hashCode();
			return result;
		}
		
		@Override
		public String toString() {
			return data.toString() + (isDefault ? " (default)" : "");
		}
		
		public MatchQuality match(BlockValues other) {
			if (type == other.type) {
				if (data.equals(other.data)) { // Check for exact item match
					return MatchQuality.EXACT;
				} else if (data.matches(other.data)) { // What about explicitly defined states only?
					return MatchQuality.SAME_ITEM;
				} else { // Just same material and different block states
					return MatchQuality.SAME_MATERIAL;
				}
			} else {
				return MatchQuality.DIFFERENT;
			}
		}
		
	}
	
	public static class BlockSetter {
		
		/**
		 * Attempts to automatically correct rotation and direction of the block
		 * when setting it. Note that this will NOT overwrite any existing data
		 * supplied in block values.
		 */
		public static int ROTATE = 1;
		
		/**
		 * Overrides rotation and direction that might have been specified in block
		 * values when {@link #ROTATE} is also set.
		 */
		public static int ROTATE_FORCE = 1 << 1;
		
		/**
		 * Changes type of the block if that is needed to get the correct rotation.
		 */
		public static int ROTATE_FIX_TYPE = 1 << 2;
		
		/**
		 * Takes rotation or direction of the block (depending on the block)
		 * and attempts to place other parts of it according to those. For example,
		 * placing beds and doors should be simple enough with this flag.
		 */
		public static int MULTIPART = 1 << 3;
		
		/**
		 * When placing the block, apply physics.
		 */
		public static int APPLY_PHYSICS = 1 << 4;
		
		private boolean typesLoaded = false;

		/**
		 * Cached BlockFace values.
		 */
		private final BlockFace[] faces = BlockFace.values();
		
		@SuppressWarnings("null") // Late initialization with loadTypes() to avoid circular dependencies
		private BlockSetter() {}
		
		public void setBlock(Block block, Material type, @Nullable BlockValues values, int flags) {
			
			if (!typesLoaded)
				loadTypes();
			
			boolean rotate = (flags | ROTATE) != 0;
			boolean rotateForce = (flags | ROTATE_FORCE) != 0;
			boolean rotateFixType = (flags | ROTATE_FIX_TYPE) != 0;
			boolean multipart = (flags | MULTIPART) != 0;
			boolean applyPhysics = (flags | APPLY_PHYSICS) != 0;
			BlockValues ourValues = null;
			if (values != null)
				ourValues = values;
			
			Class<?> dataType = type.data;
			
			/**
			 * Set to true when block is placed. If no special logic places
			 * the block, generic placement will be done.
			 */
			boolean placed = false;
			if (rotate) {
				if (type == Material.TORCH || (rotateFixType && type == Material.WALL_TORCH)) {
					// If floor torch cannot be placed, try a wall torch
					Block under = block.getRelative(0, -1, 0);
					boolean canPlace = true;
					if (!under.getType().isOccluding()) { // Usually cannot be placed, but there are exceptions
						// TODO check for stairs and slabs, currently complicated since there is no 'any' alias
						if (isSpecialTorchFloor(under.getType())) {
							canPlace = true;
						} else {
							canPlace = false;
						}
					}
					
					// Can't really place a floor torch, try wall one instead
					if (!canPlace) {
						BlockFace face = findWallTorchSide(block);
						if (face != null) { // Found better torch spot
							block.setType(Material.TORCH);
							Directional data = (Directional) block.getBlockData();
							data.setFacing(face);
							block.setBlockData(data, applyPhysics);
							placed = true;
						}
					}
				} else if (type == Material.WALL_TORCH) {
					Directional data;
					if (ourValues != null)
						data = (Directional) ourValues.data;
					else
						data = (Directional) Bukkit.createBlockData(type);
					
					Block relative = block.getRelative(data.getFacing());
					if ((!relative.getType().isOccluding() && !isSpecialTorchSide(relative.getType())) || rotateForce) {
						// Attempt to figure out a better rotation
						BlockFace face = findWallTorchSide(block);
						if (face != null) { // Found better torch spot
							block.setType(type);
							data.setFacing(face);
							block.setBlockData(data, applyPhysics);
							placed = true;
						}
					}
				}
			}
			
			if (multipart) {
				// Beds
				if (Bed.class.isAssignableFrom(dataType)) {
					Bed data;
					if (ourValues != null)
						data = (Bed) ourValues.data.clone();
					else
						data = (Bed) Bukkit.createBlockData(type);
					
					// Place this bed
					block.setType(type, false);
					block.setBlockData(data, applyPhysics);
					
					// Calculate rotation and location of other part
					BlockFace facing = data.getFacing();
					BlockFace otherFacing = facing;
					Bed.Part otherPart = Bed.Part.HEAD;
					if (data.getPart().equals(Bed.Part.HEAD)) {
						facing = facing.getOppositeFace();
						otherPart = Bed.Part.FOOT;
					}
					
					// Place the other part
					Block other = block.getRelative(facing);
					other.setType(type, false);
					
					data.setPart(otherPart);
					data.setFacing(otherFacing);
					other.setBlockData(data, applyPhysics);
					
					placed = true;
				}
				
				// Top-down bisected blocks (doors etc.)
				if (Bisected.class.isAssignableFrom(dataType) && !Tag.STAIRS.isTagged(type) && !Tag.TRAPDOORS.isTagged(type)) {
					Bisected data;
					if (ourValues != null)
						data = (Bisected) ourValues.data.clone();
					else
						data = (Bisected) Bukkit.createBlockData(type);
					
					// Place this part
					block.setType(type, false);
					block.setBlockData(data, applyPhysics);
					
					// Figure out place of other part
					BlockFace facing = BlockFace.DOWN;
					Bisected.Half otherHalf = Bisected.Half.BOTTOM;
					if (data.getHalf().equals(Bisected.Half.BOTTOM)) {
						facing = BlockFace.UP;
						otherHalf = Bisected.Half.TOP;
					}
					
					// Place the other block
					Block other = block.getRelative(facing);
					other.setType(type, false);
					
					data.setHalf(otherHalf);
					other.setBlockData(data, applyPhysics);
					
					placed = true;
				}
			}
			
			// Generic block placement
			if (!placed) {
				block.setType(type);
				if (ourValues != null && !ourValues.isDefault())
					block.setBlockData(ourValues.data, applyPhysics);
			}
		}
		
		private void loadTypes() {
			
			typesLoaded = true;
		}

		@Nullable
		private BlockFace findWallTorchSide(Block block) {
			for (BlockFace face : faces) {
				assert face != null;
				Block relative = block.getRelative(face);
				if (relative.getType().isOccluding() || isSpecialTorchSide(relative.getType()))
					return face.getOppositeFace(); // Torch can be rotated towards from this face
			}
			
			return null; // Can't place torch here legally
		}
		
		public void sendBlockChange(Player player, Location location, Material type, @Nullable BlockValues values) {
			BlockData blockData = values != null ? ((BlockValues) values).data : type.createBlockData();
			player.sendBlockChange(location, blockData);
		}
		
	}
	
	private final BlockSetter setter = new BlockSetter();
	
	@Nullable
	public BlockValues getBlockValues(BlockState block) {
		// If block doesn't have useful data, data field of type is MaterialData
		if (block.getType().isBlock())
			return new BlockValues(block.getType(), block.getBlockData(), false);
		return null;
	}
	
	@Nullable
	public BlockValues getBlockValues(ItemStack stack) {
		Material type = stack.getType();
		if (type.isBlock()) { // Block has data
			// Create default block data for the type
			return new BlockValues(type, Bukkit.createBlockData(type), true);
		}
		return null;
	}
	
	public BlockSetter getSetter() {
		return setter;
	}
	
	public BlockState fallingBlockToState(FallingBlock entity) {
		BlockState state = entity.getWorld().getBlockAt(0, 0, 0).getState();
		state.setBlockData(entity.getBlockData());
		return state;
	}
	
	@Nullable
	public BlockValues createBlockValues(Material type, Map<String, String> states, @Nullable ItemStack item, int itemFlags) {
		// Ignore item; on 1.13+ block data never applies to items
		if (states.isEmpty()) {
			if (type.isBlock()) { // Still need default block values
				BlockData data =  Bukkit.createBlockData(type, "[]");
				return new BlockValues(type, data, true);
			} else { // Items cannot have block data
				return null;
			}
		}
		
		StringBuilder combined = new StringBuilder("[");
		boolean first = true;
		for (Map.Entry<String, String> entry : states.entrySet()) {
			if (first)
				first = false;
			else
				combined.append(',');
			combined.append(entry.getKey()).append('=').append(entry.getValue());
		}
		combined.append(']');
		
		try {
			BlockData data =  Bukkit.createBlockData(type, combined.toString());
			return new BlockValues(type, data, false);
		} catch (IllegalArgumentException e) {
			Skript.error("Parsing block state " + combined + " failed!");
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean isEmpty(Material type) {
		return type == Material.AIR || type == Material.CAVE_AIR || type == Material.VOID_AIR;
	}
	
	public boolean isLiquid(Material type) {
		return type == Material.WATER || type == Material.LAVA;
	}
	
	private static boolean isSpecialTorchSide(Material material) {
		return material == Material.SOUL_SAND || material == Material.SPAWNER;
	}
	
	private static boolean isSpecialTorchFloor(Material material) {
		if (isSpecialTorchSide(material)) {
			return true;
		}
		String mat = material.toString();
		if (mat.contains("FENCE") || material == Material.SNOW || material == Material.HOPPER) {
			return true;
		}
		return mat.contains("STAINED_GLASS") && !mat.contains("PANE");
	}
	
}
