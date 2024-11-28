package ch.njol.skript.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.inventory.ItemStack;

@Name("Is Unbreakable")
@Description("Checks whether an item is unbreakable.")
@Examples({
	"if event-item is unbreakable:",
	"\tsend \"This item is unbreakable!\" to player",
	"if tool of {_p} is breakable:",
	"\tsend \"Your tool is breakable!\" to {_p}"
})
@Since("2.5.1, 2.9.0 (breakable)")
public class CondIsUnbreakable extends PropertyCondition<ItemStack> {

	static {
		register(CondIsUnbreakable.class, "[:un]breakable", "itemstacks");
	}

	private boolean breakable;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		breakable = !parseResult.hasTag("un");
		return super.init(exprs, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public boolean check(ItemStack itemStack) {
		return itemStack.getItemMeta().isUnbreakable() ^ breakable;
	}

	@Override
	protected String getPropertyName() {
		return breakable ? "breakable" : "unbreakable";
	}

}
