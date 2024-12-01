package ch.njol.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Keywords;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import io.papermc.paper.datacomponent.DataComponentTypes;
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

	private static final boolean HAS_CONSUMABLE;
	private static final boolean HAS_FOOD;

	static {
		HAS_CONSUMABLE = Skript.classExists("io.papermc.paper.datacomponent.item.Consumable");
		boolean food = Skript.classExists("org.bukkit.inventory.meta.components.FoodComponent");
		HAS_FOOD = food && !HAS_CONSUMABLE;

		PropertyCondition.register(CondIsEdible.class, "(edible|consumable)", "materials/itemstacks");
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public boolean check(Object object) {
		if (object instanceof Material material) {
			return material.isEdible();
		} else if (object instanceof ItemStack itemstack) {
			if (HAS_CONSUMABLE && itemstack.hasData(DataComponentTypes.CONSUMABLE)) {
				return true;
			} else if (HAS_FOOD && itemstack.getItemMeta().hasFood()) {
				return true;
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
