package ch.njol.skript.conditions;

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

	static {
		PropertyCondition.register(CondIsEdible.class, "(edible|consumable)", "materials/itemstacks");
	}

	@Override
	public boolean check(Object object) {
		if (object instanceof Material material) {
			return material.isEdible();
		} else if (object instanceof ItemStack itemstack) {
			if (ItemComponentUtils.HAS_CONSUMABLE) {
				return ItemComponentUtils.isConsumable(itemstack);
			} else if (ItemComponentUtils.HAS_FOOD) {
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
