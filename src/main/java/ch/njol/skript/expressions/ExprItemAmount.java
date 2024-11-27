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
@Description("The amount of an <a href='classes.html#itemstack'>item stack</a>.")
@Examples("send \"You have got %item amount of player's tool% %player's tool% in your hand!\" to player")
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
		int amount = delta != null ? ((Number) delta[0]).intValue() : 0;
		switch (mode) {
			case REMOVE:
				amount = -amount;
				// fall through
			case ADD:
				for (ItemStack itemStack : getExpr().getArray(event)) {
					itemStack.setAmount(itemStack.getAmount() + amount);
				}
				break;
			case RESET:
			case DELETE:
				amount = 1;
				// fall through
			case SET:
				for (ItemStack itemStack : getExpr().getArray(event)) {
					itemStack.setAmount(amount);
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
		return "item[[ ]stack] (amount|size|number)";
	}
}
