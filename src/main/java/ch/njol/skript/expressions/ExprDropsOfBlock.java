package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Drops Of Block")
@Description("A list of the items that will drop when a block is broken.")
@RequiredPlugins("Minecraft 1.15+ ('as %entity%')")
@Examples({"on break of block:",
	"\tgive drops of block using player's tool to player"})
@Since("2.5.1")
public class ExprDropsOfBlock extends SimpleExpression<ItemStack> {

	private final static boolean DROPS_OF_ENTITY_EXISTS = Skript.methodExists(Block.class, "getDrops", ItemStack.class, Entity.class);

	static {
		Skript.registerExpression(ExprDropsOfBlock.class, ItemStack.class, ExpressionType.COMBINED,
			"[(all|the|all [of] the)] drops of %blocks% [(using|with) %-itemstack% [(1¦as %-entity%)]]",
			"%blocks%'s drops [(using|with) %-itemstack% [(1¦as %-entity%)]]");
	}

	@SuppressWarnings("null")
	private Expression<Block> block;
	@SuppressWarnings("null")
	private Expression<ItemStack> item;
	@SuppressWarnings("null")
	private Expression<Entity> entity;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		block = (Expression<Block>) exprs[0];
		item = (Expression<ItemStack>) exprs[1];
		if (!DROPS_OF_ENTITY_EXISTS && parseResult.mark == 1) {
			Skript.error("Getting the drops of a block as an entity is only possible on Minecraft 1.15+", ErrorQuality.SEMANTIC_ERROR);
			return false;
		}
		entity = (Expression<Entity>) exprs[2];
		return true;
	}

	@Nullable
	@Override
	@SuppressWarnings("null")
	protected ItemStack[] get(Event e) {
		@Nullable
		Block[] blocks = this.block.getArray(e);
		if (blocks != null) {
			if (this.item == null) {
				List<ItemStack> list = new ArrayList<>();
				for (Block block : blocks) {
					assert block != null;
					list.addAll(block.getDrops());
				}
				return list.toArray(new ItemStack[0]);
			} else if (this.entity != null) {
				ItemStack item = this.item.getSingle(e);
				Entity entity = this.entity.getSingle(e);
				ArrayList<ItemStack> list = new ArrayList<>();
				for (Block block : blocks) {
					assert block != null;
					list.addAll(block.getDrops(item, entity));
				}
				return list.toArray(new ItemStack[0]);
			} else {
				ItemStack item = this.item.getSingle(e);
				ArrayList<ItemStack> list = new ArrayList<>();
				for (Block block : blocks) {
					assert block != null;
					list.addAll(block.getDrops(item));
				}
				return list.toArray(new ItemStack[0]);
			}
		}
		return null;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "drops of " + block.toString(e, debug) + (item != null ? (" using " + item.toString(e, debug) + (entity != null ? " as " + entity.toString(e, debug) : null)) : "");
	}

}
