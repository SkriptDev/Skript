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
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Enchantment Level")
@Description({"The level of a particular <a href='classes.html#enchantment'>enchantment</a> on an item.",
	"The optional 'stored' pattern is for Enchanted Books, where they're not actually enchanted but have stored enchantments."})
@Examples({"if player's tool is enchanted with sharpness:",
	"\tmessage \"You have a sword of sharpness %level of sharpness of the player's tool% equipped\"",
	"# Stored Enchantments",
	"set {_item} to 1 of enchanted book",
	"set stored enchantment level of sharpness of {_item} to 10"})
@Since("2.0")
public class ExprEnchantmentLevel extends SimpleExpression<Long> {

	static {
		Skript.registerExpression(ExprEnchantmentLevel.class, Long.class, ExpressionType.PROPERTY,
			"[the] [:stored] [enchant[ment]] level[s] of %enchantments% (on|of) %itemstacks%",
			"[the] %enchantments% [:stored] [enchant[ment]] level[s] (on|of) %itemstacks%",
			"%itemstacks%'[s] %enchantments% [:stored] [enchant[ment]] level[s]",
			"%itemstacks%'[s] [:stored] [enchant[ment]] level[s] of %enchantments%");
	}

	private Expression<ItemStack> items;
	private Expression<Enchantment> enchants;
	private boolean stored;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		int i = matchedPattern < 2 ? 1 : 0;
		items = (Expression<ItemStack>) exprs[i];
		enchants = (Expression<Enchantment>) exprs[i ^ 1];
		this.stored = parseResult.hasTag("stored");
		return true;
	}

	@Override
	protected Long[] get(Event e) {
		List<Long> levels = new ArrayList<>();
		for (ItemStack itemStack : this.items.getArray(e)) {
			for (Enchantment enchantment : this.enchants.getArray(e)) {
				ItemMeta itemMeta = itemStack.getItemMeta();
				if (this.stored) {
					if (itemMeta instanceof EnchantmentStorageMeta storedMeta) {
						if (storedMeta.hasStoredEnchant(enchantment)) {
							int level = storedMeta.getStoredEnchantLevel(enchantment);
							levels.add((long) level);
						}
					}
				} else if (itemMeta.hasEnchant(enchantment)) {
					int level = itemMeta.getEnchantLevel(enchantment);
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

				ItemMeta itemMeta = itemStack.getItemMeta();
				if (this.stored && itemMeta instanceof EnchantmentStorageMeta storageMeta) {
					if (newItemLevel <= 0) {
						storageMeta.removeStoredEnchant(enchantment);
					} else {
						storageMeta.addStoredEnchant(enchantment, newItemLevel, true);
					}
				} else {
					if (newItemLevel <= 0) {
						itemMeta.removeEnchant(enchantment);
					} else {
						itemMeta.addEnchant(enchantment, newItemLevel, true);
					}
				}
				itemStack.setItemMeta(itemMeta);
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
	public String toString(@Nullable Event event, boolean debug) {
		String stored = this.stored ? " stored " : " ";
		return "the" + stored + "enchantment level of " + this.enchants.toString(event, debug) + " of " +
			this.items.toString(event, debug);
	}

}
