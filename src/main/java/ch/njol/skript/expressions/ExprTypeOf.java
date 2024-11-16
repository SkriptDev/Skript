package ch.njol.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

@Name("Type of")
@Description({
	"Type of a block, item, entity, inventory or potion effect.",
	"Types of items, blocks and block datas are item types similar to them but have amounts",
	"of one, no display names and, on Minecraft 1.13 and newer versions, are undamaged.",
	"Types of entities and inventories are entity types and inventory types known to Skript.",
	"Types of potion effects are potion effect types."
})
@Examples({"on rightclick on an entity:",
	"\tmessage \"This is a %type of clicked entity%!\""})
@Since("1.4, 2.5.2 (potion effect), 2.7 (block datas)")
public class ExprTypeOf extends SimplePropertyExpression<Object, Object> {

	static {
		// TODO this one needs some love
		register(ExprTypeOf.class, Object.class, "type",
			"entities/itemstacks/inventories/potioneffects/blocks/blockdatas");
	}

	@Override
	protected String getPropertyName() {
		return "type";
	}

	@Override
	@Nullable
	public Object convert(Object o) {
		if (o instanceof Entity entity) {
			return entity.getType();
		} else if (o instanceof Inventory) {
			return ((Inventory) o).getType();
		} else if (o instanceof PotionEffect) {
			return ((PotionEffect) o).getType();
		} else if (o instanceof Block block) {
			return block.getType();
		} else if (o instanceof BlockData blockData) {
			return blockData.getMaterial();
		} else if (o instanceof ItemStack itemStack) {
			return itemStack.getType();
		}
		assert false;
		return null;
	}

	@Override
	public Class<?> getReturnType() {
		Class<?> returnType = getExpr().getReturnType();
		return Entity.class.isAssignableFrom(returnType) ? EntityType.class
			: PotionEffectType.class.isAssignableFrom(returnType) ? PotionEffectType.class
			: ItemStack.class.isAssignableFrom(returnType) ? Material.class
			: Block.class.isAssignableFrom(returnType) ? Material.class
			: BlockData.class.isAssignableFrom(returnType) ? Material.class : Object.class;
	}

}
