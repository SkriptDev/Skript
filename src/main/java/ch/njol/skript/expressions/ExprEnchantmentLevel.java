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
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Enchantment Level")
@Description("The level of a particular <a href='classes.html#enchantment'>enchantment</a> on an item.")
@Examples({"player's tool is a sword of sharpness:",
	"\tmessage \"You have a sword of sharpness %level of sharpness of the player's tool% equipped\""})
@Since("2.0")
public class ExprEnchantmentLevel extends SimpleExpression<Long> {

	static {
		Skript.registerExpression(ExprEnchantmentLevel.class, Long.class, ExpressionType.PROPERTY,
			"[the] [enchant[ment]] level[s] of %enchantments% (on|of) %itemstacks%",
			"[the] %enchantments% [enchant[ment]] level[s] (on|of) %itemstacks%",
			"%itemstacks%'[s] %enchantments% [enchant[ment]] level[s]",
			"%itemstacks%'[s] [enchant[ment]] level[s] of %enchantments%");
	}

	private Expression<ItemStack> items;
	private Expression<Enchantment> enchants;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		int i = matchedPattern < 2 ? 1 : 0;
		items = (Expression<ItemStack>) exprs[i];
		enchants = (Expression<Enchantment>) exprs[i ^ 1];
		return true;
	}

	@Override
	protected Long[] get(Event e) {
		List<Long> levels = new ArrayList<>();
		for (ItemStack itemStack : this.items.getArray(e)) {
			for (Enchantment enchantment : this.enchants.getArray(e)) {
				if (itemStack.containsEnchantment(enchantment)) {
					int level = itemStack.getEnchantmentLevel(enchantment);
					levels.add((long) level);
				}
			}
		}
		return levels.toArray(new Long[0]);
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case SET, REMOVE, ADD -> CollectionUtils.array(Number.class);
			default -> null;
		};
	}

	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		Enchantment[] enchantments = enchants.getArray(e);
		if (delta == null || delta.length == 0 || !(delta[0] instanceof Number num))
			return;

		int changeValue = num.intValue();

		for (ItemStack itemStack : this.items.getArray(e)) {
			for (Enchantment enchantment : enchantments) {
				int oldLevel = itemStack.getEnchantmentLevel(enchantment);

				int newItemLevel;
				switch (mode) {
					case ADD:
						newItemLevel = oldLevel + changeValue;
						break;
					case REMOVE:
						newItemLevel = oldLevel - changeValue;
						break;
					case SET:
						newItemLevel = changeValue;
						break;
					default:
						assert false;
						return;
				}

				if (newItemLevel <= 0) {
					itemStack.removeEnchantment(enchantment);
				} else {
					itemStack.addUnsafeEnchantment(enchantment, newItemLevel);
				}
			}
		}
	}

	@Override
	public boolean isSingle() {
		return items.isSingle() && enchants.isSingle();
	}

	@Override
	public Class<? extends Long> getReturnType() {
		return Long.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "the level of " + enchants.toString(e, debug) + " of " + items.toString(e, debug);
	}

}
