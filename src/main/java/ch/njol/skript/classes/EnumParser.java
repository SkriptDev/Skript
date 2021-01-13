package ch.njol.skript.classes;

import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.util.EnumUtils;

/**
 * A parser, which utilizes Enums, used to parse data from a string or turn data into a string.
 *
 * @param <T> Type of this parser
 */
public class EnumParser<T extends Enum<T>> extends Parser<T> {
	
	EnumUtils<T> enumUtils;
	
	public EnumParser(EnumUtils<T> enumUtils) {
		this.enumUtils = enumUtils;
	}
	
	@Nullable
	@Override
	public T parse(String s, ParseContext context) {
		return enumUtils.parse(s);
	}
	
	@Override
	public String toString(T o, int flags) {
		return enumUtils.toString(o, flags);
	}
	
	@Override
	public String toVariableNameString(T o) {
		return toString(o, 0);
	}
	
	@Override
	public String getVariableNamePattern() {
		return "\\S+";
	}
	
}
