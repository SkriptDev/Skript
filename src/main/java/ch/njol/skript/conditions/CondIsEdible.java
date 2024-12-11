package ch.njol.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.ItemComponentUtils;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Keywords;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Name("Is Edible")
@Description({"Checks whether a Material/ItemStack is edible/consumable.",
	"If running MC 1.20.6-1.21.1: Will check if an ItemStack has a food component.",
	"If running MC 1.21.3+: Will check if an ItemStack has a consumable component."})
@Examples({"cooked beef is edible", "player's tool is edible"})
@Since("2.2-dev36")
@Keywords("consumable")
public class CondIsEdible extends PropertyCondition<Object> {

	public static final boolean HAS_CONSUMABLE;
	public static final boolean HAS_FOOD;

	static {
		HAS_CONSUMABLE = Skript.classExists("io.papermc.paper.datacomponent.DataComponentTypes");
		boolean food = Skript.classExists("org.bukkit.inventory.meta.components.FoodComponent");
		HAS_FOOD = food && !HAS_CONSUMABLE;
	}

	static {
		PropertyCondition.register(CondIsEdible.class, "(edible|consumable)", "materials/itemstacks");
	}

	@Override
	public boolean check(Object object) {
		if (object instanceof Material material) {
			return material.isEdible();
		} else if (object instanceof ItemStack itemstack) {
			if (HAS_CONSUMABLE) {
				return ItemComponentUtils.isConsumable(itemstack);
			} else if (HAS_FOOD) {
				// Saddly this only checks if a custom food component was added
				// An apple for example doesn't have a custom one, and will fall through
				if (itemstack.getItemMeta().hasFood()) return true;
			}
			return itemstack.getType().isEdible();
		}
		return false;
	}

	@Override
	protected String getPropertyName() {
		return "edible";
	}

}
