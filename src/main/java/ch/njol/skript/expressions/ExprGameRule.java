package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Gamerule Value")
@Description("The gamerule value of a world.")
@Examples({"set the gamerule commandBlockOutput of world \"world\" to false",
	"set gamerule doDaylightCycle of all worlds to false"})
@Since("2.5")
public class ExprGameRule extends SimpleExpression<Object> {

	static {
		if (Skript.classExists("org.bukkit.GameRule")) {
			Skript.registerExpression(ExprGameRule.class, Object.class, ExpressionType.COMBINED,
				"[the] gamerule %*gamerule% of %worlds%");
		}
	}

	private Literal<GameRule<?>> gamerule;
	private Expression<World> worlds;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		gamerule = (Literal<GameRule<?>>) exprs[0];
		worlds = (Expression<World>) exprs[1];
		return true;
	}

	@Nullable
	@Override
	protected Object[] get(Event event) {
		GameRule<?> gameRule = this.gamerule.getSingle();
		if (gameRule == null)
			return null;

		List<Object> values = new ArrayList<>();
		for (World world : this.worlds.getArray(event)) {
			values.add(world.getGameRuleValue(gameRule));
		}
		return values.toArray();
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(Boolean.class, Number.class);
		return null;
	}

	@SuppressWarnings({"rawtypes", "unchecked", "DataFlowIssue"})
	@Override
	public void change(final Event event, final @Nullable Object[] delta, final ChangeMode mode) {
		assert delta != null;
		if (mode == ChangeMode.SET) {
			GameRule gameRule = this.gamerule.getSingle();
			if (gameRule == null)
				return;

			for (World world : worlds.getArray(event)) {
				world.setGameRule(gameRule, delta[0]);
			}
		}
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<?> getReturnType() {
		return this.gamerule.getSingle().getType();
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "the gamerule value of " + gamerule.toString(e, debug) + " for world " + worlds.toString(e, debug);
	}
}
