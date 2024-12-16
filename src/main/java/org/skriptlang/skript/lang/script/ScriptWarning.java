package org.skriptlang.skript.lang.script;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;

/**
 * An enum containing {@link Script} warnings that can be suppressed.
 */
public enum ScriptWarning {

	/**
	 * Variable cannot be saved (the ClassInfo is not serializable)
	 */
	VARIABLE_SAVE,

	/**
	 * Missing "and" or "or"
	 */
	MISSING_CONJUNCTION,

	/**
	 * Variable starts with an Expression
	 */
	VARIABLE_STARTS_WITH_EXPRESSION,

	/**
	 * This syntax is deprecated and scheduled for future removal
	 */
	DEPRECATED_SYNTAX;

	/**
	 * Prints the given message using {@link Skript#warning(String)} iff the current script does not suppress deprecation warnings.
	 * Intended for use in {@link ch.njol.skript.lang.SyntaxElement#init(Expression[], int, Kleenean, SkriptParser.ParseResult)}.
	 * The given message is prefixed with {@code "[Deprecated] "} to provide a common link between deprecation warnings.
	 *
	 * @param message the warning message to print.
	 */
	public static void printDeprecationWarning(String message) {
		ParserInstance parser = ParserInstance.get();
		Script currentScript = parser.isActive() ? parser.getCurrentScript() : null;
		if (currentScript != null && currentScript.suppressesWarning(ScriptWarning.DEPRECATED_SYNTAX))
			return;
		Skript.warning("[Deprecated] " + message);
	}

}
