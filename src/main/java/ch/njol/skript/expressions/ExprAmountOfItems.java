package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("Amount of Items")
@Description("Counts how many of a particular Material or ItemStack are in a given inventory.")
@Examples("message \"You have %number of diamond in the player's inventory% ores in your inventory.\"")
@Since("2.0")
public class ExprAmountOfItems extends SimpleExpression<Long> {

	static {
		Skript.registerExpression(ExprAmountOfItems.class, Long.class, ExpressionType.PROPERTY,
			"[the] (amount|number) of %materials/itemstacks% (in|of) %inventories%");
	}

	private Expression<?> items;
	private Expression<Inventory> inventories;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		items = exprs[0];
		inventories = (Expression<Inventory>) exprs[1];
		return true;
	}

	@Override
	protected Long[] get(Event event) {
		long amount = 0;
		for (Inventory inventory : this.inventories.getArray(event)) {
			for (ItemStack itemStack : inventory) {
				if (itemStack == null) continue;

				for (Object object : this.items.getArray(event)) {
					if (object instanceof ItemStack item) {
						if (itemStack.isSimilar(item))
							amount += itemStack.getAmount();
					} else if (object instanceof Material material) {
						if (itemStack.getType() == material)
							amount += itemStack.getAmount();
					}
				}
			}
		}
		return new Long[]{amount};
	}

	@Override
	public Class<? extends Long> getReturnType() {
		return Long.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "the number of " + items.toString(e, debug) + " in " + inventories.toString(e, debug);
	}

}
