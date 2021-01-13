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
package ch.njol.skript.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.localization.Language;
import ch.njol.util.StringUtils;

/**
 * @author Peter Güttinger
 */
public final class EnumUtils<E extends Enum<E>> {
	
	private final Class<E> c;
	@Nullable
	private final String languageNode;
	private String[] names;
	private final HashMap<String, E> parseMap = new HashMap<>();
	
	public EnumUtils(@NonNull final Class<E> c, @NonNull final String languageNode) {
		assert c.isEnum();
		assert !languageNode.isEmpty() && !languageNode.endsWith(".") : languageNode;
		
		this.c = c;
		this.languageNode = languageNode;
		this.names = new String[c.getEnumConstants().length];
		
		Language.addListener(() -> validate(true));
	}
	
	public EnumUtils(@NonNull Class<E> c) {
		assert c.isEnum();
		this.c = c;
		this.languageNode = null;
		this.names = new String[c.getEnumConstants().length];
		
		for (E enumConstant : c.getEnumConstants()) {
			String name = enumConstant.name().toLowerCase(Locale.ROOT).replace("_", " ");
			parseMap.put(name, enumConstant);
			names[enumConstant.ordinal()] = name;
		}
	}
	
	public EnumUtils(@NonNull Class<E> c, @Nullable String prefix, @Nullable String suffix) {
		assert c.isEnum();
		this.c = c;
		this.languageNode = null;
		this.names = new String[c.getEnumConstants().length];
		
		for (E enumConstant : c.getEnumConstants()) {
			String name = enumConstant.name().toLowerCase(Locale.ROOT).replace("_", " ");
			if (prefix != null && !name.startsWith(prefix))
				name = prefix + " " + name;
			if (suffix != null && !name.endsWith(suffix))
				name = name + " " + suffix;
			parseMap.put(name, enumConstant);
			names[enumConstant.ordinal()] = name;
		}
	}
	
	/**
	 * Updates the names if the language has changed or the enum was modified (using reflection).
	 */
	final void validate(final boolean force) {
		boolean update = force;
		
		final int newL = c.getEnumConstants().length;
		if (newL > names.length) {
			names = new String[newL];
			update = true;
		}
		
		if (update) {
			parseMap.clear();
			for (final E e : c.getEnumConstants()) {
				if (languageNode != null) {
					final String[] ls = Language.getList(languageNode + "." + e.name());
					names[e.ordinal()] = ls[0];
					for (final String l : ls)
						parseMap.put(l.toLowerCase(), e);
				} else {
					String name = e.name().toLowerCase(Locale.ROOT).replace("_", " ");
					parseMap.put(name, e);
					names[e.ordinal()] = name;
				}
			}
		}
	}
	
	@Nullable
	public final E parse(final String s) {
		validate(false);
		return parseMap.get(s.toLowerCase(Locale.ROOT));
	}
	
	@SuppressWarnings({"null", "unused"})
	public final String toString(final E e, final int flags) {
		validate(false);
		return names[e.ordinal()];
	}
	
	public final String getAllNames() {
		validate(false);
		List<String> names = new ArrayList<>();
		Collections.addAll(names, this.names);
		Collections.sort(names);
		return StringUtils.join(names, ", ");
	}
	
}
