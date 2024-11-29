package ch.njol.skript.bukkitutil;

import io.papermc.paper.entity.Bucketable;
import io.papermc.paper.entity.SchoolableFish;
import io.papermc.paper.entity.Shearable;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.ChestBoat;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Display;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Flying;
import org.bukkit.entity.Golem;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Illager;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Raider;
import org.bukkit.entity.Spellcaster;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.WaterMob;

/**
 * Categories for different types of {@link Entity Entities}
 */
@SuppressWarnings("unused")
public enum EntityCategory {

	ABSTRACT_ARROW(AbstractArrow.class),
	AGEABLE(Ageable.class),
	AMBIENT(Ambient.class),
	ANIMAL(Animals.class),
	BOAT(Boat.class),
	BOSS(Boss.class),
	BUCKETABLE(Bucketable.class),
	BREEDABLE(Breedable.class),
	CHEST_BOAT(ChestBoat.class),
	CREATURE(Creature.class),
	DAMAGEABLE(Damageable.class),
	DISPLAY(Display.class),
	ENEMY(Enemy.class),
	ENTITY(Entity.class),
	EXPLOSIVE(Explosive.class),
	FISH(Fish.class),
	FLYING(Flying.class),
	GOLEM(Golem.class),
	HANGING(Hanging.class),
	ILLAGER(Illager.class),
	MINECART(Minecart.class),
	MOB(Mob.class),
	MONSTER(Monster.class),
	PROJECTILE(Projectile.class),
	RAIDER(Raider.class),
	SCHOOLABLE_FISH(SchoolableFish.class),
	SHEARABLE(Shearable.class),
	SPELLCASTER(Spellcaster.class),
	TAMEABLE(Tameable.class),
	THROWABLE_PROJECTILE(ThrowableProjectile.class),
	VEHICLE(Vehicle.class),
	WATER_MOB(WaterMob.class),
	;

	private final Class<? extends Entity> entityClass;

	EntityCategory(Class<? extends Entity> entityClass) {
		this.entityClass = entityClass;
	}

	/**
	 * Get the Entity class of this Category
	 *
	 * @return Entity class of this category
	 */
	public Class<? extends Entity> getEntityClass() {
		return this.entityClass;
	}

	/**
	 * Check if an {@link Entity} is an instance of this Category
	 *
	 * @param entity Entity to check
	 * @return True if entity is instance of this category
	 */
	public boolean isOfType(Entity entity) {
		return this.entityClass.isInstance(entity);
	}

	/**
	 * Check if an {@link EntityType} is an instance of this Category
	 *
	 * @param entityType EntityType to check
	 * @return True if entity is instance of this category
	 */
	public boolean isOfType(EntityType entityType) {
		return this.entityClass.isInstance(entityType.getEntityClass());
	}

}
