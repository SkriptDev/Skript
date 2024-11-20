package ch.njol.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("Is Preferred Tool")
@Description({"Checks whether an Itemstack or Material is the preferred tool for a block.",
	"A preferred tool is one that will drop the block's item when used.",
	"For example, a wooden pickaxe is a preferred tool for grass and stone blocks, but not for iron ore."})
@Examples({
	"on left click:",
	"\tevent-block is set",
	"\tif player's tool is the preferred tool for event-block:",
	"\t\tbreak event-block naturally using player's tool",
	"\telse:",
	"\t\tcancel event"
})
@Since("2.7")
@RequiredPlugins("1.16.5+, Paper 1.19.2+ (blockdata)")
public class CondIsPreferredTool extends Condition {

	static {
		String types = "blocks";
		if (Skript.methodExists(BlockData.class, "isPreferredTool", ItemStack.class))
			types += "/blockdatas";

		Skript.registerCondition(CondIsPreferredTool.class,
			"%itemstacks/materials% (is|are) %" + types + "%'s preferred tool[s]",
			"%itemstacks/materials% (is|are) [the|a] preferred tool[s] (for|of) %" + types + "%",
			"%itemstacks/materials% (is|are)(n't| not) %" + types + "%'s preferred tool[s]",
			"%itemstacks/materials% (is|are)(n't| not) [the|a] preferred tool[s] (for|of) %" + types + "%"
		);
	}

	private Expression<?> items;
	private Expression<?> blocks;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		setNegated(matchedPattern >= 2);
		items = exprs[0];
		blocks = exprs[1];
		return true;
	}

	@Override
	public boolean check(Event event) {
		return blocks.check(event, block ->
			items.check(event, object -> {
				ItemStack item = null;
				if (object instanceof ItemStack itemStack) {
					item = itemStack;
				} else if (object instanceof Material material) {
					item = new ItemStack(material);
				}
				if (item != null) {
					if (block instanceof Block bloc)
						return bloc.isPreferredTool(item);
					if (block instanceof BlockData blockData)
						return blockData.isPreferredTool(item);
				}
				return false;
			}), isNegated());
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return items.toString(event, debug) + " is the preferred tool for " + blocks.toString(event, debug);
	}

}
