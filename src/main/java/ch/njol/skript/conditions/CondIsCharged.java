package ch.njol.skript.conditions;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;

@Name("Is Charged")
@Description("Checks if a creeper is charged (powered).")
@Examples({"if the last spawned creeper is charged:",
			"\tbroadcast \"A charged creeper is at %location of last spawned creeper%\""})
@Since("2.5")
public class CondIsCharged extends PropertyCondition<LivingEntity> {
	
	static {
		register(CondIsCharged.class, "(charged|powered)", "livingentities");
	}
	
	@Override
	public boolean check(final LivingEntity e) {
		if (e instanceof Creeper)
			return ((Creeper) e).isPowered();
		return false;
	}
	
	@Override
	protected String getPropertyName() {
		return "charged";
	}
	
}
