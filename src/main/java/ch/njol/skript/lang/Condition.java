package ch.njol.skript.lang;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Checker;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/**
 * A condition which must be fulfilled for the trigger to continue. If the condition is in a section the behaviour depends on the section.
 *
 * @see Skript#registerCondition(Class, String...)
 */
public abstract class Condition extends Statement {

	private boolean negated;

	protected Condition() {}

	/**
	 * Checks whether this condition is satisfied with the given event. This should not alter the event or the world in any way, as conditions are only checked until one returns
	 * false. All subsequent conditions of the same trigger will then be omitted.<br/>
	 * <br/>
	 * You might want to use {@link SimpleExpression#check(Event, Checker)}
	 * 
	 * @param event the event to check
	 * @return <code>true</code> if the condition is satisfied, <code>false</code> otherwise or if the condition doesn't apply to this event.
	 */
	public abstract boolean check(Event event);

	@Override
	public final boolean run(Event event) {
		return check(event);
	}

	/**
	 * Sets the negation state of this condition. This will change the behaviour of {@link Expression#check(Event, Checker, boolean)}.
	 */
	protected final void setNegated(boolean invert) {
		negated = invert;
	}

	/**
	 * @return whether this condition is negated or not.
	 */
	public final boolean isNegated() {
		return negated;
	}

	@Nullable
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static Condition parse(String input, @Nullable String defaultError) {
		input = input.trim();
		while (input.startsWith("(") && SkriptParser.next(input, 0, ParseContext.DEFAULT) == input.length())
			input = input.substring(1, input.length() - 1);
		return (Condition) SkriptParser.parse(input, (Iterator) Skript.getConditions().iterator(), defaultError);
	}

}
