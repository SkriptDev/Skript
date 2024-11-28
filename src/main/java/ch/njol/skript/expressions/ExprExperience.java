package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Events;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.events.bukkit.ExperienceSpawnEvent;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.jetbrains.annotations.Nullable;

/**
 * @author Peter Güttinger
 */
@Name("Experience")
@Description("How much experience was spawned in an experience spawn or block break event. Can be changed.")
@Examples({"on experience spawn:",
	"\tadd 5 to the spawned experience",
	"on break of coal ore:",
	"\tclear dropped experience",
	"on break of diamond ore:",
	"\tif tool of player = diamond pickaxe:",
	"\t\tadd 100 to dropped experience"})
@Since("2.1, 2.5.3 (block break event), 2.7 (experience change event)")
@Events({"experience spawn", "break / mine", "experience change"})
public class ExprExperience extends SimpleExpression<Number> {
	static {
		Skript.registerExpression(ExprExperience.class, Number.class, ExpressionType.SIMPLE, "[the] (spawned|dropped|) [e]xp[erience] [orb[s]]");
	}

	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		if (!getParser().isCurrentEvent(ExperienceSpawnEvent.class, BlockBreakEvent.class, PlayerExpChangeEvent.class)) {
			Skript.error("The experience expression can only be used in experience spawn, block break and player experience change events");
			return false;
		}
		return true;
	}

	@Override
	@Nullable
	protected Number[] get(final Event e) {
		if (e instanceof ExperienceSpawnEvent experienceSpawnEvent)
			return new Number[]{experienceSpawnEvent.getSpawnedXP()};
		else if (e instanceof BlockBreakEvent blockBreakEvent)
			return new Number[]{blockBreakEvent.getExpToDrop()};
		else if (e instanceof PlayerExpChangeEvent playerExpChangeEvent)
			return new Number[]{playerExpChangeEvent.getAmount()};
		else
			return new Number[0];
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(final ChangeMode mode) {
		return switch (mode) {
			case SET, ADD, DELETE, REMOVE, REMOVE_ALL ->
				new Class[]{Number[].class};
			case RESET -> null;
		};
	}

	@Override
	public void change(final Event e, final @Nullable Object[] delta, final ChangeMode mode) {
		double eventExp;
		if (e instanceof ExperienceSpawnEvent experienceSpawnEvent) {
			eventExp = experienceSpawnEvent.getSpawnedXP();
		} else if (e instanceof BlockBreakEvent blockBreakEvent) {
			eventExp = blockBreakEvent.getExpToDrop();
		} else if (e instanceof PlayerExpChangeEvent playerExpChangeEvent) {
			eventExp = playerExpChangeEvent.getAmount();
		} else {
			return;
		}
		if (delta == null) {
			eventExp = 0;
		} else {
			for (Object obj : delta) {
				double value = ((Number) obj).doubleValue();
				switch (mode) {
					case ADD:
						eventExp += value;
						break;
					case SET:
						eventExp = value;
						break;
					case REMOVE:
					case REMOVE_ALL:
						eventExp -= value;
						break;
					case RESET:
					case DELETE:
						assert false;
						break;
				}
			}
		}


		eventExp = Math.max(0, Math.round(eventExp));
		int roundedEventExp = (int) eventExp;
		if (e instanceof ExperienceSpawnEvent experienceSpawnEvent) {
			experienceSpawnEvent.setSpawnedXP(roundedEventExp);
		} else if (e instanceof BlockBreakEvent blockBreakEvent) {
			blockBreakEvent.setExpToDrop(roundedEventExp);
		} else {
			((PlayerExpChangeEvent) e).setAmount(roundedEventExp);
		}
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "the experience";
	}

}
