package ch.njol.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.EntityCategory;
import ch.njol.skript.bukkitutil.HealthUtils;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class EvtDamage extends SkriptEvent {

	static {
		Skript.registerEvent("Damage", EvtDamage.class, EntityDamageEvent.class,
				"damag(e|ing) [of %-entitytype/entitycategory%] [by %-entitytype/entitycategory%]")
			.description("Called when an entity receives damage, e.g. by an attack from another entity, lava, fire, drowning, fall, suffocation, etc.")
			.examples("on damage:", "on damage of a player:", "on damage of player by zombie:")
			.since("1.0, 2.7 (by entity)");
	}

	@Nullable
	private Literal<?> ofTypes, byTypes;

	@Override
	public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parser) {
		ofTypes = args[0];
		byTypes = args[1];
		return true;
	}

	@Override
	public boolean check(Event evt) {
		EntityDamageEvent entityDamageEvent = (EntityDamageEvent) evt;
		if (evt instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
			if (!checkDamager(entityDamageByEntityEvent.getDamager()))
				return false;
		} else if (byTypes != null) {
			return false;
		}
		if (!checkDamaged(entityDamageEvent.getEntity()))
			return false;
		if (entityDamageEvent instanceof EntityDamageByEntityEvent entityDamageByEntityEvent && entityDamageByEntityEvent.getDamager() instanceof EnderDragon && entityDamageEvent.getEntity() instanceof EnderDragon)
			return false;
		return checkDamage(entityDamageEvent);
	}

	private boolean checkDamager(Entity entity) {
		if (byTypes != null) {
			for (Object object : byTypes.getArray()) {
				if (object instanceof EntityType entityType && entityType == entity.getType())
					return true;
				else if (object instanceof EntityCategory entityCategory && entityCategory.isOfType(entity))
					return true;
			}
			return false;
		}
		return true;
	}

	private boolean checkDamaged(Entity entity) {
		if (ofTypes != null) {
			for (Object object : ofTypes.getArray()) {
				if (object instanceof EntityType entityType && entityType == entity.getType())
					return true;
				else if (object instanceof EntityCategory entityCategory && entityCategory.isOfType(entity))
					return true;
			}
			return false;
		}
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "damage" + (ofTypes != null ? " of " + ofTypes.toString(e, debug) : "") +
			(byTypes != null ? " by " + byTypes.toString(e, debug) : "");
	}

	private static boolean checkDamage(EntityDamageEvent entityDamageEvent) {
		if (!(entityDamageEvent.getEntity() instanceof LivingEntity livingEntity))
			return true;
		return !(HealthUtils.getHealth(livingEntity) <= 0);
	}

}
