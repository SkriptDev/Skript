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

import java.util.List;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.HorseJumpEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.eclipse.jdt.annotation.Nullable;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import ch.njol.skript.util.slot.InventorySlot;
import ch.njol.skript.util.slot.Slot;

public class InventoryEventValues {
	
	static {
		// === InventoryEvents ===
		// InventoryClickEvent
		EventValues.registerEventValue(InventoryClickEvent.class, Player.class, new Getter<Player, InventoryClickEvent>() {
			@Override
			@Nullable
			public Player get(final InventoryClickEvent e) {
				return e.getWhoClicked() instanceof Player ? (Player) e.getWhoClicked() : null;
			}
		}, 0);
		EventValues.registerEventValue(InventoryClickEvent.class, World.class, new Getter<World, InventoryClickEvent>() {
			@Override
			@Nullable
			public World get(final InventoryClickEvent e) {
				return e.getWhoClicked().getWorld();
			}
		}, 0);
		EventValues.registerEventValue(InventoryClickEvent.class, ItemType.class, new Getter<ItemType, InventoryClickEvent>() {
			@Override
			@Nullable
			public ItemType get(final InventoryClickEvent e) {
				if (e instanceof CraftItemEvent)
					return new ItemType(((CraftItemEvent) e).getRecipe().getResult());
				ItemStack item = e.getCurrentItem();
				return item == null ? null : new ItemType(item);
			}
		}, 0);
		EventValues.registerEventValue(InventoryClickEvent.class, Slot.class, new Getter<Slot, InventoryClickEvent>() {
			@SuppressWarnings("null")
			@Override
			@Nullable
			public Slot get(final InventoryClickEvent e) {
				Inventory invi = e.getClickedInventory(); // getInventory is WRONG and dangerous
				int slotIndex = e.getSlot();
				
				// Not all indices point to inventory slots. Equipment, for example
				if (invi instanceof PlayerInventory && slotIndex >= 36) {
					return new ch.njol.skript.util.slot.EquipmentSlot(((PlayerInventory) invi).getHolder(), slotIndex);
				} else {
					return new InventorySlot(invi, slotIndex);
				}
			}
		}, 0);
		EventValues.registerEventValue(InventoryClickEvent.class, InventoryAction.class, new Getter<InventoryAction, InventoryClickEvent>() {
			@Override
			@Nullable
			public InventoryAction get(final InventoryClickEvent e) {
				return e.getAction();
			}
		}, 0);
		EventValues.registerEventValue(InventoryClickEvent.class, ClickType.class, new Getter<ClickType, InventoryClickEvent>() {
			@Override
			@Nullable
			public ClickType get(final InventoryClickEvent e) {
				return e.getClick();
			}
		}, 0);
		EventValues.registerEventValue(InventoryClickEvent.class, Inventory.class, new Getter<Inventory, InventoryClickEvent>() {
			@Override
			@Nullable
			public Inventory get(final InventoryClickEvent e) {
				return e.getClickedInventory();
			}
		}, 0);
		//BlockFertilizeEvent
		if(Skript.classExists("org.bukkit.event.block.BlockFertilizeEvent")) {
			EventValues.registerEventValue(BlockFertilizeEvent.class, Player.class, new Getter<Player, BlockFertilizeEvent>() {
				@Nullable
				@Override
				public Player get(BlockFertilizeEvent event) {
					return event.getPlayer();
				}
			}, 0);
		}
		// CraftItemEvent REMIND maybe re-add this when Skript parser is reworked?
//		EventValues.registerEventValue(CraftItemEvent.class, ItemStack.class, new Getter<ItemStack, CraftItemEvent>() {
//			@Override
//			@Nullable
//			public ItemStack get(final CraftItemEvent e) {
//				return e.getRecipe().getResult();
//			}
//		}, 0);
		// PrepareItemCraftEvent
		EventValues.registerEventValue(PrepareItemCraftEvent.class, Slot.class, new Getter<Slot, PrepareItemCraftEvent>() {
			@Override
			@Nullable
			public Slot get(final PrepareItemCraftEvent e) {
				return new InventorySlot(e.getInventory(), 9);
			}
		}, 0);
		EventValues.registerEventValue(PrepareItemCraftEvent.class, Player.class, new Getter<Player, PrepareItemCraftEvent>() {
			@Override
			@Nullable
			public Player get(final PrepareItemCraftEvent e) {
				List<HumanEntity> viewers = e.getInventory().getViewers(); // Get all viewers
				if (viewers.size() == 0) // ... if we don't have any
					return null;
				HumanEntity first = viewers.get(0); // Get first viewer and hope it is crafter
				if (first instanceof Player) // Needs to be player... Usually it is
					return (Player) first;
				return null;
			}
		}, 0);
		//InventoryOpenEvent
		EventValues.registerEventValue(InventoryOpenEvent.class, Player.class, new Getter<Player, InventoryOpenEvent>() {
			@Override
			@Nullable
			public Player get(final InventoryOpenEvent e) {
				return (Player) e.getPlayer();
			}
		}, 0);
		EventValues.registerEventValue(InventoryOpenEvent.class, Inventory.class, new Getter<Inventory, InventoryOpenEvent>() {
			@Override
			@Nullable
			public Inventory get(final InventoryOpenEvent e) {
				return e.getInventory();
			}
		}, 0);
		//InventoryCloseEvent
		EventValues.registerEventValue(InventoryCloseEvent.class, Player.class, new Getter<Player, InventoryCloseEvent>() {
			@Override
			@Nullable
			public Player get(final InventoryCloseEvent e) {
				return (Player) e.getPlayer();
			}
		}, 0);
		EventValues.registerEventValue(InventoryCloseEvent.class, Inventory.class, new Getter<Inventory, InventoryCloseEvent>() {
			@Override
			@Nullable
			public Inventory get(final InventoryCloseEvent e) {
				return e.getInventory();
			}
		}, 0);
		//InventoryPickupItemEvent
		EventValues.registerEventValue(InventoryPickupItemEvent.class, Inventory.class, new Getter<Inventory, InventoryPickupItemEvent>() {
			@Nullable
			@Override
			public Inventory get(InventoryPickupItemEvent event) {
				return event.getInventory();
			}
		}, 0);
		EventValues.registerEventValue(InventoryPickupItemEvent.class, Item.class, new Getter<Item, InventoryPickupItemEvent>() {
			@Nullable
			@Override
			public Item get(InventoryPickupItemEvent event) {
				return event.getItem();
			}
		}, 0);
		EventValues.registerEventValue(InventoryPickupItemEvent.class, ItemType.class, new Getter<ItemType, InventoryPickupItemEvent>() {
			@Nullable
			@Override
			public ItemType get(InventoryPickupItemEvent event) {
				return new ItemType(event.getItem().getItemStack());
			}
		}, 0);
		//PlayerArmorChangeEvent
		if (Skript.classExists("com.destroystokyo.paper.event.player.PlayerArmorChangeEvent")) {
			EventValues.registerEventValue(PlayerArmorChangeEvent.class, ItemType.class, new Getter<ItemType, PlayerArmorChangeEvent>() {
				@Override
				@Nullable
				public ItemType get(PlayerArmorChangeEvent e) {
					ItemStack stack = e.getNewItem();
					return stack == null ? null : new ItemType(stack);
				}
			}, 0);
		}
		//PrepareItemEnchantEvent
		EventValues.registerEventValue(PrepareItemEnchantEvent.class, Player.class, new Getter<Player, PrepareItemEnchantEvent>() {
			@Override
			@Nullable
			public Player get(PrepareItemEnchantEvent e) {
				return e.getEnchanter();
			}
		}, 0);
		EventValues.registerEventValue(PrepareItemEnchantEvent.class, ItemType.class, new Getter<ItemType, PrepareItemEnchantEvent>() {
			@Override
			@Nullable
			public ItemType get(PrepareItemEnchantEvent e) {
				return new ItemType(e.getItem());
			}
		}, 0);
		EventValues.registerEventValue(PrepareItemEnchantEvent.class, Block.class, new Getter<Block, PrepareItemEnchantEvent>() {
			@Override
			@Nullable
			public Block get(PrepareItemEnchantEvent e) {
				return e.getEnchantBlock();
			}
		}, 0);
		//EnchantItemEvent
		EventValues.registerEventValue(EnchantItemEvent.class, Player.class, new Getter<Player, EnchantItemEvent>() {
			@Override
			@Nullable
			public Player get(EnchantItemEvent e) {
				return e.getEnchanter();
			}
		}, 0);
		EventValues.registerEventValue(EnchantItemEvent.class, ItemType.class, new Getter<ItemType, EnchantItemEvent>() {
			@Override
			@Nullable
			public ItemType get(EnchantItemEvent e) {
				return new ItemType(e.getItem());
			}
		}, 0);
		EventValues.registerEventValue(EnchantItemEvent.class, Block.class, new Getter<Block, EnchantItemEvent>() {
			@Override
			@Nullable
			public Block get(EnchantItemEvent e) {
				return e.getEnchantBlock();
			}
		}, 0);
		EventValues.registerEventValue(HorseJumpEvent.class, Entity.class, new Getter<Entity, HorseJumpEvent>() {
			@Nullable
			@Override
			public Entity get(HorseJumpEvent evt) {
				return evt.getEntity();
			}
		}, 0);
	}
	
}
