package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.log.RetainingLogHandler;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

/**
 * @author Peter Güttinger
 */
@Name("Creature/Entity/Player/Projectile/Villager/Powered Creeper/etc.")
@Description({"The entity involved in an event (an entity is a player, a creature or an inanimate object like ignited TNT, a dropped item or an arrow).",
	"You can use the specific type of the entity that's involved in the event, e.g. in a 'death of a creeper' event you can use 'the creeper' instead of 'the entity'."})
@Examples({"give a diamond sword of sharpness 3 to the player",
	"kill the creeper",
	"kill all powered creepers in the wolf's world",
	"projectile is an arrow"})
@Since("1.0")
public class ExprEntity extends SimpleExpression<Entity> {
	static {
		// TODO need to figure this out (having "event" optional just makes a crap load of conflicts)
		Skript.registerExpression(ExprEntity.class, Entity.class, ExpressionType.PATTERN_MATCHES_EVERYTHING,
			"[the] event-<.+>", "[the] (entity|p:player)");
	}

	@SuppressWarnings("null")
	private EntityType type;

	@SuppressWarnings("null")
	private EventValueExpression<Entity> entity;

	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		if (matchedPattern == 1) {
			if (parseResult.hasTag("player")) {
				type = EntityType.PLAYER;
				entity = new EventValueExpression<>(Player.class);
			} else {
				entity = new EventValueExpression<>(Entity.class);
			}
			return entity.init();
		}
		String pattern = parseResult.regexes.get(0).group();
		if (pattern.equalsIgnoreCase("entity")) {
			entity = new EventValueExpression<>(Entity.class);
			return entity.init();
		}
		final RetainingLogHandler log = SkriptLogger.startRetainingLog();
		try {

			final EntityType type = Classes.parse(pattern, EntityType.class, ParseContext.DEFAULT);
			log.clear();
			log.printLog();
			if (type == null)
				return false;
			this.type = type;
		} finally {
			log.stop();
		}
		entity = new EventValueExpression<>(type.getEntityClass());
		return entity.init();
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Entity> getReturnType() {
		if (type == null) return Entity.class;
		return type.getEntityClass();
	}

	@Override
	@Nullable
	protected Entity[] get(final Event e) {
		final Entity[] es = entity.getArray(e);
		if (es.length == 0 || type == null || type == (es[0]).getType())
			return es;
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public <R> Expression<? extends R> getConvertedExpression(Class<R>... to) {
		for (Class<R> t : to) {
			if (t.equals(EntityType.class)) {
				return new SimpleLiteral<>((R) type, false);
			}
		}
		return super.getConvertedExpression(to);
	}

	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "the " + type;
	}

}
