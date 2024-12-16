package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.ItemComponentUtils;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

@Name("ItemStack with Fire Resistance")
@Description("Creates a copy of an item with (or without) fire resistance.")
@Examples({"set {_x} to itemstack of diamond sword with fire resistance",
	"equip player with 1 of netherite helmet without fire resistance",
	"drop fire resistant itemstack of stone at player"})
@Since("2.9.0")
public class ExprItemStackWithFireResistance extends PropertyExpression<ItemStack, ItemStack> {

	private static final boolean HAS_DATA_COMPONENTS = Skript.classExists("io.papermc.paper.datacomponent.DataComponentTypes");

	static {
		Skript.registerExpression(ExprItemStackWithFireResistance.class, ItemStack.class, ExpressionType.PROPERTY,
			"%itemstack% with[:out] fire[ ]resistance",
			"fire resistant %itemstack%");
	}

	private boolean without;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		setExpr((Expression<ItemStack>) exprs[0]);
		without = parseResult.hasTag("out");
		return true;
	}

	@Override
	protected ItemStack[] get(Event event, ItemStack[] source) {
		return get(source.clone(), item -> {
			if (HAS_DATA_COMPONENTS) {
				ItemComponentUtils.setFireResistant(item, !without);
			} else {
				ItemMeta meta = item.getItemMeta();
				meta.setFireResistant(!without);
				item.setItemMeta(meta);
			}
			return item;
		});
	}

	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return getExpr().toString(event, debug) + " with fire resistance";
	}

}
