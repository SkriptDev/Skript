package ch.njol.skript.conditions;

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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("Is Enchanted")
@Description("Checks whether an item is enchanted.")
@Examples({"tool of the player is enchanted with efficiency",
	"tool of player is not enchanted"})
@Since("1.4.6")
public class CondIsEnchanted extends Condition {

	static {
		PropertyCondition.register(CondIsEnchanted.class, "enchanted [with %-enchantments%]", "itemstacks");
	}

	private Expression<ItemStack> itemStacks;
	@Nullable
	private Expression<Enchantment> enchantments;

	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		itemStacks = (Expression<ItemStack>) exprs[0];
		enchantments = (Expression<Enchantment>) exprs[1];
		setNegated(matchedPattern == 1);
		return true;
	}

	@Override
	public boolean check(final Event e) {
		if (enchantments != null)
			return itemStacks.check(e, item -> enchantments.check(e, item::containsEnchantment), isNegated());
		else
			return itemStacks.check(e, itemStack -> !itemStack.getEnchantments().isEmpty(), isNegated());

	}

	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		final Expression<Enchantment> es = this.enchantments;
		return PropertyCondition.toString(this, PropertyType.BE, e, debug, itemStacks,
			"enchanted" + (es == null ? "" : " with " + es.toString(e, debug)));
	}

}
