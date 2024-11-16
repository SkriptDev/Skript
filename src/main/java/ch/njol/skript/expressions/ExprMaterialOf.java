package ch.njol.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("Material Of")
@Description("Get the material of an item/block/blockdata.")
@Since("INSERT VERSION")
public class ExprMaterialOf extends SimplePropertyExpression<Object, Material> {

	static {
		register(ExprMaterialOf.class, Material.class, "material",
			"itemstacks/blocks/blockdatas");
	}
	@Override
	public @Nullable Material convert(Object from) {
		if (from instanceof ItemStack itemStack) {
			return itemStack.getType();
		} else if (from instanceof Block block) {
			return block.getType();
		} else if (from instanceof BlockData blockData) {
			return blockData.getMaterial();
		}
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "material";
	}

	@Override
	public Class<? extends Material> getReturnType() {
		return Material.class;
	}

}
