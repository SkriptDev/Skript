package ch.njol.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import org.bukkit.Material;

@Name("Is Fuel")
@Description("Checks whether an item can be used as fuel in a furnace.")
@Examples({"on right click on furnace:",
	"\tif player's tool is not fuel:",
	"\t\tsend \"Please hold a valid fuel item in your hand\"",
	"\t\tcancel event"
})// todo fix docs
@Since("2.5.1")
@RequiredPlugins("Minecraft 1.11.2+")
public class CondIsFuel extends PropertyCondition<Material> {

	static {
		if (Skript.methodExists(Material.class, "isFuel")) {
			register(CondIsFuel.class, "[furnace] fuel", "materials");
		}
	}

	@Override
	public boolean check(Material material) {
		return material.isFuel();
	}

	@Override
	protected String getPropertyName() {
		return "fuel";
	}

}
