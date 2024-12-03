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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

/**
 * @author Peter Güttinger
 */
@Name("Enchant/Disenchant")
@Description("Enchant or disenchant an existing item.")
@Examples({"enchant the player's tool with sharpness 5",
	"disenchant the player's tool"})
@Since("2.0")
public class EffEnchant extends Effect {
	static {
		Skript.registerEffect(EffEnchant.class,
			"enchant %~itemstacks% with %enchantment% [%-number%]",
			"disenchant %~itemstacks%");
	}

	@SuppressWarnings("null")
	private Expression<ItemStack> items;
	@Nullable
	private Expression<Enchantment> enchantment;
	private Expression<Number> level;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		this.items = (Expression<ItemStack>) exprs[0];
		if (matchedPattern == 0) {
			this.enchantment = (Expression<Enchantment>) exprs[1];
			this.level = (Expression<Number>) exprs[2];
		}
		return true;
	}

	@Override
	protected void execute(Event event) {
		ItemStack[] items = this.items.getArray(event);
		if (items.length == 0) // short circuit
			return;

		if (this.enchantment != null) {
			Enchantment enchantment = this.enchantment.getSingle(event);
			if (enchantment == null) return;

			Number levelNum = this.level != null ? this.level.getSingle(event) : 1;
			int level = levelNum != null ? levelNum.intValue() : 1;

			for (ItemStack item : items) {
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.addEnchant(enchantment, level, true);
				item.setItemMeta(itemMeta);
			}
		} else {
			for (ItemStack item : items) {
				item.removeEnchantments();
			}
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		if (this.enchantment != null) {
			String level = this.level != null ? " " + this.level.getSingle(event).toString() : "";
			return "enchant " + this.items.toString(event, debug) + " with " +
				this.enchantment.toString(event, debug) + level;
		}
		return "disenchant " + this.items.toString(event, debug);
	}

}
