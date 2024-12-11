package ch.njol.skript.bukkitutil;

import ch.njol.skript.Skript;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;

/**
 * Utilily methods to handle {@link io.papermc.paper.datacomponent.DataComponentType DataComponents}
 */
@SuppressWarnings("UnstableApiUsage")
public class ItemComponentUtils {



	public static boolean isConsumable(final ItemStack item) {
		return item.hasData(DataComponentTypes.CONSUMABLE);
	}

}
