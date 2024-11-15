package ch.njol.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import org.bukkit.Material;

@Name("Is Interactable")
@Description("Checks whether or not a material is interactable.")
@Examples({"on block break:",
	"\tif event-block is interactable:",
	"\t\tcancel event",
	"\t\tsend \"You cannot break interactable blocks!\""}) //TODO fix examples
@Since("2.5.2")
@RequiredPlugins("Minecraft 1.13+")
public class CondIsInteractable extends PropertyCondition<Material> {

	static {
		if (Skript.methodExists(Material.class, "isInteractable")) {
			register(CondIsInteractable.class, "interactable", "materials");
		}
	}

	@Override
	public boolean check(Material material) {
		return material.isInteractable();
	}

	@Override
	protected String getPropertyName() {
		return "interactable";
	}
}
