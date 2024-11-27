package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Inventory Slot")
@Description({"Represents a slot in an inventory. It can be used to change the item in an inventory too."})
@Examples({"if slot 1 of player is set:",
	"\tset slot 0 of player to itemstack of 2 of stone",
	"\tclear slot 1 of player"})
@Since("2.2-dev24")
public class ExprInventorySlot extends SimpleExpression<ItemStack> {

	static {
		Skript.registerExpression(ExprInventorySlot.class, ItemStack.class, ExpressionType.COMBINED,
			"[the] slot[s] %numbers% of %inventory%",
			"%inventory%'[s] slot[s] %numbers%");
	}

	@SuppressWarnings("null")
	private Expression<Number> slots;
	@SuppressWarnings("null")
	private Expression<Inventory> inventory;

	@SuppressWarnings({"null", "unchecked"})
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		this.slots = (Expression<Number>) exprs[matchedPattern];
		this.inventory = (Expression<Inventory>) exprs[matchedPattern == 0 ? 1 : 0];
		return true;
	}

	@Override
	@Nullable
	protected ItemStack[] get(Event event) {
		Inventory inventory = this.inventory.getSingle(event);
		if (inventory == null)
			return null;

		List<ItemStack> itemStacks = new ArrayList<>();
		for (Number slot : this.slots.getArray(event)) {
			int slotIndex = slot.intValue();
			if (slotIndex >= 0 && slotIndex < inventory.getSize()) {

				// Not all indices point to inventory slots. Equipment, for example
				if (inventory instanceof PlayerInventory playerInventory && slotIndex >= 36) {
					HumanEntity holder = playerInventory.getHolder();
					assert holder != null;
					//inventorySlots.add(new EquipmentSlot(holder, slotIndex));
				} else {
					itemStacks.add(inventory.getItem(slotIndex));
				}
			}
		}

		if (itemStacks.isEmpty())
			return null;
		return itemStacks.toArray(new ItemStack[0]);
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

		Inventory inventory = this.inventory.getSingle(event);
		if (inventory == null) return;

		for (Number number : this.slots.getArray(event)) {
			int slot = number.intValue();
			if (slot >= 0 && slot < inventory.getSize()) {
				inventory.setItem(slot, itemStack);
			}
		}
	}

	@Override
	public boolean isSingle() {
		return this.slots.isSingle();
	}

	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "slots " + slots.toString(e, debug) + " of " + inventory.toString(e, debug);
	}
}
