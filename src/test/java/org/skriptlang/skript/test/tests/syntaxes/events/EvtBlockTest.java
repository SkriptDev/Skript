package org.skriptlang.skript.test.tests.syntaxes.events;

import ch.njol.skript.test.runner.SkriptJUnitTest;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class EvtBlockTest extends SkriptJUnitTest {

	static {
		setShutdownDelay(1);
	}

	private Player player;

	@Before
	public void setUp() {
		this.player = EasyMock.niceMock(Player.class);
	}

	@Test
	public void testBreakBlock() {
		Block block = getBlock();

		BlockData beforeData = block.getBlockData();
		block.setBlockData(Material.OAK_STAIRS.createBlockData());

		BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block, this.player);
		blockBreakEvent.callEvent();

		// Reset block
		block.setBlockData(beforeData);
	}

	@Test
	public void testPlaceBlock() {
		Block block = getBlock();

		Block placedAt = block.getRelative(BlockFace.UP);
		BlockState previous = placedAt.getState();
		placedAt.setType(Material.DIRT);

		ItemStack itemStack = new ItemStack(Material.DIRT);
		BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(placedAt, previous,
			block, itemStack, this.player, true, EquipmentSlot.HAND);
		blockPlaceEvent.callEvent();

		// Reset block
		previous.update(true);
	}

}
