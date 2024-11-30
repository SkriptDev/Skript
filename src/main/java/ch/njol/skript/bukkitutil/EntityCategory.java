package ch.njol.skript.bukkitutil;

import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.util.StringUtils;
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
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
	BOSS(Boss.class, "bosses"),
	BUCKETABLE(Bucketable.class),
	BREEDABLE(Breedable.class),
	CHEST_BOAT(ChestBoat.class),
	CREATURE(Creature.class),
	DAMAGEABLE(Damageable.class),
	DISPLAY(Display.class),
	ENEMY(Enemy.class, "enemies"),
	ENTITY(Entity.class, "entities"),
	EXPLOSIVE(Explosive.class),
	FISH(Fish.class, "fish"),
	FLYING(Flying.class, "flying"),
	GOLEM(Golem.class),
	HANGING(Hanging.class, "hanging"),
	ILLAGER(Illager.class),
	MINECART(Minecart.class),
	MOB(Mob.class),
	MONSTER(Monster.class),
	PROJECTILE(Projectile.class),
	RAIDER(Raider.class),
	SCHOOLABLE_FISH(SchoolableFish.class, "schoolable fish"),
	SHEARABLE(Shearable.class),
	SPELLCASTER(Spellcaster.class),
	TAMEABLE(Tameable.class),
	THROWABLE_PROJECTILE(ThrowableProjectile.class),
	VEHICLE(Vehicle.class),
	WATER_MOB(WaterMob.class),
	;

	private final Class<? extends Entity> entityClass;
	private final String plural;

	EntityCategory(Class<? extends Entity> entityClass) {
		this.entityClass = entityClass;
		String name = name().toLowerCase(Locale.ENGLISH);
		this.plural = name + "s";
	}

	EntityCategory(Class<? extends Entity> entityClass, String plural) {
		this.entityClass = entityClass;
		this.plural = plural;
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

	public String getName() {
		return name().toLowerCase(Locale.ENGLISH).replaceAll("_", " ");
	}

	/**
	 * @return names
	 * @hidden
	 */
	// STATIC STUFF FOR SKRIPT
	public static String getAllNames() {
		return StringUtils.join(Arrays.stream(values()).map(EntityCategory::getName).toList(), ", ");
	}

	private static Map<String, EntityCategory> parseMap = new HashMap<>();

	static {
		for (EntityCategory category : values()) {
			String name = category.getName().toLowerCase(Locale.ENGLISH).replaceAll("_", " ");
			parseMap.put(name, category);
			parseMap.put(category.plural, category);
		}
	}

	/**
	 * @return parser
	 * @hidden
	 */
	public static Parser<EntityCategory> getParser() {
		return new Parser<>() {
			@Override
			public @Nullable EntityCategory parse(String string, ParseContext context) {
				if (string.startsWith("a ")) string = string.substring(2);
				if (string.startsWith("an ")) string = string.substring(3);
				return parseMap.get(string.toLowerCase(Locale.ENGLISH));
			}

			@Override
			public String toString(EntityCategory category, int flags) {
				return category.getName();
			}

			@Override
			public String toVariableNameString(EntityCategory category) {
				return "entity_category:" + category.getName();
			}
		};
	}

}
