package ch.njol.skript.bukkitutil;

import ch.njol.skript.Skript;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Miscellaneous static utility methods related to items.
 */
public class ItemUtils {

	public static final boolean HAS_MAX_DAMAGE = Skript.methodExists(Damageable.class, "getMaxDamage");
	// Introduced in Paper 1.21
	public static final boolean HAS_RESET = Skript.methodExists(Damageable.class, "resetDamage");
	public static final boolean CAN_CREATE_PLAYER_PROFILE = Skript.methodExists(Bukkit.class, "createPlayerProfile", UUID.class, String.class);

	/**
	 * Gets damage/durability of an item, or 0 if it does not have damage.
	 *
	 * @param itemStack Item.
	 * @return Damage.
	 */
	public static int getDamage(ItemStack itemStack) {
		return getDamage(itemStack.getItemMeta());
	}

	/**
	 * Gets damage/durability of an itemmeta, or 0 if it does not have damage.
	 *
	 * @param itemMeta ItemMeta.
	 * @return Damage.
	 */
	public static int getDamage(ItemMeta itemMeta) {
		if (itemMeta instanceof Damageable)
			return ((Damageable) itemMeta).getDamage();
		return 0; // Non damageable item
	}

	/**
	 * Gets the max damage/durability of an item
	 * <p>NOTE: Will account for custom damageable items in MC 1.20.5+</p>
	 *
	 * @param itemStack Item to grab durability from
	 * @return Max amount of damage this item can take
	 */
	public static int getMaxDamage(ItemStack itemStack) {
		ItemMeta meta = itemStack.getItemMeta();
		if (HAS_MAX_DAMAGE && meta instanceof Damageable && ((Damageable) meta).hasMaxDamage())
			return ((Damageable) meta).getMaxDamage();
		return itemStack.getType().getMaxDurability();
	}

	/**
	 * Set the max damage/durability of an item
	 *
	 * @param itemStack ItemStack to set max damage
	 * @param maxDamage Amount of new max damage
	 */
	public static void setMaxDamage(ItemStack itemStack, int maxDamage) {
		ItemMeta meta = itemStack.getItemMeta();
		if (HAS_MAX_DAMAGE && meta instanceof Damageable damageable) {
			if (HAS_RESET && maxDamage < 1) {
				damageable.resetDamage();
			} else {
				damageable.setMaxDamage(Math.max(1, maxDamage));
			}
			itemStack.setItemMeta(damageable);
		}
	}

	/**
	 * Sets damage/durability of an item if possible.
	 *
	 * @param itemStack Item to modify.
	 * @param damage    New damage. Note that on some Minecraft versions,
	 *                  this might be truncated to short.
	 */
	public static void setDamage(ItemStack itemStack, int damage) {
		ItemMeta meta = itemStack.getItemMeta();
		if (meta instanceof Damageable) {
			((Damageable) meta).setDamage(Math.max(0, damage));
			itemStack.setItemMeta(meta);
		}
	}

	/**
	 * Sets the owner of a player head.
	 *
	 * @param skull  player head item to modify
	 * @param player owner of the head
	 */
	public static void setHeadOwner(ItemStack skull, OfflinePlayer player) {
		ItemMeta meta = skull.getItemMeta();
		if (!(meta instanceof SkullMeta skullMeta))
			return;

		if (player.getName() != null) {
			skullMeta.setOwningPlayer(player);
		} else if (CAN_CREATE_PLAYER_PROFILE) {
			//noinspection deprecation
			skullMeta.setOwnerProfile(Bukkit.createPlayerProfile(player.getUniqueId(), player.getName()));
		} else {
			skullMeta.setOwningPlayer(null);
		}

		skull.setItemMeta(skullMeta);
	}

	/**
	 * Gets a block material corresponding to given item material, which might
	 * be the given material. If no block material is found, null is returned.
	 *
	 * @param type Material.
	 * @return Block version of material or null.
	 */
	@Nullable
	public static Material asBlock(Material type) {
		if (type.isBlock()) {
			return type;
		} else {
			return null;
		}
	}

	// Only 1.15 and versions after have Material#isAir method
	private static final boolean IS_AIR_EXISTS = Skript.methodExists(Material.class, "isAir");

	public static boolean isAir(Material type) {
		if (IS_AIR_EXISTS)
			return type.isAir();
		return type == Material.AIR || type == Material.CAVE_AIR || type == Material.VOID_AIR;
	}

	/**
	 * Whether the block is a fence or a wall.
	 *
	 * @param block the block to check.
	 * @return whether the block is a fence/wall.
	 */
	public static boolean isFence(Block block) {
		Material type = block.getType();
		return Tag.FENCES.isTagged(type)
			|| Tag.FENCE_GATES.isTagged(type)
			|| Tag.WALLS.isTagged(type);
	}

	/**
	 * @param material The material to check
	 * @return whether the material is a full glass block
	 */
	public static boolean isGlass(Material material) {
		return switch (material) {
			case GLASS, RED_STAINED_GLASS, ORANGE_STAINED_GLASS,
				 YELLOW_STAINED_GLASS, LIGHT_BLUE_STAINED_GLASS,
				 BLUE_STAINED_GLASS, CYAN_STAINED_GLASS, LIME_STAINED_GLASS,
				 GREEN_STAINED_GLASS, MAGENTA_STAINED_GLASS,
				 PURPLE_STAINED_GLASS, PINK_STAINED_GLASS, WHITE_STAINED_GLASS,
				 LIGHT_GRAY_STAINED_GLASS, GRAY_STAINED_GLASS,
				 BLACK_STAINED_GLASS, BROWN_STAINED_GLASS -> true;
			default -> false;
		};
	}

}
