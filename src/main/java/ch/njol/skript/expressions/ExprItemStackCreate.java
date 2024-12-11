package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("ItemStack Create")
@Description({"Create a new item stack.",
	"Supports Minecraft's command item format.",
	"See <a href='https://minecraft.wiki/w/Data_component_format'>McWiki Data Component Format</a> for more details"})
@Examples({"set {_i} to itemstack of 10 of diamond",
	"set {_i} to itemstack of netherite shovel",
	"set {_item} to itemstack of stick[minecraft:consumable={},food={saturation:1,nutrition:2}] # Will create a stick you can eat",
	"set {_item} to itemstack of experience_bottle[enchantment_glint_override=false] # Will create an xp bottle without glint",
	"set {_item} to itemstack of apple[!food,!consumable] # Will create an apple that cannot be consumed"})
@Since("3.0.0")
public class ExprItemStackCreate extends SimpleExpression<ItemStack> {

	static {
		Skript.registerExpression(ExprItemStackCreate.class, ItemStack.class, ExpressionType.COMBINED,
			"[new] item[ ]stack (of|from) [%number% [of]] %material%[\\[<.+>\\]]");
	}

	private Expression<Number> amount;
	private Expression<Material> material;
	@Nullable
	private String format;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		this.amount = (Expression<Number>) exprs[0];
		this.material = (Expression<Material>) exprs[1];
		if (!parseResult.regexes.isEmpty()) {
			this.format = parseResult.regexes.getFirst().group();
		}
		return true;
	}

	@Override
	protected @Nullable ItemStack[] get(Event event) {
		List<ItemStack> items = new ArrayList<>();

		int amount = 1;
		if (this.amount != null) {
			Number num = this.amount.getSingle(event);
			if (num != null) amount = num.intValue();
		}

		Material material = this.material.getSingle(event);
		if (material == null || !material.isItem()) return null;

		if (this.format != null) {
			String format = material.getKey() + "[" + this.format + "]";
			ItemStack itemStack;
			try {
				itemStack = Bukkit.getItemFactory().createItemStack(format);
			} catch (IllegalArgumentException ignore) {
				itemStack = new ItemStack(material);
			}
			itemStack.setAmount(amount);
			items.add(itemStack);
		} else {
			items.add(new ItemStack(material, amount));
		}
		return items.toArray(new ItemStack[0]);
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		String amount = this.amount != null ? this.amount.toString(event, debug) + " of " : "";
		String format = this.format != null ? "[" + this.format + "]" : "";
		return "itemstack of " + amount + this.material.toString(event, debug) + format;
	}

}
