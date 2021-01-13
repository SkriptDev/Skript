/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.events.eventvalues;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.command.CommandEvent;
import ch.njol.skript.events.EvtMoveOn;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.BlockStateBlock;
import ch.njol.skript.util.Direction;
import ch.njol.skript.util.Getter;

public class PlayerEventValues {
	
	public PlayerEventValues() { }
	
	static {
		EventValues.registerEventValue(PlayerItemDamageEvent.class, Player.class, new Getter<Player, PlayerItemDamageEvent>() {
			@Nullable
			@Override
			public Player get(PlayerItemDamageEvent arg) {
				return null;
			}
		}, 0);
		// --- PlayerEvents ---
		EventValues.registerEventValue(PlayerEvent.class, Player.class, new Getter<Player, PlayerEvent>() {
			@Override
			@Nullable
			public Player get(final PlayerEvent e) {
				return e.getPlayer();
			}
		}, 0);
		EventValues.registerEventValue(PlayerEvent.class, World.class, new Getter<World, PlayerEvent>() {
			@Override
			@Nullable
			public World get(final PlayerEvent e) {
				return e.getPlayer().getWorld();
			}
		}, 0);
		// PlayerBedEnterEvent
		EventValues.registerEventValue(PlayerBedEnterEvent.class, Block.class, new Getter<Block, PlayerBedEnterEvent>() {
			@Override
			@Nullable
			public Block get(final PlayerBedEnterEvent e) {
				return e.getBed();
			}
		}, 0);
		// PlayerBedLeaveEvent
		EventValues.registerEventValue(PlayerBedLeaveEvent.class, Block.class, new Getter<Block, PlayerBedLeaveEvent>() {
			@Override
			@Nullable
			public Block get(final PlayerBedLeaveEvent e) {
				return e.getBed();
			}
		}, 0);
		// PlayerBucketEvents
		EventValues.registerEventValue(PlayerBucketFillEvent.class, Block.class, new Getter<Block, PlayerBucketFillEvent>() {
			@Override
			@Nullable
			public Block get(final PlayerBucketFillEvent e) {
				return e.getBlockClicked().getRelative(e.getBlockFace());
			}
		}, 0);
		EventValues.registerEventValue(PlayerBucketFillEvent.class, Block.class, new Getter<Block, PlayerBucketFillEvent>() {
			@Override
			@Nullable
			public Block get(final PlayerBucketFillEvent e) {
				final BlockState s = e.getBlockClicked().getRelative(e.getBlockFace()).getState();
				s.setType(Material.AIR);
				s.setRawData((byte) 0);
				return new BlockStateBlock(s, true);
			}
		}, 1);
		EventValues.registerEventValue(PlayerBucketEmptyEvent.class, Block.class, new Getter<Block, PlayerBucketEmptyEvent>() {
			@Override
			@Nullable
			public Block get(final PlayerBucketEmptyEvent e) {
				return e.getBlockClicked().getRelative(e.getBlockFace());
			}
		}, -1);
		EventValues.registerEventValue(PlayerBucketEmptyEvent.class, Block.class, new Getter<Block, PlayerBucketEmptyEvent>() {
			@Override
			@Nullable
			public Block get(final PlayerBucketEmptyEvent e) {
				final BlockState s = e.getBlockClicked().getRelative(e.getBlockFace()).getState();
				s.setType(e.getBucket() == Material.WATER_BUCKET ? Material.WATER : Material.LAVA);
				s.setRawData((byte) 0);
				return new BlockStateBlock(s, true);
			}
		}, 0);
		// PlayerDropItemEvent
		EventValues.registerEventValue(PlayerDropItemEvent.class, Item.class, new Getter<Item, PlayerDropItemEvent>() {
			@Override
			@Nullable
			public Item get(final PlayerDropItemEvent e) {
				return e.getItemDrop();
			}
		}, 0);
		EventValues.registerEventValue(PlayerDropItemEvent.class, ItemType.class, new Getter<ItemType, PlayerDropItemEvent>() {
			@Override
			@Nullable
			public ItemType get(final PlayerDropItemEvent e) {
				return new ItemType(e.getItemDrop().getItemStack());
			}
		}, 0);
		// PlayerPickupItemEvent
		EventValues.registerEventValue(PlayerPickupItemEvent.class, Item.class, new Getter<Item, PlayerPickupItemEvent>() {
			@Override
			@Nullable
			public Item get(final PlayerPickupItemEvent e) {
				return e.getItem();
			}
		}, 0);
		EventValues.registerEventValue(PlayerPickupItemEvent.class, ItemType.class, new Getter<ItemType, PlayerPickupItemEvent>() {
			@Override
			@Nullable
			public ItemType get(final PlayerPickupItemEvent e) {
				return new ItemType(e.getItem().getItemStack());
			}
		}, 0);
		// PlayerItemConsumeEvent
		if (Skript.classExists("org.bukkit.event.player.PlayerItemConsumeEvent")) {
			EventValues.registerEventValue(PlayerItemConsumeEvent.class, ItemType.class, new Getter<ItemType, PlayerItemConsumeEvent>() {
				@Override
				@Nullable
				public ItemType get(final PlayerItemConsumeEvent e) {
					return new ItemType(e.getItem());
				}
			}, 0);
		}
		// PlayerItemBreakEvent
		if (Skript.classExists("org.bukkit.event.player.PlayerItemBreakEvent")) {
			EventValues.registerEventValue(PlayerItemBreakEvent.class, ItemType.class, new Getter<ItemType, PlayerItemBreakEvent>() {
				@Override
				@Nullable
				public ItemType get(final PlayerItemBreakEvent e) {
					return new ItemType(e.getBrokenItem());
				}
			}, 0);
		}
		// PlayerInteractEntityEvent
		EventValues.registerEventValue(PlayerInteractEntityEvent.class, Entity.class, new Getter<Entity, PlayerInteractEntityEvent>() {
			@Override
			@Nullable
			public Entity get(final PlayerInteractEntityEvent e) {
				return e.getRightClicked();
			}
		}, 0);
		EventValues.registerEventValue(PlayerInteractEntityEvent.class, ItemType.class, new Getter<ItemType, PlayerInteractEntityEvent>() {
			@Override
			@Nullable
			public ItemType get(final PlayerInteractEntityEvent e) {
				EquipmentSlot hand = e.getHand();
				if (hand == EquipmentSlot.HAND)
					return new ItemType(e.getPlayer().getInventory().getItemInMainHand());
				else if (hand == EquipmentSlot.OFF_HAND)
					return new ItemType(e.getPlayer().getInventory().getItemInOffHand());
				else
					return null;
			}
		}, 0);
		// PlayerInteractEvent
		EventValues.registerEventValue(PlayerInteractEvent.class, ItemType.class, new Getter<ItemType, PlayerInteractEvent>() {
			@Override
			@Nullable
			public ItemType get(final PlayerInteractEvent e) {
				ItemStack item = e.getItem();
				return item == null ? null : new ItemType(item);
			}
		}, 0);
		EventValues.registerEventValue(PlayerInteractEvent.class, Block.class, new Getter<Block, PlayerInteractEvent>() {
			@Override
			@Nullable
			public Block get(final PlayerInteractEvent e) {
				return e.getClickedBlock();
			}
		}, 0);
		EventValues.registerEventValue(PlayerInteractEvent.class, Direction.class, new Getter<Direction, PlayerInteractEvent>() {
			@Override
			@Nullable
			public Direction get(final PlayerInteractEvent e) {
				return new Direction(new double[]{e.getBlockFace().getModX(), e.getBlockFace().getModY(), e.getBlockFace().getModZ()});
			}
		}, 0);
		// PlayerShearEntityEvent
		EventValues.registerEventValue(PlayerShearEntityEvent.class, Entity.class, new Getter<Entity, PlayerShearEntityEvent>() {
			@Override
			@Nullable
			public Entity get(final PlayerShearEntityEvent e) {
				return e.getEntity();
			}
		}, 0);
		// PlayerMoveEvent
		EventValues.registerEventValue(PlayerMoveEvent.class, Block.class, new Getter<Block, PlayerMoveEvent>() {
			@Override
			@Nullable
			public Block get(final PlayerMoveEvent e) {
				return EvtMoveOn.getBlock(e);
			}
		}, 0);
		// PlayerItemDamageEvent
		EventValues.registerEventValue(PlayerItemDamageEvent.class, ItemType.class, new Getter<ItemType, PlayerItemDamageEvent>() {
			@Override
			@Nullable
			public ItemType get(PlayerItemDamageEvent event) {
				return new ItemType(event.getItem());
			}
		}, 0);
		//PlayerItemMendEvent
		if (Skript.classExists("org.bukkit.event.player.PlayerItemMendEvent")) {
			EventValues.registerEventValue(PlayerItemMendEvent.class, Player.class, new Getter<Player, PlayerItemMendEvent>() {
				@Override
				@Nullable
				public Player get(PlayerItemMendEvent e) {
					return e.getPlayer();
				}
			}, 0);
			EventValues.registerEventValue(PlayerItemMendEvent.class, ItemType.class, new Getter<ItemType, PlayerItemMendEvent>() {
				@Override
				@Nullable
				public ItemType get(PlayerItemMendEvent e) {
					return new ItemType(e.getItem());
				}
			}, 0);
			EventValues.registerEventValue(PlayerItemMendEvent.class, Entity.class, new Getter<Entity, PlayerItemMendEvent>() {
				@Override
				@Nullable
				public Entity get(PlayerItemMendEvent e) {
					return e.getExperienceOrb();
				}
			}, 0);
		}
		//PlayerTeleportEvent
		EventValues.registerEventValue(PlayerTeleportEvent.class, PlayerTeleportEvent.TeleportCause.class, new Getter<PlayerTeleportEvent.TeleportCause, PlayerTeleportEvent>() {
			@Override
			@Nullable
			public PlayerTeleportEvent.TeleportCause get(final PlayerTeleportEvent e) {
				return e.getCause();
			}
		}, 0);
		//PlayerToggleFlightEvent
		EventValues.registerEventValue(PlayerToggleFlightEvent.class, Player.class, new Getter<Player, PlayerToggleFlightEvent>() {
			@Override
			@Nullable
			public Player get(PlayerToggleFlightEvent e) {
				return e.getPlayer();
			}
		}, 0);
		//PlayerRiptideEvent
		if (Skript.classExists("org.bukkit.event.player.PlayerRiptideEvent")) {
			EventValues.registerEventValue(PlayerRiptideEvent.class, ItemType.class, new Getter<ItemType, PlayerRiptideEvent>() {
				@Override
				public ItemType get(PlayerRiptideEvent e) {
					return new ItemType(e.getItem());
				}
			}, 0);
		}
		//PlayerEditBookEvent
		EventValues.registerEventValue(PlayerEditBookEvent.class, ItemType.class, new Getter<ItemType, PlayerEditBookEvent>() {
			@Nullable
			@Override
			public ItemType get(PlayerEditBookEvent e) {
				ItemStack book = new ItemStack(e.getPlayer().getItemInHand().getType());
				book.setItemMeta(e.getNewBookMeta());
				return new ItemType(book); //TODO: Find better way to derive this event value
			}
		}, 0);
		
		// === CommandEvents ===
		// PlayerCommandPreprocessEvent is a PlayerEvent
		EventValues.registerEventValue(ServerCommandEvent.class, CommandSender.class, new Getter<CommandSender, ServerCommandEvent>() {
			@Override
			@Nullable
			public CommandSender get(final ServerCommandEvent e) {
				return e.getSender();
			}
		}, 0);
		EventValues.registerEventValue(CommandEvent.class, CommandSender.class, new Getter<CommandSender, CommandEvent>() {
			@Override
			@Nullable
			public CommandSender get(final CommandEvent e) {
				return e.getSender();
			}
		}, 0);
		EventValues.registerEventValue(CommandEvent.class, World.class, new Getter<World, CommandEvent>() {
			@Override
			@Nullable
			public World get(final CommandEvent e) {
				return e.getSender() instanceof Player ? ((Player) e.getSender()).getWorld() : null;
			}
		}, 0);
	}
	
}
