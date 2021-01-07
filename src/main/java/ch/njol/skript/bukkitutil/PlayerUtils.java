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
package ch.njol.skript.bukkitutil;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.eclipse.jdt.annotation.Nullable;

/**
 * TODO check all updates and find out which ones are not required
 * 
 * @author Peter Güttinger
 */
public abstract class PlayerUtils {
	private PlayerUtils() {}
	
	final static Set<Player> inviUpdate = new HashSet<>();
	
	public static void updateInventory(final @Nullable Player p) {
		if (p != null)
			inviUpdate.add(p);
	}
	
}
