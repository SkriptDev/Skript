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

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.potion.PotionEffectType;
import org.eclipse.jdt.annotation.Nullable;

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent;
import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Direction;
import ch.njol.skript.util.Getter;

public class EntityEventValues {
	
	static {
		// === EntityEvents ===
		EventValues.registerEventValue(EntityEvent.class, Entity.class, new Getter<Entity, EntityEvent>() {
			@Override
			@Nullable
			public Entity get(final EntityEvent e) {
				return e.getEntity();
			}
		}, 0, "Use 'attacker' and/or 'victim' in damage events", EntityDamageEvent.class);
		EventValues.registerEventValue(EntityEvent.class, CommandSender.class, new Getter<CommandSender, EntityEvent>() {
			@Override
			@Nullable
			public CommandSender get(final EntityEvent e) {
				return e.getEntity();
			}
		}, 0, "Use 'attacker' and/or 'victim' in damage events", EntityDamageEvent.class);
		EventValues.registerEventValue(EntityEvent.class, World.class, new Getter<World, EntityEvent>() {
			@Override
			@Nullable
			public World get(final EntityEvent e) {
				return e.getEntity().getWorld();
			}
		}, 0);
		EventValues.registerEventValue(EntityEvent.class, Location.class, new Getter<Location, EntityEvent>() {
			@Override
			@Nullable
			public Location get(final EntityEvent e) {
				return e.getEntity().getLocation();
			}
		}, 0);
		//CreatureSpawnEvent
		EventValues.registerEventValue(CreatureSpawnEvent.class, SpawnReason.class, new Getter<CreatureSpawnEvent.SpawnReason, CreatureSpawnEvent>() {
			@Override
			@Nullable
			public SpawnReason get(CreatureSpawnEvent e) {
				return e.getSpawnReason();
			}
		}, 0);
		// EntityDamageEvent
		EventValues.registerEventValue(EntityDamageEvent.class, EntityDamageEvent.DamageCause.class, new Getter<EntityDamageEvent.DamageCause, EntityDamageEvent>() {
			@Override
			@Nullable
			public EntityDamageEvent.DamageCause get(final EntityDamageEvent e) {
				return e.getCause();
			}
		}, 0);
		EventValues.registerEventValue(EntityDamageByEntityEvent.class, Projectile.class, new Getter<Projectile, EntityDamageByEntityEvent>() {
			@Override
			@Nullable
			public Projectile get(final EntityDamageByEntityEvent e) {
				if (e.getDamager() instanceof Projectile)
					return (Projectile) e.getDamager();
				return null;
			}
		}, 0);
		// EntityDeathEvent
		EventValues.registerEventValue(EntityDeathEvent.class, Projectile.class, new Getter<Projectile, EntityDeathEvent>() {
			@Override
			@Nullable
			public Projectile get(final EntityDeathEvent e) {
				final EntityDamageEvent ldc = e.getEntity().getLastDamageCause();
				if (ldc instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) ldc).getDamager() instanceof Projectile)
					return (Projectile) ((EntityDamageByEntityEvent) ldc).getDamager();
				return null;
			}
		}, 0);
		EventValues.registerEventValue(EntityDeathEvent.class, EntityDamageEvent.DamageCause.class, new Getter<EntityDamageEvent.DamageCause, EntityDeathEvent>() {
			@Override
			@Nullable
			public EntityDamageEvent.DamageCause get(final EntityDeathEvent e) {
				final EntityDamageEvent ldc = e.getEntity().getLastDamageCause();
				return ldc == null ? null : ldc.getCause();
			}
		}, 0);
		// ProjectileHitEvent
		// ProjectileHitEvent#getHitBlock was added in 1.11
		if(Skript.methodExists(ProjectileHitEvent.class, "getHitBlock"))
			EventValues.registerEventValue(ProjectileHitEvent.class, Block.class, new Getter<Block, ProjectileHitEvent>() {
				@Nullable
				@Override
				public Block get(ProjectileHitEvent e) {
					return e.getHitBlock();
				}
			}, 0);
		EventValues.registerEventValue(ProjectileHitEvent.class, Entity.class, new Getter<Entity, ProjectileHitEvent>() {
			@Override
			@Nullable
			public Entity get(final ProjectileHitEvent e) {
				assert false;
				return e.getEntity();
			}
		}, 0, "Use 'projectile' and/or 'shooter' in projectile hit events", ProjectileHitEvent.class);
		EventValues.registerEventValue(ProjectileHitEvent.class, Projectile.class, new Getter<Projectile, ProjectileHitEvent>() {
			@Override
			@Nullable
			public Projectile get(final ProjectileHitEvent e) {
				return e.getEntity();
			}
		}, 0);
		if (Skript.methodExists(ProjectileHitEvent.class, "getHitBlockFace")) {
			EventValues.registerEventValue(ProjectileHitEvent.class, Direction.class, new Getter<Direction, ProjectileHitEvent>() {
				@Override
				@Nullable
				public Direction get(final ProjectileHitEvent e) {
					BlockFace theHitFace = e.getHitBlockFace();
					if (theHitFace == null) return null;
					return new Direction(theHitFace, 1);
				}
			}, 0);
		}
		// ProjectileLaunchEvent
		EventValues.registerEventValue(ProjectileLaunchEvent.class, Entity.class, new Getter<Entity, ProjectileLaunchEvent>() {
			@Override
			@Nullable
			public Entity get(final ProjectileLaunchEvent e) {
				assert false;
				return e.getEntity();
			}
		}, 0, "Use 'projectile' and/or 'shooter' in shoot events", ProjectileLaunchEvent.class);
		//ProjectileCollideEvent
		if (Skript.classExists("com.destroystokyo.paper.event.entity.ProjectileCollideEvent")) {
			EventValues.registerEventValue(ProjectileCollideEvent.class, Projectile.class, new Getter<Projectile, ProjectileCollideEvent>() {
				@Nullable
				@Override
				public Projectile get(ProjectileCollideEvent evt) {
					return evt.getEntity();
				}
			}, 0);
			EventValues.registerEventValue(ProjectileCollideEvent.class, Entity.class, new Getter<Entity, ProjectileCollideEvent>() {
				@Nullable
				@Override
				public Entity get(ProjectileCollideEvent evt) {
					return evt.getCollidedWith();
				}
			}, 0);
		}
		EventValues.registerEventValue(ProjectileLaunchEvent.class, Projectile.class, new Getter<Projectile, ProjectileLaunchEvent>() {
			@Override
			@Nullable
			public Projectile get(final ProjectileLaunchEvent e) {
				return e.getEntity();
			}
		}, 0);
		// EntityTameEvent
		EventValues.registerEventValue(EntityTameEvent.class, Entity.class, new Getter<Entity, EntityTameEvent>() {
			@Override
			@Nullable
			public Entity get(final EntityTameEvent e) {
				return e.getEntity();
			}
		}, 0);
		// EntityChangeBlockEvent
		EventValues.registerEventValue(EntityChangeBlockEvent.class, Block.class, new Getter<Block, EntityChangeBlockEvent>() {
			@Override
			@Nullable
			public Block get(final EntityChangeBlockEvent e) {
				return e.getBlock();
			}
		}, 0);
		if (Skript.classExists("org.bukkit.event.entity.AreaEffectCloudApplyEvent")) {
			EventValues.registerEventValue(AreaEffectCloudApplyEvent.class, PotionEffectType.class, new Getter<PotionEffectType, AreaEffectCloudApplyEvent>() {
				@Override
				@Nullable
				public PotionEffectType get(AreaEffectCloudApplyEvent e) {
					return e.getEntity().getBasePotionData().getType().getEffectType(); // Whoops this is a bit long call...
				}
			}, 0);
		}
		// ItemSpawnEvent
		EventValues.registerEventValue(ItemSpawnEvent.class, ItemType.class, new Getter<ItemType, ItemSpawnEvent>() {
			@Override
			@Nullable
			public ItemType get(final ItemSpawnEvent e) {
				return new ItemType(e.getEntity().getItemStack());
			}
		}, 0);
		// LightningStrikeEvent
		EventValues.registerEventValue(LightningStrikeEvent.class, Entity.class, new Getter<Entity, LightningStrikeEvent>() {
			@Override
			@Nullable
			public Entity get(LightningStrikeEvent event) {
				return event.getLightning();
			}
		}, 0);
		
		
		
		// --- HangingEvents ---
		
		EventValues.registerEventValue(HangingEvent.class, Hanging.class, new Getter<Hanging, HangingEvent>() {
			@Override
			@Nullable
			public Hanging get(final HangingEvent e) {
				return e.getEntity();
			}
		}, 0);
		EventValues.registerEventValue(HangingEvent.class, World.class, new Getter<World, HangingEvent>() {
			@Override
			@Nullable
			public World get(final HangingEvent e) {
				return e.getEntity().getWorld();
			}
		}, 0);
		EventValues.registerEventValue(HangingEvent.class, Location.class, new Getter<Location, HangingEvent>() {
			@Override
			@Nullable
			public Location get(final HangingEvent e) {
				return e.getEntity().getLocation();
			}
		}, 0);
		
		// HangingBreakEvent
		EventValues.registerEventValue(HangingBreakEvent.class, Entity.class, new Getter<Entity, HangingBreakEvent>() {
			@Nullable
			@Override
			public Entity get(HangingBreakEvent e) {
				if (e instanceof HangingBreakByEntityEvent)
					return ((HangingBreakByEntityEvent) e).getRemover();
				return null;
			}
		}, 0);
		// HangingPlaceEvent
		EventValues.registerEventValue(HangingPlaceEvent.class, Player.class, new Getter<Player, HangingPlaceEvent>() {
			@Override
			@Nullable
			public Player get(final HangingPlaceEvent e) {
				return e.getPlayer();
			}
		}, 0);
		
		// --- VehicleEvents ---
		EventValues.registerEventValue(VehicleEvent.class, Vehicle.class, new Getter<Vehicle, VehicleEvent>() {
			@Override
			@Nullable
			public Vehicle get(final VehicleEvent e) {
				return e.getVehicle();
			}
		}, 0);
		EventValues.registerEventValue(VehicleEvent.class, World.class, new Getter<World, VehicleEvent>() {
			@Override
			@Nullable
			public World get(final VehicleEvent e) {
				return e.getVehicle().getWorld();
			}
		}, 0);
		EventValues.registerEventValue(VehicleExitEvent.class, LivingEntity.class, new Getter<LivingEntity, VehicleExitEvent>() {
			@Override
			@Nullable
			public LivingEntity get(final VehicleExitEvent e) {
				return e.getExited();
			}
		}, 0);
		EventValues.registerEventValue(VehicleEnterEvent.class, Entity.class, new Getter<Entity, VehicleEnterEvent>() {
			@Nullable
			@Override
			public Entity get(VehicleEnterEvent e) {
				return e.getEntered();
			}
		}, 0);
		// We could error here instead but it's preferable to not do it in this case
		EventValues.registerEventValue(VehicleDamageEvent.class, Entity.class, new Getter<Entity, VehicleDamageEvent>() {
			@Nullable
			@Override
			public Entity get(VehicleDamageEvent e) {
				return e.getAttacker();
			}
		}, 0);
		EventValues.registerEventValue(VehicleDestroyEvent.class, Entity.class, new Getter<Entity, VehicleDestroyEvent>() {
			@Nullable
			@Override
			public Entity get(VehicleDestroyEvent e) {
				return e.getAttacker();
			}
		}, 0);
		EventValues.registerEventValue(VehicleEvent.class, Entity.class, new Getter<Entity, VehicleEvent>() {
			@Override
			@Nullable
			public Entity get(final VehicleEvent e) {
				return e.getVehicle().getPassenger();
			}
		}, 0);
		//FireworkExplodeEvent
		if (Skript.classExists("org.bukkit.event.entity.FireworkExplodeEvent")) {
			EventValues.registerEventValue(FireworkExplodeEvent.class, Firework.class, new Getter<Firework, FireworkExplodeEvent>() {
				@Override
				@Nullable
				public Firework get(FireworkExplodeEvent e) {
					return e.getEntity();
				}
			}, 0);
			EventValues.registerEventValue(FireworkExplodeEvent.class, FireworkEffect.class, new Getter<FireworkEffect, FireworkExplodeEvent>() {
				@Override
				@Nullable
				public FireworkEffect get(FireworkExplodeEvent e) {
					List<FireworkEffect> effects = e.getEntity().getFireworkMeta().getEffects();
					if (effects.size() == 0)
						return null;
					return effects.get(0);
				}
			}, 0);
		}
		//ItemDespawnEvent
		EventValues.registerEventValue(ItemDespawnEvent.class, Item.class, new Getter<Item, ItemDespawnEvent>() {
			@Override
			@Nullable
			public Item get(ItemDespawnEvent e) {
				return e.getEntity();
			}
		}, 0);
		EventValues.registerEventValue(ItemDespawnEvent.class, ItemType.class, new Getter<ItemType, ItemDespawnEvent>() {
			@Override
			@Nullable
			public ItemType get(ItemDespawnEvent e) {
				return new ItemType(e.getEntity().getItemStack());
			}
		}, 0);
		//ItemMergeEvent
		EventValues.registerEventValue(ItemMergeEvent.class, Item.class, new Getter<Item, ItemMergeEvent>() {
			@Override
			@Nullable
			public Item get(ItemMergeEvent e) {
				return e.getEntity();
			}
		}, 0);
		EventValues.registerEventValue(ItemMergeEvent.class, Item.class, new Getter<Item, ItemMergeEvent>() {
			@Override
			@Nullable
			public Item get(ItemMergeEvent e) {
				return e.getTarget();
			}
		}, 1);
		EventValues.registerEventValue(ItemMergeEvent.class, ItemType.class, new Getter<ItemType, ItemMergeEvent>() {
			@Override
			@Nullable
			public ItemType get(ItemMergeEvent e) {
				return new ItemType(e.getEntity().getItemStack());
			}
		}, 0);
	}
	
}
