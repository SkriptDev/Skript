package ch.njol.skript.expressions;

import java.util.List;
import java.util.ArrayList;
import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Hidden Players")
@Description({"The players hidden from a player that were hidden using the <a href='effects.html#EffPlayerVisibility'>player visibility</a> effect."})
@Examples({"message \"&lt;light red&gt;You are currently hiding: &lt;light gray&gt;%hidden players of the player%\""})
@Since("2.3")
public class ExprHiddenPlayers extends SimpleExpression<Player> {

	static {
		Skript.registerExpression(ExprHiddenPlayers.class, Player.class, ExpressionType.PROPERTY,
				"[(all [[of] the]|the)] hidden players (of|for) %players%",
				"[(all [[of] the]|the)] players hidden (from|for|by) %players%");
	}

	@SuppressWarnings("null")
	private Expression<Player> players;

	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		players = (Expression<Player>) exprs[0];
		return true;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	@Nullable
	public Player[] get(Event e) {
		List<Player> list = new ArrayList<>();
		for (Player player : players.getArray(e)) {
			list.addAll(player.spigot().getHiddenPlayers());
		}
		return list.toArray(new Player[list.size()]);
	}

	@Nullable
	public Expression<Player> getPlayers() {
		return players;
	}

	@Override
	public Class<? extends Player> getReturnType() {
		return Player.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "hidden players for " + players.toString(e, debug);
	}

}
