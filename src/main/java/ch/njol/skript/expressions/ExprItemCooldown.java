package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Item Cooldown")
@Description("Change the cooldown of a specific material to a certain amount of <a href='./classes.html#timespan'>Timespan</a>.")
@Examples({
	"on right click using stick:",
	"\tset item cooldown of player's tool for player to 1 minute",
	"\tset item cooldown of stone and grass for all players to 20 seconds",
	"\treset item cooldown of cobblestone and dirt for all players"
})
@Since("2.8.0")
public class ExprItemCooldown extends SimpleExpression<Timespan> {

	static {
		Skript.registerExpression(ExprItemCooldown.class, Timespan.class, ExpressionType.COMBINED,
			"[the] [item] cooldown of %materials% for %players%",
			"%players%'[s] [item] cooldown for %materials%");
	}

	@SuppressWarnings("NotNullFieldNotInitialized")
	private Expression<Player> players;
	@SuppressWarnings("NotNullFieldNotInitialized")
	private Expression<Material> materials;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		players = (Expression<Player>) exprs[matchedPattern ^ 1];
		materials = (Expression<Material>) exprs[matchedPattern];
		return true;
	}

	@Override
	protected Timespan[] get(Event event) {
		Player[] players = this.players.getArray(event);

		Timespan[] timespan = new Timespan[players.length * this.materials.getArray(event).length];

		int i = 0;
		for (Player player : players) {
			for (Material material : this.materials.getArray(event)) {
				timespan[i++] = Timespan.fromTicks_i(player.getCooldown(material));
			}
		}
		return timespan;
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		return mode == ChangeMode.REMOVE_ALL ? null : CollectionUtils.array(Timespan.class);
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		if (mode != ChangeMode.RESET && mode != ChangeMode.DELETE && delta == null)
			return;

		int ticks = delta != null ? (int) ((Timespan) delta[0]).getTicks_i() : 0; // 0 for DELETE/RESET
		Player[] players = this.players.getArray(event);

		for (Player player : players) {
			for (Material material : this.materials.getArray(event)) {
				switch (mode) {
					case RESET:
					case DELETE:
					case SET:
						player.setCooldown(material, ticks);
						break;
					case REMOVE:
						player.setCooldown(material, Math.max(player.getCooldown(material) - ticks, 0));
						break;
					case ADD:
						player.setCooldown(material, player.getCooldown(material) + ticks);
						break;
				}
			}
		}
	}

	@Override
	public boolean isSingle() {
		return players.isSingle() && materials.isSingle();
	}

	@Override
	public Class<? extends Timespan> getReturnType() {
		return Timespan.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "cooldown of " + materials.toString(event, debug) + " for " + players.toString(event, debug);
	}

}
