package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Events;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Furnace Slot")
@Description({
	"A slot of a furnace, i.e. either the ore, fuel or result slot.",
	"Remember to use '<a href='#ExprBlock'>block</a>' and not <code>furnace</code>, as <code>furnace</code> is not an existing expression.",
	"Note that if the result in a smelt event is changed to an item that differs in type from the items currently in " +
		"the result slot, the smelting will fail to complete (the item will attempt to smelt itself again).",
	"Note that if values other than <code>the result</code> are changed, event values may not accurately reflect the actual items in a furnace.",
	"Thus you may wish to use the event block in this case (e.g. <code>the fuel slot of the event-block</code>) to get accurate values if needed."
})
@Examples({
	"set the fuel slot of the clicked block to a lava bucket",
	"set the block's ore slot to 64 iron ore",
	"give the result of the block to the player",
	"clear the result slot of the block"
})
@Events({"smelt", "fuel burn"})
@Since("1.0, 2.8.0 (syntax rework)")
public class ExprFurnaceSlot extends SimpleExpression<ItemStack> {

	private static final int ORE = 0, FUEL = 1, RESULT = 2;

	static {
		Skript.registerExpression(ExprFurnaceSlot.class, ItemStack.class, ExpressionType.PROPERTY,
			"[the] (0:ore slot|1:fuel slot|2:result [slot])",
			"[the] (0:ore|1:fuel|2:result) slot[s] of %blocks%",
			"%blocks%'[s] (0:ore|1:fuel|2:result) slot[s]"
		);
	}

	@Nullable
	private Expression<Block> blocks;
	private boolean isEvent;
	private boolean isResultSlot;
	private int slot;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		this.isEvent = matchedPattern == 0;
		if (!this.isEvent)
			this.blocks = (Expression<Block>) exprs[0];

		this.slot = parseResult.mark;
		this.isResultSlot = slot == 7;
		if (this.isResultSlot)
			this.slot = RESULT;

		if (this.isEvent && (this.slot == ORE || this.slot == RESULT) && !getParser().isCurrentEvent(BlockCookEvent.class)) {
			Skript.error("Cannot use 'result slot' or 'ore slot' outside a block cook event.");
			return false;
		} else if (this.isEvent && slot == FUEL && !getParser().isCurrentEvent(FurnaceBurnEvent.class)) {
			Skript.error("Cannot use 'fuel slot' outside a fuel burn event.");
			return false;
		}

		return true;
	}

	@Override
	@Nullable
	protected ItemStack[] get(Event event) {
		List<ItemStack> itemStacks = new ArrayList<>();

		if (event instanceof BlockCookEvent smeltEvent) {
			if (this.slot == ORE)
				return new ItemStack[]{smeltEvent.getSource()};
			else if (this.slot == RESULT)
				return new ItemStack[]{smeltEvent.getResult()};
		} else if (event instanceof FurnaceBurnEvent burnEvent) {
			if (this.slot == FUEL) return new ItemStack[]{burnEvent.getFuel()};
		} else if (this.blocks != null) {
			for (Block block : this.blocks.getArray(event)) {
				if (block.getState() instanceof Furnace furnace) {
					FurnaceInventory inventory = furnace.getInventory();
					ItemStack is = switch (this.slot) {
						case FUEL -> inventory.getFuel();
						case RESULT -> inventory.getResult();
						default -> inventory.getSmelting();
					};
					itemStacks.add(is);
				}
			}
		}

		return itemStacks.toArray(new ItemStack[0]);
	}

	@Override
	public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
		if (getParser().isCurrentEvent(BlockCookEvent.class) && this.slot != RESULT) {
			Skript.error("The fuel/ore slots cannot be set in a block cook event.");
			return null;
		}
		if (getParser().isCurrentEvent(FurnaceBurnEvent.class)) {
			Skript.error("The fuel slot cannot be set in a furnace burn event.");
			return null;
		}
		if (mode == ChangeMode.SET || mode == ChangeMode.DELETE) {
			return CollectionUtils.array(ItemStack.class);
		}
		return null;
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		ItemStack itemStack = null;
		if (mode == ChangeMode.SET && delta != null && delta[0] instanceof ItemStack is)
			itemStack = is;

		if (event instanceof BlockCookEvent smeltEvent) {
			if (this.slot == RESULT && itemStack != null) {
				smeltEvent.setResult(itemStack);
			}
		} else if (this.blocks != null) {
			for (Block block : this.blocks.getArray(event)) {
				if (block.getState() instanceof Furnace furnace) {
					FurnaceInventory inventory = furnace.getInventory();
					switch (this.slot) {
						case FUEL -> inventory.setFuel(itemStack);
						case RESULT -> inventory.setResult(itemStack);
						default -> inventory.setSmelting(itemStack);
					}
				}
			}
		}
	}

	@Override
	public boolean isSingle() {
		if (this.isEvent)
			return true;
		assert this.blocks != null;
		return this.blocks.isSingle();
	}

	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		String time = (getTime() == -1) ? "past " : (getTime() == 1) ? "future " : "";
		String slotName = (slot == ORE) ? "ore" : (slot == FUEL) ? "fuel" : "result";
		if (isEvent) {
			return "the " + time + slotName + (isResultSlot ? " slot" : "");
		} else {
			assert blocks != null;
			return "the " + time + slotName + " slot of " + blocks.toString(event, debug);
		}
	}

	@Override
	public boolean setTime(int time) {
		if (this.isEvent) { // getExpr will be null
			if (this.slot == RESULT && !this.isResultSlot) { // 'the past/future result' - doesn't make sense, don't allow it
				return false;
			} else if (this.slot == FUEL) {
				return setTime(time, FurnaceBurnEvent.class);
			} else {
				return setTime(time, FurnaceSmeltEvent.class);
			}
		}
		return false;
	}

}
