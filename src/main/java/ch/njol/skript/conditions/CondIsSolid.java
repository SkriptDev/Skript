package ch.njol.skript.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import org.bukkit.Material;

@Name("Is Solid")
@Description("Checks whether a material is solid.")
@Examples({"grass block is solid", "player's tool isn't solid"}) // TODO fix examples
@Since("2.2-dev36")
public class CondIsSolid extends PropertyCondition<Material> {

	static {
		register(CondIsSolid.class, "solid", "materials");
	}

	@Override
	public boolean check(Material material) {
		return material.isSolid();
	}

	@Override
	protected String getPropertyName() {
		return "solid";
	}

}
