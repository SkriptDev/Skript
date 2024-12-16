package ch.njol.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.ItemComponentUtils;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Name("Is Fire Resistant")
@Description("Checks whether an item is fire resistant.")
@Examples({"if player's tool is fire resistant:",
	"if {_items::*} aren't resistant to fire:"})
@Since("2.9.0")
public class CondIsFireResistant extends PropertyCondition<ItemStack> {

	private static final boolean HAS_DATA_COMPONENTS = Skript.classExists("io.papermc.paper.datacomponent.DataComponentTypes");

	static {
		if (Skript.methodExists(ItemMeta.class, "isFireResistant"))
			PropertyCondition.register(CondIsFireResistant.class, "(fire resistant|resistant to fire)", "itemstacks");
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean check(ItemStack item) {
		ItemMeta itemMeta = item.getItemMeta();
		if (itemMeta != null) {
			if (itemMeta.isFireResistant()) return true;
			else if (itemMeta.hasEnchant(Enchantment.FIRE_PROTECTION))
				return true;
			else if (HAS_DATA_COMPONENTS) {
				return ItemComponentUtils.isFireResistant(item);
			}
		}
		return false;
	}

	@Override
	public String getPropertyName() {
		return "fire resistant";
	}

}
