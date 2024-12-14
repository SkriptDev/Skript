package ch.njol.skript.bukkitutil;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import org.bukkit.inventory.ItemStack;

/**
 * Utilily methods to handle {@link io.papermc.paper.datacomponent.DataComponentType DataComponents}
 */
@SuppressWarnings("UnstableApiUsage")
public class ItemComponentUtils {

	/**
	 * Check if an ItemStack has a consumable component
	 *
	 * @param item ItemStack to check
	 * @return True if item has consumable component
	 */
	public static boolean isConsumable(final ItemStack item) {
		return item.hasData(DataComponentTypes.CONSUMABLE);
	}

	/**
	 * Check if an ItemStack is resistant to fire
	 * <p>
	 * This does not check enchantments, only data component for damage resistance
	 *
	 * @param item ItemStack to check
	 * @return True if fire-resistant
	 */
	public static boolean isFireResistant(final ItemStack item) {
		if (item.hasData(DataComponentTypes.DAMAGE_RESISTANT)) {
			DamageResistant data = item.getData(DataComponentTypes.DAMAGE_RESISTANT);
			return data != null && data.types().equals(DamageTypeTagKeys.IS_FIRE);
		}
		return false;
	}

	/**
	 * Set whether an ItemStack is fire-resistant
	 * <p>
	 * This can override vanilla resistance
	 *
	 * @param item      ItemStack to modify
	 * @param resistant Whether fire-resistant or not
	 */
	public static void setFireResistant(final ItemStack item, boolean resistant) {
		if (resistant) {
			item.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(DamageTypeTagKeys.IS_FIRE));
		} else {
			item.unsetData(DataComponentTypes.DAMAGE_RESISTANT);
		}
	}

}
