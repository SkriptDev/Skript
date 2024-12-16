package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

/**
 * @author Peter Güttinger
 */
@Name("Tree")
@Description({"Creates a tree.",
	"This may require that there is enough space above the given location and that the block below is dirt/grass",
	"but it is possible that the tree will just grow anyways, possibly replacing every block in its path."})
@Examples({"grow a tall redwood tree above the clicked block"})
@Since("1.0")
public class EffTree extends Effect {

	static {
		Skript.registerEffect(EffTree.class,
			"(grow|create|generate) tree [of type %treetype%] %directions% %locations%",
			"(grow|create|generate) %treetype% %directions% %locations%");
	}

	@SuppressWarnings("null")
	private Expression<Location> locations;
	@SuppressWarnings("null")
	private Expression<TreeType> type;

	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parser) {
		type = (Expression<TreeType>) exprs[0];
		locations = Direction.combine((Expression<? extends Direction>) exprs[1], (Expression<? extends Location>) exprs[2]);
		return true;
	}

	@Override
	public void execute(final Event e) {
		final TreeType type = this.type.getSingle(e);
		if (type == null)
			return;
		for (final Location location : locations.getArray(e)) {
			World world = location.getWorld();
			if (world == null) continue;

			world.generateTree(location, type);
		}
	}

	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "grow tree of type " + type.toString(e, debug) + " " + locations.toString(e, debug);
	}

}
