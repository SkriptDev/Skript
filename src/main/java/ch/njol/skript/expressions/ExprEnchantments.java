package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
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

@Name("Item Enchantments")
@Description({"All the enchantments an <a href='classes.html#itemstack'>itemstack</a> has.",
	"The optional 'stored' pattern is for Enchanted Books, which do not have enchantments but rather have stored enchantments."})
@Examples({"clear enchantments of event-item",
	"loop enchantments of player's tool:",
	"if enchantments of {_item} contains sharpness:"})
@Since("2.2-dev36")
public class ExprEnchantments extends SimpleExpression<Enchantment> {

	static {
		PropertyExpression.register(ExprEnchantments.class, Enchantment.class,
			"[:stored] enchantments", "itemstacks");
	}

	@SuppressWarnings("null")
	private Expression<ItemStack> items;
	private boolean stored;

	@SuppressWarnings({"null", "unchecked"})
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		this.items = (Expression<ItemStack>) exprs[0];
		this.stored = parseResult.hasTag("stored");
		return true;
	}

	@Override
	@Nullable
	protected Enchantment[] get(Event event) {
		List<Enchantment> enchantments = new ArrayList<>();

		for (ItemStack item : items.getArray(event)) {
			ItemMeta itemMeta = item.getItemMeta();
			if (this.stored) {
				if (itemMeta instanceof EnchantmentStorageMeta storageMeta) {
					enchantments.addAll(storageMeta.getStoredEnchants().keySet());
				}
			} else {
				enchantments.addAll(itemMeta.getEnchants().keySet());
			}
		}
		return enchantments.toArray(new Enchantment[0]);
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.DELETE || mode == ChangeMode.REMOVE) {
			return CollectionUtils.array(Enchantment[].class);
		}
		return null;
	}


	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		if (mode == ChangeMode.DELETE) {
			for (ItemStack itemStack : this.items.getArray(event)) {
				ItemMeta itemMeta = itemStack.getItemMeta();
				if (this.stored && itemMeta instanceof EnchantmentStorageMeta storageMeta) {
					for (Enchantment enchantment : storageMeta.getStoredEnchants().keySet()) {
						storageMeta.removeStoredEnchant(enchantment);
					}
				} else {
					itemMeta.removeEnchantments();
				}
				itemStack.setItemMeta(itemMeta);
			}
		} else if (mode == ChangeMode.REMOVE && delta != null) {
			List<Enchantment> enchantments = new ArrayList<>();
			for (@Nullable Object object : delta) {
				if (object instanceof Enchantment enchantment) {
					enchantments.add(enchantment);
				}
			}
			for (ItemStack itemStack : this.items.getArray(event)) {
				for (Enchantment enchantment : enchantments) {
					ItemMeta itemMeta = itemStack.getItemMeta();
					if (this.stored && itemMeta instanceof EnchantmentStorageMeta storageMeta) {
						storageMeta.removeStoredEnchant(enchantment);
					} else {
						itemMeta.removeEnchant(enchantment);
					}
					itemStack.setItemMeta(itemMeta);
				}
			}
		}
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends Enchantment> getReturnType() {
		return Enchantment.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "the enchantments of " + this.items.toString(event, debug);
	}

}
