package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Events;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.jetbrains.annotations.Nullable;

@Name("Victim")
@Description("The victim of a damage event, e.g. when a player attacks a zombie this expression represents the zombie. " +
	"When using Minecraft 1.11+, this also covers the hit entity in a projectile hit event.")
@Examples({"on damage:",
	"\tvictim is a creeper",
	"\tdamage the attacked by 1 heart"})
@Since("1.3, 2.6.1 (projectile hit event)")
@Events({"damage", "death", "projectile hit"})
public class ExprVictim extends SimpleExpression<Entity> {

	private static final boolean SUPPORT_PROJECTILE_HIT = Skript.methodExists(ProjectileHitEvent.class, "getHitEntity");

	static {
		Skript.registerExpression(ExprVictim.class, Entity.class, ExpressionType.SIMPLE,
			"[the] victim");
	}


	@Override
	public boolean init(Expression<?>[] vars, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
		if (!getParser().isCurrentEvent(EntityDamageEvent.class, EntityDeathEvent.class, VehicleDamageEvent.class, VehicleDestroyEvent.class, ProjectileHitEvent.class)
			|| !SUPPORT_PROJECTILE_HIT && getParser().isCurrentEvent(ProjectileHitEvent.class)) {
			Skript.error("The expression 'victim' can only be used in a damage" + (SUPPORT_PROJECTILE_HIT ? ", death, or projectile hit" : " or death") + " event");
			return false;
		}
		return true;
	}

	@Override
	@Nullable
	protected Entity[] get(Event e) {
		Entity entity;
		if (e instanceof EntityEvent)
			if (SUPPORT_PROJECTILE_HIT && e instanceof ProjectileHitEvent)
				entity = ((ProjectileHitEvent) e).getHitEntity();
			else
				entity = ((EntityEvent) e).getEntity();
		else if (e instanceof VehicleEvent)
			entity = ((VehicleEvent) e).getVehicle();
		else
			return null;
		if (entity != null) return new Entity[]{entity};
		return null;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Entity> getReturnType() {
		return Entity.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		if (e == null)
			return "the attacker";
		return Classes.getDebugMessage(getSingle(e));
	}

}
