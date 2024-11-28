package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Block Data")
@Description({"Get the <a href='classes.html#blockdata'>block data</a> associated with a Block or BlockDisplay entity.",
	"This data can also be used to set blocks."})
@Examples({"set {data} to block data of target block",
	"set block at player to {data}",
	"set block data of target block to oak_stairs[facing=south;waterlogged=true]"})
@RequiredPlugins("Minecraft 1.13+")
@Since("2.5, 2.5.2 (set), 3.0.0 (BlockDisplays)")
public class ExprBlockData extends SimplePropertyExpression<Object, BlockData> {

	private static final BlockData AIR = Material.AIR.createBlockData();

	static {
		register(ExprBlockData.class, BlockData.class,
			"block[ ]data", "blocks/entities");
	}

	@Nullable
	@Override
	public BlockData convert(Object object) {
		if (object instanceof Block block) return block.getBlockData();
		else if (object instanceof BlockDisplay blockDisplay)
			return blockDisplay.getBlock();
		return null;
	}

	@Nullable
	@Override
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.DELETE)
			return CollectionUtils.array(BlockData.class);
		return null;
	}

	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		Expression<?> expr = getExpr();
		if (expr == null) return;

		BlockData blockData = delta != null && delta[0] instanceof BlockData bd ? bd : AIR;

		for (Object object : expr.getArray(e)) {
			if (object instanceof Block block) {
				block.setBlockData(blockData);
			} else if (object instanceof BlockDisplay blockDisplay) {
				blockDisplay.setBlock(blockData);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "block data";
	}

	@Override
	public Class<? extends BlockData> getReturnType() {
		return BlockData.class;
	}

}
