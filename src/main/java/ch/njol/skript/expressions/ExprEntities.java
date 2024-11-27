package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.EntityCategory;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Name("Entities")
@Description("All entities in all worlds, in a specific world, in a chunk or in a radius around a certain location, " +
	"e.g. <code>all players</code>, <code>all creepers in the player's world</code>, or <code>players in radius 100 of the player</code>.")
@Examples({"kill all creepers in the player's world",
	"send \"Psst!\" to all players within 100 meters of the player",
	"give a diamond to all ops",
	"heal all tamed wolves in radius 2000 around {town center}",
	"delete all monsters in chunk at player"})
@Since("1.2.1, 2.5 (chunks)")
public class ExprEntities extends SimpleExpression<Entity> {

	static {
		Skript.registerExpression(ExprEntities.class, Entity.class, ExpressionType.PATTERN_MATCHES_EVERYTHING,
			"[(all [[of] the]|the)] entities [(in|of) ([world[s]] %-worlds%|1¦%-chunks%)]",
			"[(all [[of] the]|the)] entities of type[s] %*-entitytypes/entitycategories% [(in|of) ([world[s]] %-worlds%|1¦%-chunks%)]",
			"[(all [[of] the]|the)] entities (within|[with]in radius) %number% [(block[s]|met(er|re)[s])] (of|around) %location%",
			"[(all [[of] the]|the)] entities of type[s] %-entitytypes/entitycategories% in radius %number% (of|around) %location%");
	}

	@SuppressWarnings("null")
	Expression<?> types;

	@Nullable
	private Expression<World> worlds;
	@Nullable
	private Expression<Chunk> chunks;
	@Nullable
	private Expression<Number> radius;
	@Nullable
	private Expression<Location> center;

	private boolean isUsingRadius;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (matchedPattern == 1 || matchedPattern == 3) {
			types = LiteralUtils.defendExpression(exprs[0]);
		}
		isUsingRadius = matchedPattern >= 2;
		if (isUsingRadius) {
			radius = (Expression<Number>) exprs[matchedPattern - 2];
			center = (Expression<Location>) exprs[matchedPattern - 1];
		} else {
			if (parseResult.mark == 1) {
				chunks = (Expression<Chunk>) exprs[matchedPattern];
			} else {
				worlds = (Expression<World>) exprs[matchedPattern];
			}
		}
		return true;
	}

	@Override
	@Nullable
	@SuppressWarnings("null")
	protected Entity[] get(Event e) {
		if (isUsingRadius) {
			if (this.center == null || this.radius == null) return null;

			Object[] types = this.types.getArray(e);

			Location center = this.center.getSingle(e);
			Number radiusNum = this.radius.getSingle(e);
			if (center == null || radiusNum == null) return null;

			int radius = radiusNum.intValue();
			World world = center.getWorld();
			if (world == null) return null;

			int radiusSquared = radius * radius;
			List<Entity> entities = new ArrayList<>();
			for (Entity entity : center.getNearbyEntities(radius, radius, radius)) {
				if (entity.getLocation().distanceSquared(center) <= radiusSquared) {
					if (isOfType(entity, types)) entities.add(entity);
				}
			}
			return entities.toArray(new Entity[0]);
		} else {
			List<Entity> entities = new ArrayList<>();
			if (this.chunks != null) {
				for (Chunk chunk : this.chunks.getArray(e)) {
					for (@NotNull Entity entity : chunk.getEntities()) {
						if (this.types != null) {
							if (isOfType(entity, this.types.getArray(e))) entities.add(entity);
						} else {
							entities.add(entity);
						}
					}
				}
			} else {
				List<World> worlds = this.worlds != null ? Arrays.asList(this.worlds.getArray(e)) : Bukkit.getWorlds();
				for (World world : worlds) {
					for (@NotNull Entity entity : world.getEntities()) {
						if (this.types != null) {
							if (isOfType(entity, this.types.getArray(e))) entities.add(entity);
						} else {
							entities.add(entity);
						}
					}
				}
			}
			return entities.toArray(new Entity[0]);
		}
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends Entity> getReturnType() {
		return Entity.class;
	}

	@Override
	@SuppressWarnings("null")
	public String toString(@Nullable Event e, boolean debug) {
		String types = this.types != null ? "of type " + this.types.toString(e, debug) : "";
		String worlds = this.worlds != null ? " in " + this.worlds.toString(e, debug) : this.chunks != null ? " in " + this.chunks.toString(e, debug) : null;
		return "all entities " + types + (worlds != null ? worlds :
			radius != null && center != null ? " in radius " + radius.toString(e, debug) + " around " + center.toString(e, debug) : "");
	}

	private boolean isOfType(Entity entity, Object[] types) {
		if (types != null) {
			for (Object type : types) {
				if (type instanceof EntityType entityType) {
					if (entityType == entity.getType()) return true;
				} else if (type instanceof EntityCategory entityCategory) {
					if (entityCategory.isOfType(entity)) return true;
				}
			}
			return false;
		}
		return true;
	}

}
