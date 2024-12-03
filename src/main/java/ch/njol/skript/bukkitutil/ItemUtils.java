package ch.njol.skript.bukkitutil;

import ch.njol.skript.Skript;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	 * Return an ItemStack with a stack size no higher than 99
	 * <br>
	 * Minecraft will not serialize an ItemStack with a size > 99
	 *
	 * @param itemStack ItemStack to check
	 * @return ItemStack with size not greater than 99
	 */
	public static ItemStack clampedStack(ItemStack itemStack) {
		if (itemStack.getAmount() > 99) {
			return itemStack.asQuantity(99);
		}
		return itemStack;
	}

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
	 * Get the max stack size of an ItemStack
	 *
	 * @param itemStack ItemStack to check
	 * @return Max stack size of ItemStack
	 */
	public static int getMaxStackSize(ItemStack itemStack) {
		return itemStack.getItemMeta().hasMaxStackSize() ? itemStack.getMaxStackSize() : itemStack.getType().getMaxStackSize();
	}

	/**
	 * Set the max stack size of an ItemStack
	 *
	 * @param itemStack    ItemStack to change max stack size
	 * @param maxStackSize Max stack size clamped between 1 and 99
	 */
	public static void setMaxStackSize(ItemStack itemStack, int maxStackSize) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setMaxStackSize(Math.clamp(maxStackSize, 1, 99));
		itemStack.setItemMeta(itemMeta);
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
	 * Remove an ItemStack from a list of ItemStacks or an Inventory
	 *
	 * @param toRemove ItemStack to remove
	 * @param from     List/Inventory to remove from
	 */
	public static void removeItemFromList(ItemStack toRemove, Iterable<ItemStack> from) {
		toRemove = toRemove.clone();

		for (ItemStack itemStack : from) {
			if (itemStack == null || itemStack.isEmpty() || !itemStack.isSimilar(toRemove))
				continue;

			int itemStackAmount = itemStack.getAmount();
			int toRemoveAmount = toRemove.getAmount();
			if (itemStackAmount < toRemoveAmount) {
				itemStack.setAmount(0);
				toRemove.setAmount(toRemoveAmount - itemStackAmount);
			} else if (itemStackAmount > toRemoveAmount) {
				itemStack.setAmount(itemStackAmount - toRemoveAmount);
				toRemove.setAmount(0);
				return;
			} else {
				itemStack.setAmount(0);
				toRemove.setAmount(0);
				return;
			}
		}
	}

	/**
	 * Add an ItemStack to a List/Inventory
	 * <br>This will break up the ItemStack if it's too large
	 *
	 * @param toAdd ItemStack to add
	 * @param to    List/Inventory to add to
	 * @return List of ItemStacks that didn't fit (only when adding to an Inventory)
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static List<ItemStack> addItemToList(ItemStack toAdd, Iterable<ItemStack> to) {
		toAdd = toAdd.clone();
		int maxStackSize = getMaxStackSize(toAdd);

		List<ItemStack> toAddList = new ArrayList<>();
		while (toAdd.getAmount() > maxStackSize) {
			toAddList.add(toAdd.asQuantity(maxStackSize));
			toAdd.subtract(maxStackSize);
		}
		toAddList.add(toAdd);

		if (to instanceof Inventory inventory) {
			HashMap<Integer, ItemStack> integerItemStackHashMap = inventory.addItem(toAddList.toArray(new ItemStack[0]));
			return new ArrayList<>(integerItemStackHashMap.values());
		} else if (to instanceof List<ItemStack> list) {
			list.addAll(toAddList);
		}
		return null;
	}

	/**
	 * Add a List/Inventory to another List/Inventory
	 *
	 * @param from List/Inventory to add
	 * @param to   List/Inventory to add to
	 * @return List of ItemStacks that didn't fit (only when adding to an Inventory)
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static List<ItemStack> addListToList(Iterable<ItemStack> from, Iterable<ItemStack> to) {
		List<ItemStack> cloneList = new ArrayList<>();
		for (ItemStack itemStack : from) {
			if (itemStack == null || itemStack.isEmpty()) continue;
			cloneList.add(itemStack);
		}

		if (to instanceof Inventory inventory) {
			List<ItemStack> returned = new ArrayList<>();
			for (ItemStack itemStack : cloneList) {
				returned.addAll(inventory.addItem(itemStack).values());
			}
			return returned;
		} else if (to instanceof List<ItemStack> list) {
			list.addAll(cloneList);
		}
		return null;
	}

}
