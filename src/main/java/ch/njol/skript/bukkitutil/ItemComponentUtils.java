package ch.njol.skript.bukkitutil;

import ch.njol.skript.Skript;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;

/**
 * Utilily methods to handle {@link io.papermc.paper.datacomponent.DataComponentType DataComponents}
 */
@SuppressWarnings("UnstableApiUsage")
public class ItemComponentUtils {

	public static final boolean HAS_CONSUMABLE;
	public static final boolean HAS_FOOD;

	static {
		HAS_CONSUMABLE = Skript.classExists("io.papermc.paper.datacomponent.DataComponentTypes");
		boolean food = Skript.classExists("org.bukkit.inventory.meta.components.FoodComponent");
		HAS_FOOD = food && !HAS_CONSUMABLE;
	}

	public static boolean isConsumable(final ItemStack item) {
		return item.hasData(DataComponentTypes.CONSUMABLE);
	}

}
