package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Send Block Change")
@Description("Makes a player see a block as something it really isn't. BlockData support is only for MC 1.13+")
@Examples({"make player see block at player as dirt",
	"make player see target block as campfire[facing=south]"})
@Since("2.2-dev37c, 2.5.1 (block data support)")
public class EffSendBlockChange extends Effect {

	static {
		Skript.registerEffect(EffSendBlockChange.class, "make %players% see %blocks% as %material/blockdata%");
	}

	@SuppressWarnings("null")
	private Expression<Player> players;

	@SuppressWarnings("null")
	private Expression<Block> blocks;

	@SuppressWarnings("null")
	private Expression<Object> as;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		players = (Expression<Player>) exprs[0];
		blocks = (Expression<Block>) exprs[1];
		as = (Expression<Object>) exprs[2];
		return true;
	}

	@Override
	protected void execute(Event e) {
		Object object = this.as.getSingle(e);
		if (object instanceof Material material) {
			BlockData blockData = material.createBlockData();
			for (Player player : players.getArray(e)) {
				for (Block block : blocks.getArray(e)) {
					player.sendBlockChange(block.getLocation(), blockData);
				}
			}
		} else if (object instanceof BlockData blockData) {
			for (Player player : players.getArray(e)) {
				for (Block block : blocks.getArray(e)) {
					player.sendBlockChange(block.getLocation(), blockData);
				}
			}
		}
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return String.format(
			"make %s see %s as %s",
			players.toString(e, debug),
			blocks.toString(e, debug),
			as.toString(e, debug)
		);
	}

}
