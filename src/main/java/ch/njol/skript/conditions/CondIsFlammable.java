package ch.njol.skript.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import org.bukkit.Material;

@Name("Is Flammable")
@Description("Checks whether a material is flammable.")
@Examples({"wood is flammable", "player's tool is flammable"}) // TODO fix examples

@Since("2.2-dev36")
public class CondIsFlammable extends PropertyCondition<Material> {
	
	static {
		register(CondIsFlammable.class, "flammable", "materials");
	}
	
	@Override
	public boolean check(Material material) {
		return material.isFlammable();
	}
	
	@Override
	protected String getPropertyName() {
		return "flammable";
	}
	
}
