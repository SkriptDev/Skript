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
package ch.njol.skript.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import org.bukkit.Material;

@Name("Is Edible")
@Description("Checks whether a material is edible.")
@Examples({"steak is edible", "player's tool is edible"}) //todo fix docs
@Since("2.2-dev36")
public class CondIsEdible extends PropertyCondition<Material> {

	static {
		PropertyCondition.register(CondIsEdible.class, "edible", "materials");
	}

	@Override
	public boolean check(Material material) {
		return material.isEdible();
	}

	@Override
	protected String getPropertyName() {
		return "edible";
	}

}
