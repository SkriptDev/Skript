package ch.njol.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.EntityCategory;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.StringUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.jetbrains.annotations.Nullable;

/**
 * @author Peter Güttinger
 */
public final class EvtEntity extends SkriptEvent {

	static {
		Skript.registerEvent("Death", EvtEntity.class, EntityDeathEvent.class, "death [of %-entitytypes/entitycategories%]")
			.description("Called when a living entity (including players) dies.")
			.examples("on death:",
				"on death of player:",
				"on death of a wither or ender dragon:",
				"	broadcast \"A %entity% has been slain in %world%!\"")
			.since("1.0");
		Skript.registerEvent("Spawn", EvtEntity.class, EntitySpawnEvent.class, "spawn[ing] [of %-entitytypes/entitycategories%]")
			.description("Called when an entity spawns (excluding players).")
			.examples("on spawn of a zombie:",
				"on spawn of an ender dragon:",
				"	broadcast \"A dragon has been sighted in %world%!\"")
			.since("1.0, 2.5.1 (non-living entities)");
	}

	@Nullable
	private Object[] types;

	private boolean spawn;

	@SuppressWarnings("null")
	@Override
	public boolean init(final Literal<?>[] args, final int matchedPattern, final ParseResult parser) {
		types = args[0] == null ? null : (args[0]).getAll();
		spawn = StringUtils.startsWithIgnoreCase(parser.expr, "spawn");
		if (types != null) {
			if (spawn) {
				for (final Object type : types) {
					if (type instanceof EntityType entityType && entityType == EntityType.PLAYER) {
						Skript.error("The spawn event does not work for players", ErrorQuality.SEMANTIC_ERROR);
						return false;
					}
				}
			} else {
				for (final Object object : types) {
					if (object instanceof EntityType entityType && !entityType.isAlive()) {
						Skript.error("The death event only works for living entities", ErrorQuality.SEMANTIC_ERROR);
						return false;
					}
				}
			}
		}
		return true;
	}

	@SuppressWarnings("null")
	@Override
	public boolean check(final Event e) {
		if (types == null)
			return true;
		final Entity en = e instanceof EntityDeathEvent ? ((EntityDeathEvent) e).getEntity() : ((EntitySpawnEvent) e).getEntity();
		for (final Object type : types) {
			if (type instanceof EntityType entityType && entityType == en.getType())
				return true;
			else if (type instanceof EntityCategory entityCategory && entityCategory.isOfType(en))
				return true;
		}
		return false;
	}

	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return (spawn ? "spawn" : "death") + (types != null ? " of " + Classes.toString(types, false) : "");
	}

}
