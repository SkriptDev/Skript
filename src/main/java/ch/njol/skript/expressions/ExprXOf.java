package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("X of Item")
@Description({"An expression to be able to use a certain amount of items where the amount can be any expression.",
	"Please note that this expression is not stable and might be replaced in the future."})
@Examples("give level of player of diamond sword to the player")
@Since("1.2")
public class ExprXOf extends PropertyExpression<Object, ItemStack> {

	static {
		Skript.registerExpression(ExprXOf.class, ItemStack.class, ExpressionType.PATTERN_MATCHES_EVERYTHING,
			"%number% of %materials/itemstacks%");
	}

	private Expression<Number> amount;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		setExpr(exprs[1]);
		amount = (Expression<Number>) exprs[0];
		return true;
	}

	@Override
	protected ItemStack[] get(Event e, Object[] source) {
		Number a = this.amount.getSingle(e);
		if (a == null)
			return null;

		int amount = a.intValue();
		if (amount <= 0) return null;

		return get(source, object -> {
			if (object instanceof Material material) {
				if (!material.isItem()) return null;
				return new ItemStack(material, amount);
			} else if (object instanceof ItemStack itemStack) {
				ItemStack is = itemStack.clone();
				is.setAmount(amount);
				return is;
			}
			return null;
		});
	}

	@Override
	public Class<ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		assert getExpr() != null;
		return amount.toString(event, debug) + " of " + getExpr().toString(event, debug);
	}

}
