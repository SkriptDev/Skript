package ch.njol.skript.conditions;

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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Has Item Cooldown")
@Description("Check whether a cooldown is active on the specified material for a specific player.")
@Examples({
	"if player has player's tool on cooldown:",
	"\tsend \"You can't use this item right now. Wait %item cooldown of player's tool for player%\""
}) // TODO fix docs
@Since("2.8.0")
public class CondHasItemCooldown extends Condition {

	static {
		Skript.registerCondition(CondHasItemCooldown.class,
			"%players% (has|have) [([an] item|a)] cooldown (on|for) %materials%",
			"%players% (has|have) %materials% on [(item|a)] cooldown",
			"%players% (doesn't|does not|do not|don't) have [([an] item|a)] cooldown (on|for) %materials%",
			"%players% (doesn't|does not|do not|don't) have %materials% on [(item|a)] cooldown");
	}

	private Expression<Player> players;
	private Expression<Material> materials;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		players = (Expression<Player>) exprs[0];
		materials = (Expression<Material>) exprs[1];
		setNegated(matchedPattern > 1);
		return true;
	}

	@Override
	public boolean check(Event event) {
		return players.check(event, (player) ->
			materials.check(event, player::hasCooldown)
		);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return PropertyCondition.toString(this, PropertyType.HAVE, event, debug, players,
			materials.toString(event, debug) + " on cooldown");
	}

}
