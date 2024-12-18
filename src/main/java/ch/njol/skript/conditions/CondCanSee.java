package ch.njol.skript.conditions;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.conditions.base.PropertyCondition.PropertyType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

@Name("Can See")
@Description("Checks whether the given players can see another players.")
@Examples({"if the player can't see the player-argument:",
		"\tmessage \"&lt;light red&gt;The player %player-argument% is not online!\""})
@Since("2.3")
public class CondCanSee extends Condition {

	static {
		Skript.registerCondition(CondCanSee.class,
				"%players% (is|are) [(1¦in)]visible for %players%",
				"%players% can see %players%",
				"%players% (is|are)(n't| not) [(1¦in)]visible for %players%",
				"%players% can('t| not) see %players%");
	}
	
	@SuppressWarnings("null")
	private Expression<Player> players;
	@SuppressWarnings("null")
	private Expression<Player> targetPlayers;

	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (matchedPattern == 1 || matchedPattern == 3) {
			players = (Expression<Player>) exprs[0];
			targetPlayers = (Expression<Player>) exprs[1];
		} else {
			players = (Expression<Player>) exprs[1];
			targetPlayers = (Expression<Player>) exprs[0];
		}
		setNegated(matchedPattern > 1 ^ parseResult.mark == 1);
		return true;
	}

	@Override
	public boolean check(Event e) {
		return players.check(e,
				player -> targetPlayers.check(e,
						player::canSee
				), isNegated());
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return PropertyCondition.toString(this, PropertyType.CAN, e, debug, players,
				"see" + targetPlayers.toString(e, debug));
	}

}
