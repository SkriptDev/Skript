package ch.njol.skript.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import org.bukkit.Material;

@Name("Is Block")
@Description("Checks whether a material is a block.")
@Examples({"player's held item is a block", "{list::*} are blocks"}) // TODO fix examples
@Since("2.4")
public class CondIsBlock extends PropertyCondition<Material> {
	
	static {
		register(CondIsBlock.class, "([a] block|blocks)", "materials");
	}
	
	@Override
	public boolean check(Material material) {
		return material.isBlock();
	}
	
	@Override
	protected String getPropertyName() {
		return "block";
	}
	
}
