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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("Item with Enchantment")
@Description("Add an enchantment to an ItemStack.")
@Examples("set {_i} to itemstack of diamond sword enchanted with unbreaking 3")
@Since("3.0.0")
public class ExprItemStackWithEnchantment extends SimpleExpression<ItemStack> {

	static {
		Skript.registerExpression(ExprItemStackWithEnchantment.class, ItemStack.class, ExpressionType.COMBINED,
			"%itemstack% (enchanted with|with enchantment) %enchantment%[ %-number%]");
	}

	private Expression<ItemStack> item;
	private Expression<Enchantment> enchantment;
	private @Nullable Expression<Number> level;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		this.item = (Expression<ItemStack>) exprs[0];
		this.enchantment = (Expression<Enchantment>) exprs[1];
		this.level = (Expression<Number>) exprs[2];
		return true;
	}

	@Override
	protected @Nullable ItemStack[] get(Event event) {
		ItemStack itemStack = this.item.getSingle(event);
		Enchantment enchantment = this.enchantment.getSingle(event);
		if (itemStack == null || enchantment == null) return null;
		// Clone incase we want a copy
		itemStack = itemStack.clone();

		int level = 1;
		if (this.level != null) {
			Number num = this.level.getSingle(event);
			if (num != null) level = num.intValue();
		}
		itemStack.addUnsafeEnchantment(enchantment, level);
		return new ItemStack[]{itemStack};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		String level = this.level != null ? " " + this.level.toString(event, debug) : "";
		return this.item.toString(event, debug) + " with enchantment " + enchantment.toString(event, debug) + level;
	}

}
