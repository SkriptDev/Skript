package ch.njol.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Proxy;

@Name("Inventory")
@Description({"The inventory of a block or entity.",
	"You can usually omit this expression and can directly add or remove items to/from blocks or entities."})
@Examples({"add an oak plank to the player's inventory",
	"clear the player's inventory",
	"remove 5 of wool from the inventory of the clicked block"})
@Since("1.0")
public class ExprInventory extends SimplePropertyExpression<Object, Inventory> {

	static {
		register(ExprInventory.class, Inventory.class,
			"inventor(y|ies)", "inventoryholders/itemstacks");
	}

	@Override
	public @Nullable Inventory convert(Object from) {
		if (from instanceof InventoryHolder inventoryHolder) {
			return inventoryHolder.getInventory();
		} else if (from instanceof ItemStack itemStack) {
			ItemMeta meta = itemStack.getItemMeta();
			if (!(meta instanceof BlockStateMeta blockStateMeta) || !(blockStateMeta.getBlockState() instanceof Container container))
				return null;
			Inventory underlyingInv = container.getInventory();
			// The proxy is used here to ensure that any changes to the inventory are reflected in the
			// BlockStateMeta and ItemMeta of `holder`
			// calling update here causes the changes to the inventory to be synced to the meta
			return (Inventory) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class[]{Inventory.class}, (proxy1, method, args) -> {
					Object returnValue = method.invoke(underlyingInv, args);
					// calling update here causes the changes to the inventory to be synced to the meta
					boolean updateSucceeded = container.update();
					if (updateSucceeded) {
						((BlockStateMeta) meta).setBlockState(container);
						itemStack.setItemMeta(meta);
					}
					return returnValue;
				});
		}
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "inventory";
	}

	@Override
	public Class<? extends Inventory> getReturnType() {
		return Inventory.class;
	}

}
