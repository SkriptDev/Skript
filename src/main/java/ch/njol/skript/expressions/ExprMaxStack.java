package ch.njol.skript.expressions;

import ch.njol.skript.bukkitutil.ItemUtils;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("Maximum Stack Size")
@Description({"The maximum stack size of the specified ItemStack/Material, e.g. 64 for torches, 16 for buckets, and 1 for swords.",
	"The max stack size you can set must be between 1 and 99 (Clamped value from Minecraft).",
	"Resetting will reset back to the vanilla Minecraft value."})
@Examples({"send \"You can only pick up %max stack size of player's tool% of %type of (player's tool)%\" to player",
	"set max stack size of player's tool to 10",
	"add 3 to max stack size of player's tool",
	"remove 20 from max stack size of player's tool",
	"reset max stack size of player's tool"})
@Since("2.1, INSERT VERSION (change)")
public class ExprMaxStack extends SimplePropertyExpression<Object, Long> {

	static {
		register(ExprMaxStack.class, Long.class,
			"max[imum] stack[[ ]size]", "materials/itemstacks");
	}

	@SuppressWarnings("null")
	@Override
	public Long convert(Object object) {
		if (object instanceof Material material)
			return (long) material.getMaxStackSize();
		return (long) ((ItemStack) object).getMaxStackSize();
	}

	@Override
	public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case SET, ADD, REMOVE, RESET -> CollectionUtils.array(Number.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		Expression<?> expr = getExpr();
		if (expr == null) return;

		int changeValue = delta != null && delta[0] instanceof Number number ? number.intValue() : 0;

		for (Object object : expr.getArray(event)) {
			if (object instanceof ItemStack itemStack) {
				int oldMax = ItemUtils.getMaxStackSize(itemStack);
				int newMax = switch (mode) {
					case ADD -> oldMax + changeValue;
					case REMOVE -> oldMax - changeValue;
					case RESET -> itemStack.getType().getMaxStackSize();
					default -> changeValue;
				};
				ItemUtils.setMaxStackSize(itemStack, newMax);
			}
		}
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
