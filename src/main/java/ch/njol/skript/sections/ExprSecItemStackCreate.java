package ch.njol.skript.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("ItemStack Create")
@Description({"Create a new item stack.",
	"This can be used as an expression as well as a section."})
@Examples({"set {_i} to itemstack of 10 of diamond",
	"set {_i} to itemstack of netherite shovel",
	"",
	"# Section",
	"set {_i} to itemstack of diamond sword",
	"\tset name of event-itemstack to \"My Sword\"",
	"\tset custom model data of event-itemstack to 1010",
	"give {_i} to player"})
@Since("3.0.0")
public class ExprSecItemStackCreate extends SectionExpression<ItemStack> {

	private static class ItemStackCreateEvent extends Event {

		private final ItemStack itemStack;

		public ItemStackCreateEvent(ItemStack itemStack) {
			this.itemStack = itemStack;
		}

		@Override
		public @NotNull HandlerList getHandlers() {
			throw new IllegalStateException("This event should never be called/registered!");
		}

		static {
			EventValues.registerEventValue(ItemStackCreateEvent.class, ItemStack.class, new Getter<>() {
				@Override
				public @Nullable ItemStack get(ItemStackCreateEvent event) {
					return event.itemStack;
				}
			}, EventValues.TIME_NOW);
		}
	}

	static {
		Skript.registerExpression(ExprSecItemStackCreate.class, ItemStack.class, ExpressionType.COMBINED,
			"[new] item[ ]stack[s] (of|from) [%number% [of]] %materials%");
	}

	private Expression<Number> amount;
	private Expression<Material> materials;
	private Trigger trigger;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int pattern, Kleenean delayed, ParseResult result,
						@Nullable SectionNode node, @Nullable List<TriggerItem> triggerItems) {
		this.amount = (Expression<Number>) expressions[0];
		this.materials = (Expression<Material>) expressions[1];
		if (node != null) {
			ParserInstance parser = getParser();
			Class<? extends Event>[] currentEvents = parser.getCurrentEvents();
			parser.setCurrentEvent("itemstack create", ItemStackCreateEvent.class);
			this.trigger = loadCode(node, "d", null, ItemStackCreateEvent.class);
			parser.setCurrentEvents(currentEvents);
		}
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

		Object localVars = Variables.copyLocalVariables(event);
		for (Material material : this.materials.getArray(event)) {
			ItemStack itemStack = new ItemStack(material, amount);

			// Execute section
			if (this.trigger != null) {
				ItemStackCreateEvent itemEvent = new ItemStackCreateEvent(itemStack);
				Variables.setLocalVariables(itemEvent, localVars);
				TriggerItem.walk(this.trigger, itemEvent);
				Variables.setLocalVariables(event, localVars);
				Variables.removeLocals(itemEvent);
			}

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
