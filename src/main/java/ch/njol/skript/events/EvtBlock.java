package ch.njol.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.Classes;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.jetbrains.annotations.Nullable;

/**
 * @author Peter Güttinger
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class EvtBlock extends SkriptEvent {

	static {
		// TODO 'block destroy' event for any kind of block destruction (player, water, trampling, fall (sand, toches, ...), etc) -> BlockPhysicsEvent?
		// REMIND attacking an item frame first removes its item; include this in on block damage?
		Skript.registerEvent("Break / Mine", EvtBlock.class, new Class[]{BlockBreakEvent.class, PlayerBucketFillEvent.class, HangingBreakEvent.class}, "[block] (break[ing]|1¦min(e|ing)) [[of] %-materials/blockdatas%]")
			.description("Called when a block is broken by a player. If you use 'on mine', only events where the broken block dropped something will call the trigger.")
			.examples("on mine:", "on break of stone:", "on mine of any ore:", "on break of chest[facing=north]:", "on break of potatoes[age=7]:")
			.since("1.0 (break), <i>unknown</i> (mine), 2.6 (BlockData support)");
		Skript.registerEvent("Burn", EvtBlock.class, BlockBurnEvent.class, "[block] burn[ing] [[of] %-materials/blockdatas%]")
			.description("Called when a block is destroyed by fire.")
			.examples("on burn:", "on burn of wood, fences, or chests:", "on burn of oak_log[axis=y]:")
			.since("1.0, 2.6 (BlockData support)");
		Skript.registerEvent("Place", EvtBlock.class, new Class[]{BlockPlaceEvent.class, PlayerBucketEmptyEvent.class, HangingPlaceEvent.class}, "[block] (plac(e|ing)|build[ing]) [[of] %-materials/blockdatas%]")
			.description("Called when a player places a block.")
			.examples("on place:", "on place of a furnace, workbench or chest:", "on break of chest[type=right] or chest[type=left]")
			.since("1.0, 2.6 (BlockData support)");
		Skript.registerEvent("Fade", EvtBlock.class, BlockFadeEvent.class, "[block] fad(e|ing) [[of] %-materials/blockdatas%]")
			.description("Called when a block 'fades away', e.g. ice or snow melts.")
			.examples("on fade of snow or ice:", "on fade of snow[layers=2]")
			.since("1.0, 2.6 (BlockData support)");
		Skript.registerEvent("Form", EvtBlock.class, BlockFormEvent.class, "[block] form[ing] [[of] %-materials/blockdatas%]")
			.description("Called when a block is created, but not by a player, e.g. snow forms due to snowfall, water freezes in cold biomes. This isn't called when block spreads (mushroom growth, water physics etc.), as it has its own event (see <a href='#spread'>spread event</a>).")
			.examples("on form of snow:", "on form of a mushroom:")
			.since("1.0, 2.6 (BlockData support)");
	}

	@Nullable
	private Literal<Object> types;

	private boolean mine = false;

	@Override
	public boolean init(final Literal<?>[] args, final int matchedPattern, final ParseResult parser) {
		types = (Literal<Object>) args[0];
		mine = parser.mark == 1;
		return true;
	}

	@SuppressWarnings("null")
	@Override
	public boolean check(final Event event) {
		if (mine && event instanceof BlockBreakEvent) {
			if (((BlockBreakEvent) event).getBlock().getDrops(((BlockBreakEvent) event).getPlayer().getItemInHand()).isEmpty())
				return false;
		}
		if (types == null)
			return true;

		Material material;
		BlockData blockData = null;

		if (event instanceof BlockFormEvent blockFormEvent) {
			BlockState newState = blockFormEvent.getNewState();
			material = newState.getType();
			blockData = newState.getBlockData();
		} else if (event instanceof BlockEvent blockEvent) {
			Block block = blockEvent.getBlock();
			material = block.getType();
			blockData = block.getBlockData();
		} else if (event instanceof PlayerBucketFillEvent playerBucketFillEvent) {
			Block block = playerBucketFillEvent.getBlockClicked();
			material = block.getType();
			blockData = block.getBlockData();
		} else if (event instanceof PlayerBucketEmptyEvent playerBucketEmptyEvent) {
			material = playerBucketEmptyEvent.getItemStack().getType();
		} else {
			assert false;
			return false;
		}

		final Material materialF = material;
		BlockData finalBlockData = blockData;

		return types.check(event, o -> {
			if (o instanceof Material mat)
				return mat == materialF;
			else if (o instanceof BlockData && finalBlockData != null)
				return finalBlockData.matches(((BlockData) o));
			return false;
		});
	}

	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "break/place/burn/fade/form of " + Classes.toString(types);
	}

}
