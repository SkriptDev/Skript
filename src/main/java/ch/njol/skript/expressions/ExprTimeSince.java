package ch.njol.skript.expressions;

import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.util.Date;
import ch.njol.skript.util.Timespan;

@Name("Time Since")
@Description("The time that has passed since a date. If the given date is in the future, a value will not be returned.")
@Examples("send \"%time since 5 minecraft days ago% has passed since 5 minecraft days ago!\" to player")
@Since("2.5")
public class ExprTimeSince extends SimplePropertyExpression<Date, Timespan> {

	static {
		Skript.registerExpression(ExprTimeSince.class, Timespan.class, ExpressionType.PROPERTY, "[the] time since %dates%");
	}

	@Override
	@Nullable
	public Timespan convert(Date date) {
		Date now = Date.now();

		/*
		 * This condition returns whether the date the player is using is
		 * before the current date, the same as the current date, or after the current date.
		 * A value less than 0 indicates that the new date is BEFORE the current date.
		 * A value of 0 indicates that the new date is the SAME as the current date.
		 * A value greater than 0 indicates that the new date is AFTER the current date.
		 */
		if (date.compareTo(now) < 1)
			return date.difference(now);
		return null;
	}

	@Override
	public Class<? extends Timespan> getReturnType() {
		return Timespan.class;
	}

	@Override
	protected String getPropertyName() {
		return "time since";
	}

	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "the time since " + getExpr().toString(e, debug);
	}

}
