package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("Material Of")
@Description({"Get the material of an ItemStack/Block/BlockData.",
	"Setting the material of an ItemStack is not supported and may result in weird behaviour.",
	"Setting the material of a BlockData is not supported.",
	"Setting the material of a Block will act normally."})
@Examples({"if material of player's tool = diamond:",
	"set {_mat} to material of player's tool",
	"set {_mat} to material of target block of player",
	"set material of target block of player to oak log"})
@Since("INSERT VERSION")
public class ExprMaterialOf extends SimplePropertyExpression<Object, Material> {

	static {
		register(ExprMaterialOf.class, Material.class, "material[s]",
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
		if (getExpr() != null && getExpr().getReturnType() == ItemStack.class) {
			Skript.warning("The material of an ItemStack cannot properly be set and may result in issues." +
				"Instead you should set the ItemStack to a new ItemStack.");
		}
		if (mode == ChangeMode.SET) {
			return CollectionUtils.array(Material.class);
		}
		return null;
	}

	@SuppressWarnings("deprecation") // Warning provided during acceptChange
	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		Expression<?> expr = getExpr();
		if (expr == null) return;

		if (delta != null && delta[0] instanceof Material material) {
			for (Object object : expr.getArray(event)) {
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
