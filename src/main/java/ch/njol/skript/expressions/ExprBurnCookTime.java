package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.arithmetic.Arithmetics;
import org.skriptlang.skript.lang.arithmetic.Operator;

import java.util.Arrays;
import java.util.function.Function;

@Name("Burn/Cook Time")
@Description({"The time a furnace takes to burn an item in a <a href='events.html#fuel_burn'>fuel burn</a> event.",
	"Can also be used to change the burn/cook time of a placed furnace."
})
@Examples({
	"on fuel burn:",
	"\tif fuel slot is coal:",
	"\t\tset burning time to 1 tick"
})
@Since("2.3")
public class ExprBurnCookTime extends PropertyExpression<Block, Timespan> {

	static {
		Skript.registerExpression(ExprBurnCookTime.class, Timespan.class, ExpressionType.PROPERTY,
			"[the] burn[ing] time",
			"[the] (burn|1:cook)[ing] time of %blocks%",
			"%blocks%'[s] (burn|1:cook)[ing] time");
	}

	private boolean cookTime;
	private boolean isEvent;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		cookTime = parseResult.mark == 1;
		isEvent = matchedPattern == 0;
		if (isEvent && !getParser().isCurrentEvent(FurnaceBurnEvent.class)) {
			Skript.error("Cannot use 'burning time' outside a fuel burn event.");
			return false;
		}
		if (!isEvent)
			setExpr((Expression<? extends Block>) exprs[0]);
		return true;
	}

	@Override
	protected Timespan[] get(Event event, Block[] source) {
		if (isEvent) {
			if (!(event instanceof FurnaceBurnEvent))
				return new Timespan[0];
			return CollectionUtils.array(Timespan.fromTicks(((FurnaceBurnEvent) event).getBurnTime()));
		} else {
			return Arrays.stream(source)
				.map(Block::getState)
				.filter(blockState -> blockState instanceof Furnace)
				.map(state -> {
					Furnace furnace = (Furnace) state;
					return Timespan.fromTicks(cookTime ? furnace.getCookTime() : furnace.getBurnTime());
				})
				.toArray(Timespan[]::new);
		}
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.SET)
			return CollectionUtils.array(Timespan.class);
		return null;
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		if (delta == null)
			return;

		Function<Timespan, Timespan> value = null;
		Timespan changed = (Timespan) delta[0];

		switch (mode) {
			case ADD:
				value = (original) -> Arithmetics.calculate(Operator.ADDITION, original, changed, Timespan.class);
				break;
			case REMOVE:
				value = (original) -> Arithmetics.difference(original, changed, Timespan.class);
				break;
			case SET:
				value = (original) -> changed;
				break;
			default:
				assert false;
				break;
		}

		if (isEvent) {
			if (!(event instanceof FurnaceBurnEvent))
				return;

			FurnaceBurnEvent burnEvent = (FurnaceBurnEvent) event;
			burnEvent.setBurnTime((int) value.apply(Timespan.fromTicks(burnEvent.getBurnTime())).getTicks());
			return;
		}

		for (Block block : getExpr().getArray(event)) {
			BlockState state = block.getState();
			if (!(state instanceof Furnace))
				continue;
			Furnace furnace = (Furnace) block.getState();
			long time = value.apply(Timespan.fromTicks(cookTime ? furnace.getCookTime() : furnace.getBurnTime())).getTicks();

			if (cookTime) {
				furnace.setCookTime((short) time);
			} else {
				furnace.setBurnTime((short) time);
			}

			furnace.update();
		}
	}

	@Override
	public Class<? extends Timespan> getReturnType() {
		return Timespan.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return isEvent ? "the burning time" : String.format("the %sing time of %s", cookTime ? "cook" : "burn", getExpr().toString(event, debug));
	}

}
