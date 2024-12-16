package ch.njol.skript.conditions;

import org.bukkit.entity.Entity;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;

@Name("Is Invulnerable")
@Description("Checks whether an entity is invulnerable.")
@Examples("target entity is invulnerable")
@Since("2.5")
public class CondIsInvulnerable extends PropertyCondition<Entity> {
	
	static {
		register(CondIsInvulnerable.class, PropertyType.BE, "(invulnerable|invincible)", "entities");
	}
	
	@Override
	public boolean check(Entity entity) {
		return entity.isInvulnerable();
	}
	
	@Override
	protected String getPropertyName() {
		return "invulnerable";
	}
	
}
