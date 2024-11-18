package ch.njol.skript.expressions;

import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.util.slot.Slot;
import ch.njol.util.Kleenean;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Peter Güttinger
 */
@Name("Inventory")
@Description("The inventory of a block or player. You can usually omit this expression and can directly add or remove items to/from blocks or players.")
@Examples({"add a plank to the player's inventory",
	"clear the player's inventory",
	"remove 5 wool from the inventory of the clicked block"})
@Since("1.0")
public class ExprInventory extends SimpleExpression<Object> {

	private boolean inLoop;
	@SuppressWarnings("null")
	private Expression<?> holders;

	static {
		PropertyExpression.register(ExprInventory.class, Object.class,
			"inventor(y|ies)", "inventoryholders/itemstacks");
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		// prevent conflict with ExprItemsIn (https://github.com/SkriptLang/Skript/issues/6290)
//		if (exprs[0].getSource() instanceof ExprItemsIn)
//			return false;
		// if we're dealing with a loop of just this expression
		Node n = SkriptLogger.getNode();
		inLoop = n != null && ("loop " + parseResult.expr).equals(n.getKey());
		holders = exprs[0];
		return true;
	}

	@Override
	protected Object[] get(Event e) {
		List<Inventory> inventories = new ArrayList<>();
		for (Object holder : holders.getArray(e)) {
			if (holder instanceof InventoryHolder) {
				inventories.add(((InventoryHolder) holder).getInventory());
			} else if (holder instanceof ItemStack itemStack) {
				ItemMeta meta = itemStack.getItemMeta();
				if (!(meta instanceof BlockStateMeta))
					continue;
				BlockState state = ((BlockStateMeta) meta).getBlockState();
				if (!(state instanceof Container))
					continue;
				Inventory underlyingInv = ((Container) state).getInventory();
				// The proxy is used here to ensure that any changes to the inventory are reflected in the
				// BlockStateMeta and ItemMeta of `holder`
				Inventory proxy = (Inventory) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Inventory.class}, new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						Object returnValue = method.invoke(underlyingInv, args);
						// calling update here causes the changes to the inventory to be synced to the meta
						boolean updateSucceeded = state.update();
						if (updateSucceeded) {
							((BlockStateMeta) meta).setBlockState(state);
							itemStack.setItemMeta(meta);
						}
						return returnValue;
					}
				});
				inventories.add(proxy);
			}
		}
		Inventory[] invArray = inventories.toArray(new Inventory[0]);
		if (inLoop) {
			List<ItemStack> items = new ArrayList<>();
			for (Inventory inventory : inventories) {
				items.addAll(Arrays.asList(inventory.getContents()));
			}
			return items.toArray(new ItemStack[0]);
		}
		return invArray;
	}

	@Override
	public boolean isSingle() {
		return !inLoop && holders.isSingle();
	}

	@Override
	public Class<?> getReturnType() {
		return inLoop ? Slot.class : Inventory.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "inventor" + (holders.isSingle() ? "y" : "ies") + " of " + holders.toString(e, debug);
	}

}
