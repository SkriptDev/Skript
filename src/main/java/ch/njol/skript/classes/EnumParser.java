/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
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
