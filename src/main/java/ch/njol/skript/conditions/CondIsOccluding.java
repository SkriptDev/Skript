package ch.njol.skript.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import org.bukkit.Material;

@Name("Is Occluding")
@Description("Checks whether a material is a block and completely blocks vision.")
@Examples("player's tool is occluding") // todo better examples
@Since("2.5.1")
public class CondIsOccluding extends PropertyCondition<Material> {

	static {
		register(CondIsOccluding.class, "occluding", "materials");
	}

	@Override
	public boolean check(Material material) {
		return material.isOccluding();
	}

	@Override
	protected String getPropertyName() {
		return "occluding";
	}

}
