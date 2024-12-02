package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("Item Amount")
@Description({"The stack size of an <a href='classes.html#itemstack'>ItemStack</a>.",
	"This amount is clamped between 0 and 99.",
	"Anything above 99 will not serialize and may kick the player.",
	"0 and below will delete the item."})
@Examples({"send \"You have got %item amount of player's tool% %player's tool% in your hand!\" to player",
	"set item amount of player's tool to 10",
	"add 3 to item amount of player's tool",
	"remove 2 from item amount of player's tool"})
@Since("2.2-dev24")
public class ExprItemAmount extends SimplePropertyExpression<ItemStack, Long> {

	static {
		register(ExprItemAmount.class, Long.class,
			"item[[ ]stack] (amount|size|number)", "itemstacks");
	}

	@Override
	public Long convert(final ItemStack item) {
		return (long) item.getAmount();
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case SET, ADD, RESET, DELETE, REMOVE ->
				CollectionUtils.array(Long.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		if (getExpr() == null) return;
		int amount = delta != null && delta[0] instanceof Number num ? num.intValue() : 0;

		switch (mode) {
			case REMOVE:
				amount = -amount;
				// fall through
			case ADD:
				for (ItemStack itemStack : getExpr().getArray(event)) {
					int clamp = Math.clamp(itemStack.getAmount() + amount, 0, 99);
					itemStack.setAmount(clamp);
				}
				break;
			case RESET:
			case DELETE:
				amount = 1;
				// fall through
			case SET:
				for (ItemStack itemStack : getExpr().getArray(event)) {
					int clamp = Math.clamp(amount, 0, 99);
					itemStack.setAmount(clamp);
				}
				break;
		}
	}

	@Override
	public Class<? extends Long> getReturnType() {
		return Long.class;
	}

	@Override
	protected String getPropertyName() {
		return "item amount";
	}

}
