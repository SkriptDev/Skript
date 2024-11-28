package ch.njol.skript.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import org.bukkit.Material;

@Name("Is Transparent")
@Description({"Checks whether a material is transparent.",
	"Note that this condition may not work for all blocks, due to the transparency list used by Spigot not being completely accurate."})
@Examples("target block is transparent")
@Since("2.2-dev36")
public class CondIsTransparent extends PropertyCondition<Material> {

	static {
		register(CondIsTransparent.class, "transparent", "materials");
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean check(Material i) {
		return i.isTransparent();
	}

	@Override
	protected String getPropertyName() {
		return "transparent";
	}

}
