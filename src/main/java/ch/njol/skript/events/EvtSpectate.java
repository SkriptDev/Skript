package ch.njol.skript.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.coll.CollectionUtils;

public class EvtSpectate extends SkriptEvent {

	static {
		if (Skript.classExists("com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent"))
			Skript.registerEvent("Spectate", EvtSpectate.class, CollectionUtils.array(PlayerStartSpectatingEntityEvent.class, PlayerStopSpectatingEntityEvent.class),
						"[player] stop spectating [(of|from) %-*entitytypes%]",
						"[player] (swap|switch) spectating [(of|from) %-*entitytypes%]",
						"[player] start spectating [of %-*entitytypes%]")
					.description("Called with a player starts, stops or swaps spectating an entity.")
					.examples("on player start spectating of a zombie:")
					.requiredPlugins("Paper")
					.since("2.7");
	}

	private Literal<EntityType> entityTypes;

	private static final int STOP = -1, SWAP = 0, START = 1;

	/**
	 * 1 = swap. When the player did have a past spectating target.
	 * 0 = start. When the player starts spectating a new target.
	 * -1 = stop. When the player stops spectating a target.
	 */
	private int pattern;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
		pattern = matchedPattern - 1;
		entityTypes = (Literal<EntityType>) args[0];
		return true;
	}

	@Override
	public boolean check(Event event) {
		boolean swap = false;
		Entity entity;
		// Start or swap event, and must be PlayerStartSpectatingEntityEvent.
		if (pattern != STOP && event instanceof PlayerStartSpectatingEntityEvent) {
			PlayerStartSpectatingEntityEvent spectating = (PlayerStartSpectatingEntityEvent) event;
			entity = spectating.getNewSpectatorTarget();

			// If it's a swap event, we're checking for past target on entity data and no null targets in the event.
			if (swap = pattern == SWAP && entity != null && spectating.getCurrentSpectatorTarget() != null)
				entity = spectating.getCurrentSpectatorTarget();
		} else if (event instanceof PlayerStopSpectatingEntityEvent) {
			entity = ((PlayerStopSpectatingEntityEvent) event).getSpectatorTarget();
		} else {
			// Swap event cannot be a stop spectating event.
			return false;
		}
		// Wasn't a swap event.
		if (pattern == SWAP && !swap)
			return false;
		if (entityTypes == null)
			return true;
		for (EntityType entityType : this.entityTypes.getAll(event)) {
			if (entityType == entity.getType())
				return true;
		}
		return false;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return (pattern == START ? "start" : pattern == SWAP ? "swap" : "stop") + " spectating" +
					(entityTypes != null ? "of " + entityTypes.toString(event, debug) : "");
	}

}
