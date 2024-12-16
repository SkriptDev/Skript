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
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Charge Creeper")
@Description("Charges or uncharges a creeper. A creeper is charged when it has been struck by lightning.")
@Examples({"on spawn of creeper:", 
			"\tcharge the event-entity"})
@Since("2.5")
public class EffChargeCreeper extends Effect {

	static {
		Skript.registerEffect(EffChargeCreeper.class,
				"make %livingentities% [a[n]] (charged|powered|1¦((un|non[-])charged|(un|non[-])powered)) [creeper[s]]",
				"(charge|power|1¦(uncharge|unpower)) %livingentities%");
	}

	@SuppressWarnings("null")
	private Expression<LivingEntity> entities;

	private boolean charge;

	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		entities = (Expression<LivingEntity>) exprs[0];
		charge = parseResult.mark != 1;
		return true;
	}

	@Override
	protected void execute(Event e) {
		for (Entity le : entities.getArray(e)) {
			if (le instanceof Creeper)
				((Creeper) le).setPowered(charge);
		}
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "make " + entities.toString(e, debug) + (charge == true ? " charged" : " not charged");
	}
}
