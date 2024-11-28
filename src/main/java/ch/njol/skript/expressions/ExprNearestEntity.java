package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Utils;
import ch.njol.util.Kleenean;
import ch.njol.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import ch.njol.skript.lang.util.SimpleExpression;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;

@Name("Nearest Entity")
@Description("Gets the entity nearest to a location or another entity.")
@Examples({
	"kill the nearest pig and cow relative to player",
	"teleport player to the nearest cow relative to player",
	"teleport player to the nearest entity relative to player",
	"",
	"on click:",
	"\tkill nearest pig"
})
@Since("2.7")
public class ExprNearestEntity extends SimpleExpression<Entity> {

	static {
		Skript.registerExpression(ExprNearestEntity.class, Entity.class, ExpressionType.COMBINED,
				"[the] nearest %*entitytypes% [[relative] to %entity/location%]",
				"[the] %*entitytypes% nearest [to %entity/location%]",
			"[the] nearest entity [[relative] to %entity/location%]",
			"[the] entity nearest [to %entity/location%]");
	}

	@SuppressWarnings("NotNullFieldNotInitialized")
	private EntityType[] entityTypes;

	@SuppressWarnings("NotNullFieldNotInitialized")
	private Expression<?> relativeTo;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (matchedPattern > 1) {
			entityTypes = new EntityType[1];
			entityTypes[0] = null;
		} else {
			entityTypes = ((Literal<EntityType>) exprs[0]).getArray();
		}
		if (entityTypes.length != Arrays.stream(entityTypes).distinct().count()) {
			Skript.error("Entity list may not contain duplicate entities");
			return false;
		}
		relativeTo = exprs[matchedPattern > 1 ? 0 : 1];
		return true;
	}

	@Override
	protected Entity[] get(Event event) {
		Object relativeTo = this.relativeTo.getSingle(event);
		if (relativeTo == null || (relativeTo instanceof Location && ((Location) relativeTo).getWorld() == null))
			return (Entity[]) Array.newInstance(this.getReturnType(), 0);;
		Entity[] nearestEntities = (Entity[]) Array.newInstance(this.getReturnType(), entityTypes.length);
		for (int i = 0; i < nearestEntities.length; i++) {
			if (relativeTo instanceof Entity) {
				nearestEntities[i] = getNearestEntity(entityTypes[i], ((Entity) relativeTo).getLocation(), (Entity) relativeTo);
			} else {
				nearestEntities[i] = getNearestEntity(entityTypes[i], (Location) relativeTo, null);
			}
		}
		return nearestEntities;
	}

	@Override
	public boolean isSingle() {
		return entityTypes.length == 1;
	}

	private transient @Nullable Class<? extends Entity> knownReturnType;

	@Override
	public Class<? extends Entity> getReturnType() {
		if (knownReturnType != null)
			return knownReturnType;
		if (entityTypes.length == 1 && entityTypes[0] == null) return Entity.class;
		Class<? extends Entity>[] types = new Class[entityTypes.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = entityTypes[i].getEntityClass();
		}
		return knownReturnType = Utils.highestDenominator(Entity.class, types);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "nearest " + StringUtils.join(entityTypes) + " relative to " + relativeTo.toString(event, debug);
	}

	@Nullable
	private Entity getNearestEntity(EntityType entityType, Location relativePoint, @Nullable Entity excludedEntity) {
		Entity nearestEntity = null;
		double nearestDistance = -1;
		if (entityType == null) {
			// TODO this is an awful way, gonna fix later
			for (Entity entity : relativePoint.getWorld().getEntities()) {
				if (entity != excludedEntity) {
					double distance = entity.getLocation().distance(relativePoint);
					if (nearestEntity == null || distance < nearestDistance) {
						nearestDistance = distance;
						nearestEntity = entity;
					}
				}
			}
		} else if (entityType.getEntityClass() != null) {
			for (Entity entity : relativePoint.getWorld().getEntitiesByClass(entityType.getEntityClass())) {
				if (entity != excludedEntity && entityType == entity.getType()) {
					double distance = entity.getLocation().distance(relativePoint);
					if (nearestEntity == null || distance < nearestDistance) {
						nearestDistance = distance;
						nearestEntity = entity;
					}
				}
			}
		}
		return nearestEntity;
	}

}
