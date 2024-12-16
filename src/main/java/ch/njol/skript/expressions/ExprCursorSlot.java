package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Cursor item slot is not actually an inventory slot, but an item which the player
 * has in their cursor when any inventory is open for them.
 */
@Name("Cursor Slot")
@Description({"The item which the player has in their inventory cursor.",
	"This slot is always empty if player has no inventory open."})
@Examples({"material of cursor slot of player is dirt",
	"set cursor slot of player to itemstack of 64 of diamond"})
@Since("2.2-dev17")
public class ExprCursorSlot extends PropertyExpression<Player, ItemStack> {

	static {
		register(ExprCursorSlot.class, ItemStack.class, "cursor slot", "players");
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		setExpr((Expression<? extends Player>) exprs[0]);
		return true;
	}

	@Override
	protected ItemStack[] get(Event event, Player[] source) {
		return get(source, player -> {
			if (event instanceof InventoryClickEvent inventoryClickEvent)
				return inventoryClickEvent.getCursor();
			return player.getItemOnCursor();
		});
	}

	@Override
	public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.DELETE) {
			return CollectionUtils.array(ItemStack.class);
		}
		return null;
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		ItemStack itemStack = null;
		if (mode == ChangeMode.SET && delta != null && delta[0] instanceof ItemStack is) {
			itemStack = is;
		}

		for (Player player : getExpr().getArray(event)) {
			if (event instanceof InventoryClickEvent inventoryClickEvent) {
				inventoryClickEvent.setCursor(itemStack);
			} else {
				player.setItemOnCursor(itemStack);
			}
		}
	}

	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "cursor slot of " + getExpr().toString(event, debug);
	}

}
