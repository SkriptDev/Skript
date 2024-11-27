package ch.njol.skript.bukkitutil;

import ch.njol.skript.Skript;
import ch.njol.util.Math2;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class HealthUtils {

	private static final Attribute MAX_HEALTH;

	static {
		if (Skript.isRunningMinecraft(1, 21, 2)) { // "generic" removed in 1.21.2
			MAX_HEALTH = Registry.ATTRIBUTE.get(NamespacedKey.minecraft("max_health"));
		} else {
			MAX_HEALTH = Registry.ATTRIBUTE.get(NamespacedKey.minecraft("generic.max_health"));
		}
	}

	/**
	 * Get the health of an entity
	 *
	 * @param e Entity to get health from
	 * @return The amount of hearts the entity has left
	 */
	public static double getHealth(Damageable e) {
		if (e.isDead())
			return 0;
		return e.getHealth();
	}

	/**
	 * Set the health of an entity
	 *
	 * @param e      Entity to set health for
	 * @param health The amount of hearts to set
	 */
	public static void setHealth(Damageable e, double health) {
		e.setHealth(Math2.fit(0, health, getMaxHealth(e)));
	}

	/**
	 * Get the max health an entity has
	 *
	 * @param e Entity to get max health from
	 * @return How many hearts the entity can have at most
	 */
	public static double getMaxHealth(Damageable e) {
		AttributeInstance attributeInstance = ((Attributable) e).getAttribute(MAX_HEALTH);
		assert attributeInstance != null;
		return attributeInstance.getValue();
	}

	/**
	 * Set the max health an entity can have
	 *
	 * @param e      Entity to set max health for
	 * @param health How many hearts the entity can have at most
	 */
	public static void setMaxHealth(Damageable e, double health) {
		AttributeInstance attributeInstance = ((Attributable) e).getAttribute(MAX_HEALTH);
		assert attributeInstance != null;
		attributeInstance.setBaseValue(health);
	}

	/**
	 * Apply damage to an entity
	 *
	 * @param e Entity to apply damage to
	 * @param d Amount of hearts to damage
	 */
	public static void damage(Damageable e, double d) {
		if (d < 0) {
			heal(e, -d);
			return;
		}
		e.damage(d);
	}

	/**
	 * Heal an entity
	 *
	 * @param e Entity to heal
	 * @param h Amount of hearts to heal
	 */
	public static void heal(Damageable e, double h) {
		if (h < 0) {
			damage(e, -h);
			return;
		}
		setHealth(e, getHealth(e) + h);
	}

	public static double getDamage(EntityDamageEvent e) {
		return e.getDamage();
	}

	public static double getFinalDamage(EntityDamageEvent e) {
		return e.getFinalDamage();
	}

	public static void setDamage(EntityDamageEvent event, double damage) {
		event.setDamage(damage);
		// Set last damage manually as Bukkit doesn't appear to do that
		if (event.getEntity() instanceof LivingEntity)
			((LivingEntity) event.getEntity()).setLastDamage(damage);
	}

	@Nullable
	private static final Constructor<EntityDamageEvent> OLD_DAMAGE_EVENT_CONSTRUCTOR;

	static {
		Constructor<EntityDamageEvent> constructor = null;
		try {
			constructor = EntityDamageEvent.class.getConstructor(Damageable.class, DamageCause.class, double.class);
		} catch (NoSuchMethodException ignored) {
		}
		OLD_DAMAGE_EVENT_CONSTRUCTOR = constructor;
	}

	@SuppressWarnings({"removal", "UnstableApiUsage", "ThrowableNotThrown"})
	public static void setDamageCause(Damageable e, DamageCause cause) {
		if (OLD_DAMAGE_EVENT_CONSTRUCTOR != null) {
			try {
				e.setLastDamageCause(OLD_DAMAGE_EVENT_CONSTRUCTOR.newInstance(e, cause, 0));
			} catch (InstantiationException | IllegalAccessException |
					 InvocationTargetException ex) {
				Skript.exception("Failed to set last damage cause");
			}
		} else {
			e.setLastDamageCause(new EntityDamageEvent(e, cause, DamageSource.builder(DamageType.GENERIC).build(), 0));
		}
	}

}
