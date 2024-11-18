package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("Material Of")
@Description("Get/set the material of an item/block/blockdata.")
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
	public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET) {
			return CollectionUtils.array(Material.class);
		}
		return null;
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		if (delta != null && delta[0] instanceof Material material) {
			for (Object object : getExpr().getArray(event)) {
				if (object instanceof ItemStack itemStack) {
					itemStack.setType(material);
				} else if (object instanceof Block block) {
					block.setType(material);
				}
			}
		}

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
