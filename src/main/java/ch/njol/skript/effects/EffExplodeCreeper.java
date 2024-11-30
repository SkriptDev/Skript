package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Explode Creeper")
@Description("Starts the explosion process of a creeper or instantly explodes it.")
@Examples({"start explosion of the last spawned creeper",
	"stop ignition of the last spawned creeper"})
@Since("2.5")
public class EffExplodeCreeper extends Effect {

	static {
		Skript.registerEffect(EffExplodeCreeper.class,
			"instantly explode [creeper[s]] %livingentities%",
			"explode [creeper[s]] %livingentities% instantly",
			"ignite creeper[s] %livingentities%",
			"start (ignition|explosion) [process] of [creeper[s]] %livingentities%",
			"stop (ignition|explosion) [process] of [creeper[s]] %livingentities%");
	}

	@SuppressWarnings("null")
	private Expression<Entity> entities;

	private boolean instant;

	private boolean stop;

	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		entities = (Expression<Entity>) exprs[0];
		instant = matchedPattern == 0;
		stop = matchedPattern == 4;
		return true;
	}

	@Override
	protected void execute(final Event e) {
		for (final Entity le : entities.getArray(e)) {
			if (le instanceof Creeper creeper) {
				if (instant) {
					creeper.explode();
				} else
					creeper.setIgnited(!stop);
			}
		}
	}

	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return (instant ? "instantly explode " : "start the explosion process of ") + entities.toString(e, debug);
	}

}
