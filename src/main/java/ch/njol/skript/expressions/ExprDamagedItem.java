package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("Damaged Item")
@Description("Directly damages an item. In MC versions 1.12.2 and lower, this can be used to apply data values to items/blocks")
@Examples({"give player diamond sword with damage value 100", "set player's tool to diamond hoe damaged by 250",
	"give player diamond sword with damage 700 named \"BROKEN SWORD\"",
	"set {_item} to diamond hoe with damage value 50 named \"SAD HOE\"",
	"set target block of player to wool with data value 1", "set target block of player to potato plant with data value 7"}) // TODO fix examples
@Since("2.4")
public class ExprDamagedItem extends PropertyExpression<ItemStack, ItemStack> {

	static {
		Skript.registerExpression(ExprDamagedItem.class, ItemStack.class, ExpressionType.COMBINED,
			"%itemstacks% with (damage|data) [value] %number%",
			"%itemstacks% damaged by %number%");
	}

	@SuppressWarnings("null")
	private Expression<Number> damage;

	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		setExpr((Expression<ItemStack>) exprs[0]);
		damage = (Expression<Number>) exprs[1];
		return true;
	}

	@Override
	protected ItemStack[] get(Event e, ItemStack[] source) {
		Number damage = this.damage.getSingle(e);
		if (damage == null)
			return source;
		return get(source.clone(), item -> {
			item.setDurability(damage.shortValue());
			return item;
		});
	}

	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public String toString(final @Nullable Event e, boolean debug) {
		return getExpr().toString(e, debug) + " with damage value " + damage.toString(e, debug);
	}

}
