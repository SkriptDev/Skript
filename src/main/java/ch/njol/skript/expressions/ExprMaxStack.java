package ch.njol.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author joeuguce99
 */
@Name("Maximum Stack Size")
@Description("The maximum stack size of the specified material, e.g. 64 for torches, 16 for buckets, and 1 for swords.")
@Examples("send \"You can only pick up %max stack size of player's tool% of %type of (player's tool)%\" to player")
@Since("2.1")
public class ExprMaxStack extends SimplePropertyExpression<Object, Long> {

	static {
		register(ExprMaxStack.class, Long.class, "max[imum] stack[[ ]size]", "material/itemstack");
	}

	@SuppressWarnings("null")
	@Override
	public Long convert(Object object) {
		if (object instanceof Material material)
			return (long) material.getMaxStackSize();
		return (long) ((ItemStack) object).getMaxStackSize();
	}

	@Override
	public Class<? extends Long> getReturnType() {
		return Long.class;
	}

	@Override
	protected String getPropertyName() {
		return "maximum stack size";
	}

}
