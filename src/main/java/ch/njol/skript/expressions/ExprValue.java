package ch.njol.skript.expressions;

import java.lang.reflect.Array;

import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Unit;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

/**
 * @author Peter Güttinger
 */
public class ExprValue extends SimpleExpression<Unit> {
//	static { // REMIND add this (>2.0)
//		Skript.registerExpression(ExprValue.class, Unit.class, ExpressionType.PATTERN_MATCHES_EVERYTHING, "%~number% %*unit%");
//	}
	
	@SuppressWarnings("null")
	private Expression<Number> amount;
	@SuppressWarnings("null")
	private Unit unit;
	
	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		amount = (Expression<Number>) exprs[0];
		unit = ((Literal<Unit>) exprs[1]).getSingle();
		return true;
	}
	
	@Override
	@Nullable
	protected Unit[] get(final Event e) {
		final Number a = amount.getSingle(e);
		if (a == null)
			return null;
		final Unit u = unit.clone();
		u.setAmount(a.doubleValue());
		final Unit[] one = (Unit[]) Array.newInstance(unit.getClass(), 1);
		one[0] = u;
		return one;
	}
	
	@Override
	public boolean isSingle() {
		return true;
	}
	
	@Override
	public Class<? extends Unit> getReturnType() {
		return unit.getClass();
	}
	
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return amount.toString(e, debug) + " " + unit.toString();
	}
	
}
