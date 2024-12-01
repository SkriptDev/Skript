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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("ItemStack Create")
@Description({"Create a new item stack."})
@Examples({"set {_i} to itemstack of 10 of diamond",
	"set {_i} to itemstack of netherite shovel"})
@Since("3.0.0")
public class ExprItemStackCreate extends SimpleExpression<ItemStack> {

	static {
		Skript.registerExpression(ExprItemStackCreate.class, ItemStack.class, ExpressionType.COMBINED,
			"[new] item[ ]stack[s] (of|from) [%number% [of]] %materials%");
	}

	private Expression<Number> amount;
	private Expression<Material> materials;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		this.amount = (Expression<Number>) exprs[0];
		this.materials = (Expression<Material>) exprs[1];
		return true;
	}

	@Override
	protected @Nullable ItemStack[] get(Event event) {
		List<ItemStack> items = new ArrayList<>();

		int amount = 1;
		if (this.amount != null) {
			Number num = this.amount.getSingle(event);
			if (num != null) amount = num.intValue();
		}

		for (Material material : this.materials.getArray(event)) {
			ItemStack itemStack = new ItemStack(material, amount);
			items.add(itemStack);
		}
		return items.toArray(new ItemStack[0]);
	}

	@Override
	public boolean isSingle() {
		return this.materials.isSingle();
	}

	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		String amount = this.amount != null ? this.amount.toString(event, debug) + " of " : "";
		return "itemstack[s] of " + amount + this.materials.toString(event, debug);
	}

}
