package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.HealthUtils;
import ch.njol.skript.bukkitutil.ItemUtils;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.Math2;
import org.bukkit.entity.Damageable;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("Damage/Heal/Repair")
@Description({"Damage/Heal/Repair an entity, or item.",
	"Damage/healing will be referenced by full health amount.",
	"If using optional 'hearts', the amount will be cut in half to represent a heart."})
@Examples({"damage player by 5 hearts",
	"damage all mobs by 10",
	"heal the player",
	"repair tool of player"})
@Since("1.0")
public class EffHealth extends Effect {

	static {
		Skript.registerEffect(EffHealth.class,
			"damage %livingentities/itemstacks% by %number% [heart:heart[s]]",
			"heal %livingentities% [by %-number% [heart:heart[s]]]",
			"repair %itemstacks% [by %-number%]");
	}

	private Expression<?> damageables;
	@Nullable
	private Expression<Number> amount;
	private boolean isHealing, isRepairing;
	private boolean hearts;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		this.damageables = exprs[0];
		this.isHealing = matchedPattern >= 1;
		this.isRepairing = matchedPattern == 2;
		this.amount = (Expression<Number>) exprs[1];
		this.hearts = parseResult.hasTag("heart");
		return true;
	}

	@Override
	protected void execute(Event event) {
		double amount = 0;
		if (this.amount != null) {
			Number amountPostCheck = this.amount.getSingle(event);
			if (amountPostCheck == null)
				return;
			amount = amountPostCheck.doubleValue();
		}

		if (hearts) amount /= 2;

		for (Object obj : this.damageables.getArray(event)) {
			if (obj instanceof ItemStack itemStack) {

				if (this.amount == null) {
					ItemUtils.setDamage(itemStack, 0);
				} else {
					ItemUtils.setDamage(itemStack, (int) Math2.fit(0, (ItemUtils.getDamage(itemStack) + (isHealing ? -amount : amount)), ItemUtils.getMaxDamage(itemStack)));
				}

			}
			if (obj instanceof Damageable damageable) {

				if (this.amount == null) {
					HealthUtils.heal(damageable, HealthUtils.getMaxHealth(damageable));
				} else if (isHealing) {
					HealthUtils.heal(damageable, amount);
				} else {
					HealthUtils.damage(damageable, amount);
				}

			}
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {

		String prefix = "damage ";
		if (isRepairing) {
			prefix = "repair ";
		} else if (isHealing) {
			prefix = "heal ";
		}

		return prefix + damageables.toString(event, debug) + (amount != null ? " by " + amount.toString(event, debug) : "");
	}

}
