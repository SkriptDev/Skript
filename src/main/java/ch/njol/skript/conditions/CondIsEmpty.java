package ch.njol.skript.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Peter Güttinger
 */
@Name("Is Empty")
@Description("Checks whether an Inventory, ItemStack or a text is empty.")
@Examples("player's inventory is empty")
@Since("<i>unknown</i> (before 2.1)")
public class CondIsEmpty extends PropertyCondition<Object> {

	static {
		register(CondIsEmpty.class, "empty", "inventories/itemstacks/strings");
	}

	@Override
	public boolean check(final Object object) {
		if (object instanceof String string)
			return string.isEmpty();
		else if (object instanceof Inventory inventory) {
			for (ItemStack itemStack : inventory.getContents()) {
				if (itemStack != null && !itemStack.isEmpty())
					return false; // There is an item here!
			}
			return true;
		} else if (object instanceof ItemStack itemStack) {
			return itemStack.isEmpty();
		}
		assert false;
		return false;
	}

	@Override
	protected String getPropertyName() {
		return "empty";
	}

}
