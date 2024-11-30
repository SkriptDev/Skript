package ch.njol.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.EventValueExpression;
import org.bukkit.inventory.ItemStack;

@Name("Item")
@Description("The item involved in an event, e.g. in a drop, dispense, pickup or craft event.")
@Examples({"on dispense:",
	"\titem is a clock",
	"\tset the time to 6:00"})
@Since("<i>unknown</i> (before 2.1)")
public class ExprItem extends EventValueExpression<ItemStack> {

	static {
		register(ExprItem.class, ItemStack.class, "item[stack]");
	}

	public ExprItem() {
		super(ItemStack.class);
	}

}
