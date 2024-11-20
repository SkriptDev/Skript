package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Items In")
@Description({"All items or specific type(s) of items in an inventory. Useful for looping or storing in a list variable.",
	"Please note that the positions of the items in the inventory are not saved, only their order is preserved."
})
@Examples({
	"loop all items in the player's inventory:",
	"\tloop-item is enchanted",
	"\tremove loop-item from the player",
	"set {inventory::%uuid of player%::*} to items in the player's inventory"
})
@Since("2.0, 2.8.0 (specific types of items)")
public class ExprItemsIn extends SimpleExpression<ItemStack> {

	static {
		Skript.registerExpression(ExprItemsIn.class, ItemStack.class, ExpressionType.PROPERTY,
			"[all [[of] the]] items ([with]in|of|contained in|out of) [1:inventor(y|ies)] %inventories%",
			"all [[of] the] %materials% ([with]in|of|contained in|out of) [1:inventor(y|ies)] %inventories%"
		);
	}

	private Expression<Inventory> inventories;

	@Nullable
	private Expression<Material> types;

	@Override
	@SuppressWarnings("unchecked")
	/*
	 * the parse result will be null if it is used via the ExprInventory expression, however the expression will never
	 * be a variable when used with that expression (it is always an anonymous SimpleExpression)
	 */
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (matchedPattern == 0) {
			inventories = (Expression<Inventory>) exprs[0];
		} else {
			types = (Expression<Material>) exprs[0];
			inventories = (Expression<Inventory>) exprs[1];
		}
		if (inventories instanceof Variable && !inventories.isSingle() && parseResult.mark != 1)
			Skript.warning("'items in {variable::*}' does not actually represent the items stored in the variable. Use either '{variable::*}' (e.g. 'loop {variable::*}') if the variable contains items, or 'items in inventories {variable::*}' if the variable contains inventories.");
		return true;
	}

	@Override
	protected ItemStack[] get(Event event) {
		List<ItemStack> itemStacks = new ArrayList<>();
		Material[] types = this.types == null ? null : this.types.getArray(event);
		for (Inventory inventory : inventories.getArray(event)) {
			for (ItemStack itemStack : inventory) {
				if (types != null) {
					for (Material type : types) {
						if (itemStack.getType() == type) {
							itemStacks.add(itemStack);
						}
					}
				} else {
					itemStacks.add(itemStack);
				}
			}
		}
		return itemStacks.toArray(new ItemStack[0]);
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		if (types == null)
			return "items in " + inventories.toString(event, debug);
		return "all " + types.toString(event, debug) + " in " + inventories.toString(event, debug);
	}

}
