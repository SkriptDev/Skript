package ch.njol.skript.classes.data.defaults;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.InventoryUtils;
import ch.njol.skript.command.CommandEvent;
import ch.njol.skript.events.bukkit.ScriptEvent;
import ch.njol.skript.events.bukkit.SkriptStartEvent;
import ch.njol.skript.events.bukkit.SkriptStopEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.BlockStateBlock;
import ch.njol.skript.util.DelayedChangeBlock;
import ch.njol.skript.util.Direction;
import ch.njol.skript.util.Getter;
import ch.njol.skript.util.Timespan;
import com.destroystokyo.paper.event.block.AnvilDamagedEvent;
import com.destroystokyo.paper.event.entity.EndermanAttackPlayerEvent;
import com.destroystokyo.paper.event.entity.ProjectileCollideEvent;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.papermc.paper.event.entity.EntityMoveEvent;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import io.papermc.paper.event.player.PlayerStonecutterRecipeSelectEvent;
import io.papermc.paper.event.player.PlayerStopUsingItemEvent;
import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.block.BellResonateEvent;
import org.bukkit.event.block.BellRingEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.EntityTransformEvent.TransformReason;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.entity.HorseJumpEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerQuitEvent.QuitReason;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.WeatherEvent;
import org.bukkit.event.world.ChunkEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Peter Güttinger
 */
@SuppressWarnings("deprecation")
public final class BukkitEventValues {

	private BukkitEventValues() {
	}

	private static final ItemStack AIR_IS = new ItemStack(Material.AIR);

	static void init() {

		// === WorldEvents ===
		EventValues.registerEventValue(WorldEvent.class, World.class, new Getter<>() {
			@Override
			public @NotNull World get(final WorldEvent e) {
				return e.getWorld();
			}
		}, 0);
		// StructureGrowEvent - a WorldEvent
		EventValues.registerEventValue(StructureGrowEvent.class, Block.class, new Getter<>() {
			@Override
			public @NotNull Block get(final StructureGrowEvent e) {
				return e.getLocation().getBlock();
			}
		}, 0);
		EventValues.registerEventValue(StructureGrowEvent.class, Block[].class, new Getter<>() {
			@Override
			@Nullable
			public Block[] get(StructureGrowEvent event) {
				return event.getBlocks().stream()
					.map(BlockState::getBlock)
					.toArray(Block[]::new);
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(StructureGrowEvent.class, Block.class, new Getter<>() {
			@Override
			public @NotNull Block get(StructureGrowEvent event) {
				for (final BlockState bs : event.getBlocks()) {
					if (bs.getLocation().equals(event.getLocation()))
						return new BlockStateBlock(bs);
				}
				return event.getLocation().getBlock();
			}
		}, EventValues.TIME_FUTURE);
		EventValues.registerEventValue(StructureGrowEvent.class, Block[].class, new Getter<>() {
			@Override
			@Nullable
			public Block[] get(StructureGrowEvent event) {
				return event.getBlocks().stream()
					.map(BlockStateBlock::new)
					.toArray(Block[]::new);
			}
		}, EventValues.TIME_FUTURE);
		// WeatherEvent - not a WorldEvent (wtf ô_Ô)
		EventValues.registerEventValue(WeatherEvent.class, World.class, new Getter<>() {
			@Override
			public @NotNull World get(final WeatherEvent e) {
				return e.getWorld();
			}
		}, 0);
		// ChunkEvents
		EventValues.registerEventValue(ChunkEvent.class, Chunk.class, new Getter<>() {
			@Override
			public @NotNull Chunk get(final ChunkEvent e) {
				return e.getChunk();
			}
		}, 0);

		// === BlockEvents ===
		EventValues.registerEventValue(BlockEvent.class, Block.class, new Getter<>() {
			@Override
			public @NotNull Block get(final BlockEvent e) {
				return e.getBlock();
			}
		}, 0);
		EventValues.registerEventValue(BlockEvent.class, World.class, new Getter<>() {
			@Override
			public @NotNull World get(final BlockEvent e) {
				return e.getBlock().getWorld();
			}
		}, 0);
		// REMIND workaround of the event's location being at the entity in block events that have an entity event value
		EventValues.registerEventValue(BlockEvent.class, Location.class, new Getter<>() {
			@Override
			public @NotNull Location get(final BlockEvent e) {
				return e.getBlock().getLocation();
			}
		}, 0);
		// BlockPlaceEvent
		EventValues.registerEventValue(BlockPlaceEvent.class, Player.class, new Getter<>() {
			@Override
			public @NotNull Player get(final BlockPlaceEvent e) {
				return e.getPlayer();
			}
		}, 0);
		EventValues.registerEventValue(BlockPlaceEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public @NotNull ItemStack get(BlockPlaceEvent event) {
				return event.getItemInHand();
			}
		}, EventValues.TIME_PAST);
		EventValues.registerEventValue(BlockPlaceEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public @NotNull ItemStack get(BlockPlaceEvent event) {
				return event.getItemInHand();
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(BlockPlaceEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public @NotNull ItemStack get(BlockPlaceEvent event) {
				ItemStack item = event.getItemInHand().clone();
				if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
					item.setAmount(item.getAmount() - 1);
				return item;
			}
		}, EventValues.TIME_FUTURE);
		EventValues.registerEventValue(BlockPlaceEvent.class, Block.class, new Getter<>() {
			@Override
			public Block get(final BlockPlaceEvent e) {
				return new BlockStateBlock(e.getBlockReplacedState());
			}
		}, -1);
		EventValues.registerEventValue(BlockPlaceEvent.class, Direction.class, new Getter<>() {
			@Override
			public @NotNull Direction get(final BlockPlaceEvent e) {
				BlockFace bf = e.getBlockPlaced().getFace(e.getBlockAgainst());
				if (bf != null) {
					return new Direction(new double[]{bf.getModX(), bf.getModY(), bf.getModZ()});
				}
				return Direction.ZERO;
			}
		}, 0);
		// BlockFadeEvent
		EventValues.registerEventValue(BlockFadeEvent.class, Block.class, new Getter<>() {
			@Override
			public @NotNull Block get(final BlockFadeEvent e) {
				return e.getBlock();
			}
		}, -1);
		EventValues.registerEventValue(BlockFadeEvent.class, Block.class, new Getter<>() {
			@Override
			public Block get(final BlockFadeEvent e) {
				return new DelayedChangeBlock(e.getBlock(), e.getNewState());
			}
		}, 0);
		EventValues.registerEventValue(BlockFadeEvent.class, Block.class, new Getter<>() {
			@Override
			public Block get(final BlockFadeEvent e) {
				return new BlockStateBlock(e.getNewState());
			}
		}, 1);
		// BlockGrowEvent (+ BlockFormEvent)
		EventValues.registerEventValue(BlockGrowEvent.class, Block.class, new Getter<>() {
			@Override
			public @NotNull Block get(final BlockGrowEvent e) {
				return new BlockStateBlock(e.getNewState());
			}
		}, 0);
		EventValues.registerEventValue(BlockGrowEvent.class, Block.class, new Getter<>() {
			@Override
			public @NotNull Block get(final BlockGrowEvent e) {
				return e.getBlock();
			}
		}, -1);
		// BlockDamageEvent
		EventValues.registerEventValue(BlockDamageEvent.class, Player.class, new Getter<>() {
			@Override
			public @NotNull Player get(final BlockDamageEvent e) {
				return e.getPlayer();
			}
		}, 0);
		// BlockBreakEvent
		EventValues.registerEventValue(BlockBreakEvent.class, Player.class, new Getter<>() {
			@Override
			public @NotNull Player get(final BlockBreakEvent e) {
				return e.getPlayer();
			}
		}, 0);
		EventValues.registerEventValue(BlockBreakEvent.class, Block.class, new Getter<>() {
			@Override
			public @NotNull Block get(final BlockBreakEvent e) {
				return e.getBlock();
			}
		}, -1);
		EventValues.registerEventValue(BlockBreakEvent.class, Block.class, new Getter<>() {
			@Override
			public Block get(final BlockBreakEvent e) {
				return new DelayedChangeBlock(e.getBlock());
			}
		}, 0);
		EventValues.registerEventValue(BlockBreakEvent.class, Block.class, new Getter<>() {
			@Override
			public Block get(final BlockBreakEvent e) {
				final BlockState s = e.getBlock().getState();
				s.setType(s.getType() == Material.ICE ? Material.WATER : Material.AIR);
				s.setRawData((byte) 0);
				return new BlockStateBlock(s, true);
			}
		}, 1);
		// BlockFromToEvent
		EventValues.registerEventValue(BlockFromToEvent.class, Block.class, new Getter<>() {
			@Override
			public @NotNull Block get(final BlockFromToEvent e) {
				return e.getToBlock();
			}
		}, 1);
		// BlockIgniteEvent
		EventValues.registerEventValue(BlockIgniteEvent.class, Player.class, new Getter<>() {
			@Override
			@Nullable
			public Player get(final BlockIgniteEvent e) {
				return e.getPlayer();
			}
		}, 0);
		EventValues.registerEventValue(BlockIgniteEvent.class, Block.class, new Getter<>() {
			@Override
			@Nullable
			public Block get(final BlockIgniteEvent e) {
				return e.getIgnitingBlock();
			}
		}, 0);
		// BlockDispenseEvent
		EventValues.registerEventValue(BlockDispenseEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public @NotNull ItemStack get(final BlockDispenseEvent e) {
				return e.getItem();
			}
		}, 0);
		// BlockCanBuildEvent
		EventValues.registerEventValue(BlockCanBuildEvent.class, Block.class, new Getter<>() {
			@Override
			public @NotNull Block get(final BlockCanBuildEvent e) {
				return e.getBlock();
			}
		}, -1);
		EventValues.registerEventValue(BlockCanBuildEvent.class, Block.class, new Getter<>() {
			@Override
			public Block get(final BlockCanBuildEvent e) {
				final BlockState s = e.getBlock().getState();
				s.setType(e.getMaterial());
				return new BlockStateBlock(s, true);
			}
		}, 0);
		// BlockCanBuildEvent#getPlayer was added in 1.13
		if (Skript.methodExists(BlockCanBuildEvent.class, "getPlayer")) {
			EventValues.registerEventValue(BlockCanBuildEvent.class, Player.class, new Getter<>() {
				@Override
				@Nullable
				public Player get(final BlockCanBuildEvent e) {
					return e.getPlayer();
				}
			}, 0);
		}
		// SignChangeEvent
		EventValues.registerEventValue(SignChangeEvent.class, Player.class, new Getter<>() {
			@Override
			public @NotNull Player get(final SignChangeEvent e) {
				return e.getPlayer();
			}
		}, 0);
		EventValues.registerEventValue(SignChangeEvent.class, String[].class, new Getter<>() {
			@Override
			@Nullable
			public String[] get(SignChangeEvent event) {
				return event.getLines();
			}
		}, EventValues.TIME_NOW);

		// === EntityEvents ===
		EventValues.registerEventValue(EntityEvent.class, Entity.class, new Getter<>() {
			@Override
			public @NotNull Entity get(final EntityEvent e) {
				return e.getEntity();
			}
		}, 0, "Use 'attacker' and/or 'victim' in damage/death events", EntityDamageEvent.class, EntityDeathEvent.class);
		EventValues.registerEventValue(EntityEvent.class, CommandSender.class, new Getter<>() {
			@Override
			public @NotNull CommandSender get(final EntityEvent e) {
				return e.getEntity();
			}
		}, 0, "Use 'attacker' and/or 'victim' in damage/death events", EntityDamageEvent.class, EntityDeathEvent.class);
		EventValues.registerEventValue(EntityEvent.class, World.class, new Getter<>() {
			@Override
			public @NotNull World get(final EntityEvent e) {
				return e.getEntity().getWorld();
			}
		}, 0);
		EventValues.registerEventValue(EntityEvent.class, Location.class, new Getter<>() {
			@Override
			public @NotNull Location get(final EntityEvent e) {
				return e.getEntity().getLocation();
			}
		}, 0);
		// EntityDamageEvent
		EventValues.registerEventValue(EntityDamageEvent.class, DamageCause.class, new Getter<>() {
			@Override
			public @NotNull DamageCause get(final EntityDamageEvent e) {
				return e.getCause();
			}
		}, 0);
		EventValues.registerEventValue(EntityDamageByEntityEvent.class, Projectile.class, new Getter<>() {
			@Override
			@Nullable
			public Projectile get(final EntityDamageByEntityEvent e) {
				if (e.getDamager() instanceof Projectile)
					return (Projectile) e.getDamager();
				return null;
			}
		}, 0);
		// EntityDeathEvent
		EventValues.registerEventValue(EntityDeathEvent.class, ItemStack[].class, new Getter<>() {
			@Override
			@Nullable
			public ItemStack[] get(EntityDeathEvent event) {
				return event.getDrops().toArray(new ItemStack[0]);
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(EntityDeathEvent.class, Projectile.class, new Getter<>() {
			@Override
			@Nullable
			public Projectile get(final EntityDeathEvent e) {
				final EntityDamageEvent ldc = e.getEntity().getLastDamageCause();
				if (ldc instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) ldc).getDamager() instanceof Projectile)
					return (Projectile) ((EntityDamageByEntityEvent) ldc).getDamager();
				return null;
			}
		}, 0);
		EventValues.registerEventValue(EntityDeathEvent.class, DamageCause.class, new Getter<>() {
			@Override
			@Nullable
			public DamageCause get(final EntityDeathEvent e) {
				final EntityDamageEvent ldc = e.getEntity().getLastDamageCause();
				return ldc == null ? null : ldc.getCause();
			}
		}, 0);
		// ProjectileHitEvent
		// ProjectileHitEvent#getHitBlock was added in 1.11
		if (Skript.methodExists(ProjectileHitEvent.class, "getHitBlock"))
			EventValues.registerEventValue(ProjectileHitEvent.class, Block.class, new Getter<>() {
				@Nullable
				@Override
				public Block get(ProjectileHitEvent e) {
					return e.getHitBlock();
				}
			}, 0);
		EventValues.registerEventValue(ProjectileHitEvent.class, Entity.class, new Getter<>() {
			@Override
			public @NotNull Entity get(final ProjectileHitEvent e) {
				assert false;
				return e.getEntity();
			}
		}, 0, "Use 'projectile' and/or 'shooter' in projectile hit events", ProjectileHitEvent.class);
		EventValues.registerEventValue(ProjectileHitEvent.class, Projectile.class, new Getter<>() {
			@Override
			public @NotNull Projectile get(final ProjectileHitEvent e) {
				return e.getEntity();
			}
		}, 0);
		if (Skript.methodExists(ProjectileHitEvent.class, "getHitBlockFace")) {
			EventValues.registerEventValue(ProjectileHitEvent.class, Direction.class, new Getter<>() {
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
		EventValues.registerEventValue(ProjectileLaunchEvent.class, Entity.class, new Getter<>() {
			@Override
			public @NotNull Entity get(final ProjectileLaunchEvent e) {
				assert false;
				return e.getEntity();
			}
		}, 0, "Use 'projectile' and/or 'shooter' in shoot events", ProjectileLaunchEvent.class);
		//ProjectileCollideEvent
		if (Skript.classExists("com.destroystokyo.paper.event.entity.ProjectileCollideEvent")) {
			EventValues.registerEventValue(ProjectileCollideEvent.class, Projectile.class, new Getter<>() {
				@Override
				public @NotNull Projectile get(ProjectileCollideEvent evt) {
					return evt.getEntity();
				}
			}, 0);
			EventValues.registerEventValue(ProjectileCollideEvent.class, Entity.class, new Getter<>() {
				@Override
				public @NotNull Entity get(ProjectileCollideEvent evt) {
					return evt.getCollidedWith();
				}
			}, 0);
		}
		EventValues.registerEventValue(ProjectileLaunchEvent.class, Projectile.class, new Getter<>() {
			@Override
			public @NotNull Projectile get(final ProjectileLaunchEvent e) {
				return e.getEntity();
			}
		}, 0);
		// EntityTameEvent
		EventValues.registerEventValue(EntityTameEvent.class, Entity.class, new Getter<>() {
			@Override
			public @NotNull Entity get(final EntityTameEvent e) {
				return e.getEntity();
			}
		}, 0);

		// EntityTeleportEvent
		EventValues.registerEventValue(EntityTeleportEvent.class, Location.class, new Getter<>() {
			@Override
			public @NotNull Location get(EntityTeleportEvent event) {
				return event.getFrom();
			}
		}, EventValues.TIME_PAST);
		EventValues.registerEventValue(EntityTeleportEvent.class, Location.class, new Getter<>() {
			@Override
			public @Nullable Location get(EntityTeleportEvent event) {
				return event.getTo();
			}
		}, EventValues.TIME_NOW);

		// EntityChangeBlockEvent
		EventValues.registerEventValue(EntityChangeBlockEvent.class, Block.class, new Getter<>() {
			@Override
			public @NotNull Block get(EntityChangeBlockEvent event) {
				return event.getBlock();
			}
		}, EventValues.TIME_PAST);
		EventValues.registerEventValue(EntityChangeBlockEvent.class, Block.class, new Getter<>() {
			@Override
			public @NotNull Block get(EntityChangeBlockEvent event) {
				return event.getBlock();
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(EntityChangeBlockEvent.class, BlockData.class, new Getter<>() {
			@Override
			public @NotNull BlockData get(EntityChangeBlockEvent event) {
				return event.getBlockData();
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(EntityChangeBlockEvent.class, BlockData.class, new Getter<>() {
			@Override
			public @NotNull BlockData get(EntityChangeBlockEvent event) {
				return event.getBlockData();
			}
		}, EventValues.TIME_FUTURE);

		// AreaEffectCloudApplyEvent
		EventValues.registerEventValue(AreaEffectCloudApplyEvent.class, LivingEntity[].class, new Getter<>() {
			@Override
			@Nullable
			public LivingEntity[] get(AreaEffectCloudApplyEvent event) {
				return event.getAffectedEntities().toArray(new LivingEntity[0]);
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(AreaEffectCloudApplyEvent.class, PotionEffectType.class, new Getter<>() {
			private final boolean HAS_POTION_TYPE_METHOD = Skript.methodExists(AreaEffectCloud.class, "getBasePotionType");

			@Override
			@Nullable
			public PotionEffectType get(AreaEffectCloudApplyEvent e) {
				// TODO needs to be reworked to support multiple values (there can be multiple potion effects)
				if (HAS_POTION_TYPE_METHOD) {
					PotionType base = e.getEntity().getBasePotionType();
					if (base != null)
						return base.getEffectType();
				} else {
					//noinspection removal,DataFlowIssue
					return e.getEntity().getBasePotionData().getType().getEffectType();
				}
				return null;
			}
		}, 0);
		// ItemSpawnEvent
		EventValues.registerEventValue(ItemSpawnEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public @NotNull ItemStack get(final ItemSpawnEvent e) {
				return e.getEntity().getItemStack();
			}
		}, 0);
		// LightningStrikeEvent
		EventValues.registerEventValue(LightningStrikeEvent.class, Entity.class, new Getter<>() {
			@Override
			public Entity get(LightningStrikeEvent event) {
				return event.getLightning();
			}
		}, 0);
		// EndermanAttackPlayerEvent
		if (Skript.classExists("com.destroystokyo.paper.event.entity.EndermanAttackPlayerEvent")) {
			EventValues.registerEventValue(EndermanAttackPlayerEvent.class, Player.class, new Getter<>() {
				@Override
				public Player get(EndermanAttackPlayerEvent event) {
					return event.getPlayer();
				}
			}, EventValues.TIME_NOW);
		}

		// --- PlayerEvents ---
		EventValues.registerEventValue(PlayerEvent.class, Player.class, new Getter<>() {
			@Override
			public @NotNull Player get(final PlayerEvent e) {
				return e.getPlayer();
			}
		}, 0);
		EventValues.registerEventValue(PlayerEvent.class, World.class, new Getter<>() {
			@Override
			public @NotNull World get(final PlayerEvent e) {
				return e.getPlayer().getWorld();
			}
		}, 0);
		// PlayerBedEnterEvent
		EventValues.registerEventValue(PlayerBedEnterEvent.class, Block.class, new Getter<>() {
			@Override
			public @NotNull Block get(final PlayerBedEnterEvent e) {
				return e.getBed();
			}
		}, 0);
		// PlayerBedLeaveEvent
		EventValues.registerEventValue(PlayerBedLeaveEvent.class, Block.class, new Getter<>() {
			@Override
			public @NotNull Block get(final PlayerBedLeaveEvent e) {
				return e.getBed();
			}
		}, 0);
		// PlayerBucketEvents
		EventValues.registerEventValue(PlayerBucketFillEvent.class, Block.class, new Getter<>() {
			@Override
			public @NotNull Block get(final PlayerBucketFillEvent e) {
				return e.getBlockClicked();
			}
		}, 0);
		EventValues.registerEventValue(PlayerBucketFillEvent.class, Block.class, new Getter<>() {
			@Override
			public @NotNull Block get(final PlayerBucketFillEvent e) {
				final BlockState s = e.getBlockClicked().getState();
				s.setType(Material.AIR);
				s.setRawData((byte) 0);
				return new BlockStateBlock(s, true);
			}
		}, 1);
		EventValues.registerEventValue(PlayerBucketEmptyEvent.class, Block.class, new Getter<>() {
			@Override
			public @NotNull Block get(final PlayerBucketEmptyEvent e) {
				return e.getBlockClicked().getRelative(e.getBlockFace());
			}
		}, -1);
		EventValues.registerEventValue(PlayerBucketEmptyEvent.class, Block.class, new Getter<>() {
			@Override
			public Block get(final PlayerBucketEmptyEvent e) {
				final BlockState s = e.getBlockClicked().getRelative(e.getBlockFace()).getState();
				s.setType(e.getBucket() == Material.WATER_BUCKET ? Material.WATER : Material.LAVA);
				s.setRawData((byte) 0);
				return new BlockStateBlock(s, true);
			}
		}, 0);
		// PlayerDropItemEvent
		EventValues.registerEventValue(PlayerDropItemEvent.class, Player.class, new Getter<>() {
			@Override
			public @NotNull Player get(PlayerDropItemEvent e) {
				return e.getPlayer();
			}
		}, 0);
		EventValues.registerEventValue(PlayerDropItemEvent.class, Item.class, new Getter<>() {
			@Override
			public @NotNull Item get(final PlayerDropItemEvent e) {
				return e.getItemDrop();
			}
		}, 0);
		EventValues.registerEventValue(PlayerDropItemEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public @NotNull ItemStack get(final PlayerDropItemEvent e) {
				return e.getItemDrop().getItemStack();
			}
		}, 0);
		// EntityDropItemEvent
		EventValues.registerEventValue(EntityDropItemEvent.class, Item.class, new Getter<>() {
			@Override
			public Item get(EntityDropItemEvent event) {
				return event.getItemDrop();
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(EntityDropItemEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public ItemStack get(EntityDropItemEvent event) {
				return event.getItemDrop().getItemStack();
			}
		}, EventValues.TIME_NOW);
		// PlayerPickupItemEvent
		EventValues.registerEventValue(PlayerPickupItemEvent.class, Player.class, new Getter<>() {
			@Override
			public @NotNull Player get(PlayerPickupItemEvent e) {
				return e.getPlayer();
			}
		}, 0);
		EventValues.registerEventValue(PlayerPickupItemEvent.class, Item.class, new Getter<>() {
			@Override
			public @NotNull Item get(final PlayerPickupItemEvent e) {
				return e.getItem();
			}
		}, 0);
		EventValues.registerEventValue(PlayerPickupItemEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public @NotNull ItemStack get(final PlayerPickupItemEvent e) {
				return e.getItem().getItemStack();
			}
		}, 0);
		// EntityPickupItemEvent
		EventValues.registerEventValue(EntityPickupItemEvent.class, Entity.class, new Getter<>() {
			@Override
			public @NotNull Entity get(EntityPickupItemEvent e) {
				return e.getEntity();
			}
		}, 0);
		EventValues.registerEventValue(EntityPickupItemEvent.class, Item.class, new Getter<>() {
			@Override
			public @NotNull Item get(final EntityPickupItemEvent e) {
				return e.getItem();
			}
		}, 0);
		// PlayerItemConsumeEvent
		EventValues.registerEventValue(PlayerItemConsumeEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public @NotNull ItemStack get(final PlayerItemConsumeEvent e) {
				return e.getItem();
			}
		}, 0);
		// PlayerItemBreakEvent
		EventValues.registerEventValue(PlayerItemBreakEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public @NotNull ItemStack get(final PlayerItemBreakEvent e) {
				return e.getBrokenItem();
			}
		}, 0);
		// PlayerInteractEntityEvent
		EventValues.registerEventValue(PlayerInteractEntityEvent.class, Entity.class, new Getter<>() {
			@Override
			public @NotNull Entity get(final PlayerInteractEntityEvent e) {
				return e.getRightClicked();
			}
		}, 0);
		EventValues.registerEventValue(PlayerInteractEntityEvent.class, ItemStack.class, new Getter<>() {
			@Override
			@Nullable
			public ItemStack get(final PlayerInteractEntityEvent e) {
				EquipmentSlot hand = e.getHand();
				if (hand == EquipmentSlot.HAND)
					return e.getPlayer().getInventory().getItemInMainHand();
				else if (hand == EquipmentSlot.OFF_HAND)
					return e.getPlayer().getInventory().getItemInOffHand();
				else
					return null;
			}
		}, 0);
		// PlayerInteractEvent
		EventValues.registerEventValue(PlayerInteractEvent.class, ItemStack.class, new Getter<>() {
			@Override
			@Nullable
			public ItemStack get(final PlayerInteractEvent e) {
				return e.getItem();
			}
		}, 0);
		EventValues.registerEventValue(PlayerInteractEvent.class, Block.class, new Getter<>() {
			@Override
			@Nullable
			public Block get(final PlayerInteractEvent e) {
				return e.getClickedBlock();
			}
		}, 0);
		EventValues.registerEventValue(PlayerInteractEvent.class, Direction.class, new Getter<>() {
			@Override
			public @NotNull Direction get(final PlayerInteractEvent e) {
				return new Direction(new double[]{e.getBlockFace().getModX(), e.getBlockFace().getModY(), e.getBlockFace().getModZ()});
			}
		}, 0);
		// PlayerShearEntityEvent
		EventValues.registerEventValue(PlayerShearEntityEvent.class, Entity.class, new Getter<>() {
			@Override
			public @NotNull Entity get(final PlayerShearEntityEvent e) {
				return e.getEntity();
			}
		}, 0);
		// PlayerMoveEvent
		EventValues.registerEventValue(PlayerMoveEvent.class, Block.class, new Getter<>() {
			@Override
			public Block get(PlayerMoveEvent event) {
				return event.getTo().clone().subtract(0, 0.5, 0).getBlock();
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(PlayerMoveEvent.class, Location.class, new Getter<>() {
			@Override
			public Location get(PlayerMoveEvent event) {
				return event.getFrom();
			}
		}, EventValues.TIME_PAST);
		EventValues.registerEventValue(PlayerMoveEvent.class, Location.class, new Getter<>() {
			@Override
			public Location get(PlayerMoveEvent event) {
				return event.getTo();
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(PlayerMoveEvent.class, Chunk.class, new Getter<>() {
			@Override
			public Chunk get(PlayerMoveEvent event) {
				return event.getFrom().getChunk();
			}
		}, EventValues.TIME_PAST);
		EventValues.registerEventValue(PlayerMoveEvent.class, Chunk.class, new Getter<>() {
			@Override
			public Chunk get(PlayerMoveEvent event) {
				return event.getTo().getChunk();
			}
		}, EventValues.TIME_NOW);
		// PlayerItemDamageEvent
		EventValues.registerEventValue(PlayerItemDamageEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public ItemStack get(PlayerItemDamageEvent event) {
				return event.getItem();
			}
		}, 0);
		//PlayerItemMendEvent
		EventValues.registerEventValue(PlayerItemMendEvent.class, Player.class, new Getter<>() {
			@Override
			public @NotNull Player get(PlayerItemMendEvent e) {
				return e.getPlayer();
			}
		}, 0);
		EventValues.registerEventValue(PlayerItemMendEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public @NotNull ItemStack get(PlayerItemMendEvent e) {
				return e.getItem();
			}
		}, 0);
		EventValues.registerEventValue(PlayerItemMendEvent.class, Entity.class, new Getter<>() {
			@Override
			public @NotNull Entity get(PlayerItemMendEvent e) {
				return e.getExperienceOrb();
			}
		}, 0);

		// --- HangingEvents ---

		// Note: will not work in HangingEntityBreakEvent due to event-entity being parsed as HangingBreakByEntityEvent#getRemover() from code down below
		EventValues.registerEventValue(HangingEvent.class, Hanging.class, new Getter<>() {
			@Override
			public @NotNull Hanging get(final HangingEvent e) {
				return e.getEntity();
			}
		}, 0);
		EventValues.registerEventValue(HangingEvent.class, World.class, new Getter<>() {
			@Override
			public @NotNull World get(final HangingEvent e) {
				return e.getEntity().getWorld();
			}
		}, 0);
		EventValues.registerEventValue(HangingEvent.class, Location.class, new Getter<>() {
			@Override
			public @NotNull Location get(final HangingEvent e) {
				return e.getEntity().getLocation();
			}
		}, 0);

		// HangingBreakEvent
		EventValues.registerEventValue(HangingBreakEvent.class, Entity.class, new Getter<>() {
			@Nullable
			@Override
			public Entity get(HangingBreakEvent e) {
				if (e instanceof HangingBreakByEntityEvent)
					return ((HangingBreakByEntityEvent) e).getRemover();
				return null;
			}
		}, 0);
		// HangingPlaceEvent
		EventValues.registerEventValue(HangingPlaceEvent.class, Player.class, new Getter<>() {
			@Override
			@Nullable
			public Player get(final HangingPlaceEvent e) {
				return e.getPlayer();
			}
		}, 0);

		// --- VehicleEvents ---
		EventValues.registerEventValue(VehicleEvent.class, Vehicle.class, new Getter<>() {
			@Override
			public @NotNull Vehicle get(final VehicleEvent e) {
				return e.getVehicle();
			}
		}, 0);
		EventValues.registerEventValue(VehicleEvent.class, World.class, new Getter<>() {
			@Override
			public @NotNull World get(final VehicleEvent e) {
				return e.getVehicle().getWorld();
			}
		}, 0);
		EventValues.registerEventValue(VehicleExitEvent.class, LivingEntity.class, new Getter<>() {
			@Override
			public @NotNull LivingEntity get(final VehicleExitEvent e) {
				return e.getExited();
			}
		}, 0);

		EventValues.registerEventValue(VehicleEnterEvent.class, Entity.class, new Getter<>() {
			@Override
			public @NotNull Entity get(VehicleEnterEvent e) {
				return e.getEntered();
			}
		}, 0);

		// We could error here instead but it's preferable to not do it in this case
		EventValues.registerEventValue(VehicleDamageEvent.class, Entity.class, new Getter<>() {
			@Nullable
			@Override
			public Entity get(VehicleDamageEvent e) {
				return e.getAttacker();
			}
		}, 0);

		EventValues.registerEventValue(VehicleDestroyEvent.class, Entity.class, new Getter<>() {
			@Nullable
			@Override
			public Entity get(VehicleDestroyEvent e) {
				return e.getAttacker();
			}
		}, 0);

		EventValues.registerEventValue(VehicleEvent.class, Entity.class, new Getter<>() {
			@Override
			@Nullable
			public Entity get(final VehicleEvent e) {
				return e.getVehicle().getPassenger();
			}
		}, 0);


		// === CommandEvents ===
		// PlayerCommandPreprocessEvent is a PlayerEvent
		EventValues.registerEventValue(ServerCommandEvent.class, CommandSender.class, new Getter<>() {
			@Override
			public @NotNull CommandSender get(final ServerCommandEvent e) {
				return e.getSender();
			}
		}, 0);
		EventValues.registerEventValue(CommandEvent.class, String[].class, new Getter<>() {
			@Override
			public String[] get(CommandEvent event) {
				return event.getArgs();
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(CommandEvent.class, CommandSender.class, new Getter<>() {
			@Override
			public CommandSender get(final CommandEvent e) {
				return e.getSender();
			}
		}, 0);
		EventValues.registerEventValue(CommandEvent.class, World.class, new Getter<>() {
			@Override
			@Nullable
			public World get(final CommandEvent e) {
				return e.getSender() instanceof Player ? ((Player) e.getSender()).getWorld() : null;
			}
		}, 0);

		// === ServerEvents ===
		// Script load/unload event
		EventValues.registerEventValue(ScriptEvent.class, CommandSender.class, new Getter<>() {
			@Override
			public @NotNull CommandSender get(ScriptEvent e) {
				return Bukkit.getConsoleSender();
			}
		}, 0);
		// Server load event
		EventValues.registerEventValue(SkriptStartEvent.class, CommandSender.class, new Getter<>() {
			@Override
			public @NotNull CommandSender get(SkriptStartEvent e) {
				return Bukkit.getConsoleSender();
			}
		}, 0);
		// Server stop event
		EventValues.registerEventValue(SkriptStopEvent.class, CommandSender.class, new Getter<>() {
			@Override
			public @NotNull CommandSender get(SkriptStopEvent e) {
				return Bukkit.getConsoleSender();
			}
		}, 0);

		// === InventoryEvents ===
		// InventoryClickEvent
		EventValues.registerEventValue(InventoryClickEvent.class, Player.class, new Getter<>() {
			@Override
			@Nullable
			public Player get(final InventoryClickEvent e) {
				return e.getWhoClicked() instanceof Player ? (Player) e.getWhoClicked() : null;
			}
		}, 0);
		EventValues.registerEventValue(InventoryClickEvent.class, World.class, new Getter<>() {
			@Override
			public @NotNull World get(final InventoryClickEvent e) {
				return e.getWhoClicked().getWorld();
			}
		}, 0);
		EventValues.registerEventValue(InventoryClickEvent.class, ItemStack.class, new Getter<>() {
			@Override
			@Nullable
			public ItemStack get(final InventoryClickEvent e) {
				if (e instanceof CraftItemEvent)
					return ((CraftItemEvent) e).getRecipe().getResult();
				return e.getCurrentItem();
			}
		}, 0);
		EventValues.registerEventValue(InventoryClickEvent.class, InventoryAction.class, new Getter<>() {
			@Override
			public @NotNull InventoryAction get(final InventoryClickEvent e) {
				return e.getAction();
			}
		}, 0);
		EventValues.registerEventValue(InventoryClickEvent.class, ClickType.class, new Getter<>() {
			@Override
			public @NotNull ClickType get(final InventoryClickEvent e) {
				return e.getClick();
			}
		}, 0);
		EventValues.registerEventValue(InventoryClickEvent.class, Inventory.class, new Getter<>() {
			@Override
			@Nullable
			public Inventory get(final InventoryClickEvent e) {
				return e.getClickedInventory();
			}
		}, 0);
		// InventoryDragEvent
		EventValues.registerEventValue(InventoryDragEvent.class, Player.class, new Getter<>() {
			@Override
			@Nullable
			public Player get(InventoryDragEvent event) {
				return event.getWhoClicked() instanceof Player ? (Player) event.getWhoClicked() : null;
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(InventoryDragEvent.class, World.class, new Getter<>() {
			@Override
			public @NotNull World get(InventoryDragEvent event) {
				return event.getWhoClicked().getWorld();
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(InventoryDragEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public @NotNull ItemStack get(InventoryDragEvent event) {
				return event.getOldCursor();
			}
		}, EventValues.TIME_PAST);
		EventValues.registerEventValue(InventoryDragEvent.class, ItemStack.class, new Getter<>() {
			@Override
			@Nullable
			public ItemStack get(InventoryDragEvent event) {
				return event.getCursor();
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(InventoryDragEvent.class, ItemStack[].class, new Getter<>() {
			@Override
			@Nullable
			public ItemStack[] get(InventoryDragEvent event) {
				return event.getNewItems().values().toArray(new ItemStack[0]);
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(InventoryDragEvent.class, ClickType.class, new Getter<>() {
			@Override
			public @NotNull ClickType get(InventoryDragEvent event) {
				return event.getType() == DragType.EVEN ? ClickType.LEFT : ClickType.RIGHT;
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(InventoryDragEvent.class, Inventory[].class, new Getter<>() {
			@Override
			@Nullable
			public Inventory[] get(InventoryDragEvent event) {
				Set<Inventory> inventories = new HashSet<>();
				InventoryView view = event.getView();
				for (Integer rawSlot : event.getRawSlots()) {
					Inventory inventory = InventoryUtils.getInventory(view, rawSlot);
					if (inventory != null)
						inventories.add(inventory);
				}
				return inventories.toArray(new Inventory[0]);
			}
		}, EventValues.TIME_NOW);
		// PrepareAnvilEvent
		EventValues.registerEventValue(PrepareAnvilEvent.class, ItemStack.class, new Getter<>() {
			@Override
			@Nullable
			public ItemStack get(PrepareAnvilEvent e) {
				return e.getResult();
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(PrepareAnvilEvent.class, Inventory.class, new Getter<>() {
			@Override
			public @NotNull Inventory get(PrepareAnvilEvent e) {
				return e.getInventory();
			}
		}, EventValues.TIME_NOW);
		// AnvilDamagedEvent
		if (Skript.classExists("com.destroystokyo.paper.event.block.AnvilDamagedEvent")) {
			EventValues.registerEventValue(AnvilDamagedEvent.class, Inventory.class, new Getter<>() {
				@Override
				public @NotNull Inventory get(AnvilDamagedEvent e) {
					return e.getInventory();
				}
			}, EventValues.TIME_NOW);
		}
		//BlockFertilizeEvent
		EventValues.registerEventValue(BlockFertilizeEvent.class, Player.class, new Getter<>() {
			@Nullable
			@Override
			public Player get(BlockFertilizeEvent event) {
				return event.getPlayer();
			}
		}, 0);
		EventValues.registerEventValue(BlockFertilizeEvent.class, Block[].class, new Getter<>() {
			@Nullable
			@Override
			public Block[] get(BlockFertilizeEvent event) {
				return event.getBlocks().stream()
					.map(BlockState::getBlock)
					.toArray(Block[]::new);
			}
		}, EventValues.TIME_NOW);
		// PrepareItemCraftEvent
		EventValues.registerEventValue(PrepareItemCraftEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public ItemStack get(PrepareItemCraftEvent e) {
				ItemStack item = e.getInventory().getResult();
				return item != null ? item : AIR_IS;
			}
		}, 0);
		EventValues.registerEventValue(PrepareItemCraftEvent.class, Inventory.class, new Getter<>() {
			@Override
			public Inventory get(PrepareItemCraftEvent e) {
				return e.getInventory();
			}
		}, 0);
		EventValues.registerEventValue(PrepareItemCraftEvent.class, Player.class, new Getter<>() {
			@Override
			@Nullable
			public Player get(final PrepareItemCraftEvent e) {
				List<HumanEntity> viewers = e.getInventory().getViewers(); // Get all viewers
				if (viewers.isEmpty()) // ... if we don't have any
					return null;
				HumanEntity first = viewers.getFirst(); // Get first viewer and hope it is crafter
				if (first instanceof Player) // Needs to be player... Usually it is
					return (Player) first;
				return null;
			}
		}, 0);
		// CraftEvents - recipe namespaced key strings
		EventValues.registerEventValue(CraftItemEvent.class, String.class, new Getter<>() {
			@Nullable
			@Override
			public String get(CraftItemEvent e) {
				Recipe recipe = e.getRecipe();
				if (recipe instanceof Keyed)
					return ((Keyed) recipe).getKey().toString();
				return null;
			}
		}, 0);
		EventValues.registerEventValue(PrepareItemCraftEvent.class, String.class, new Getter<>() {
			@Nullable
			@Override
			public String get(PrepareItemCraftEvent e) {
				Recipe recipe = e.getRecipe();
				if (recipe instanceof Keyed)
					return ((Keyed) recipe).getKey().toString();
				return null;
			}
		}, 0);
		// CraftItemEvent
		EventValues.registerEventValue(CraftItemEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public @NotNull ItemStack get(CraftItemEvent e) {
				return e.getRecipe().getResult();
			}
		}, 0);
		//InventoryOpenEvent
		EventValues.registerEventValue(InventoryOpenEvent.class, Player.class, new Getter<>() {
			@Override
			public @NotNull Player get(final InventoryOpenEvent e) {
				return (Player) e.getPlayer();
			}
		}, 0);
		EventValues.registerEventValue(InventoryOpenEvent.class, Inventory.class, new Getter<>() {
			@Override
			public @NotNull Inventory get(final InventoryOpenEvent e) {
				return e.getInventory();
			}
		}, 0);
		//InventoryCloseEvent
		EventValues.registerEventValue(InventoryCloseEvent.class, Player.class, new Getter<>() {
			@Override
			public @NotNull Player get(final InventoryCloseEvent e) {
				return (Player) e.getPlayer();
			}
		}, 0);
		EventValues.registerEventValue(InventoryCloseEvent.class, Inventory.class, new Getter<>() {
			@Override
			public @NotNull Inventory get(final InventoryCloseEvent e) {
				return e.getInventory();
			}
		}, 0);
		if (Skript.classExists("org.bukkit.event.inventory.InventoryCloseEvent$Reason"))
			EventValues.registerEventValue(InventoryCloseEvent.class, InventoryCloseEvent.Reason.class, new Getter<>() {
				@Override
				public InventoryCloseEvent.Reason get(InventoryCloseEvent event) {
					return event.getReason();
				}
			}, EventValues.TIME_NOW);
		//InventoryPickupItemEvent
		EventValues.registerEventValue(InventoryPickupItemEvent.class, Inventory.class, new Getter<>() {
			@Override
			public @NotNull Inventory get(InventoryPickupItemEvent event) {
				return event.getInventory();
			}
		}, 0);
		EventValues.registerEventValue(InventoryPickupItemEvent.class, Item.class, new Getter<>() {
			@Override
			public @NotNull Item get(InventoryPickupItemEvent event) {
				return event.getItem();
			}
		}, 0);
		EventValues.registerEventValue(InventoryPickupItemEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public @NotNull ItemStack get(InventoryPickupItemEvent event) {
				return event.getItem().getItemStack();
			}
		}, 0);
		//PortalCreateEvent
		EventValues.registerEventValue(PortalCreateEvent.class, World.class, new Getter<>() {
			@Override
			public @NotNull World get(final PortalCreateEvent e) {
				return e.getWorld();
			}
		}, 0);
		EventValues.registerEventValue(PortalCreateEvent.class, Block[].class, new Getter<>() {
			@Override
			@Nullable
			public Block[] get(PortalCreateEvent event) {
				return event.getBlocks().stream()
					.map(BlockState::getBlock)
					.toArray(Block[]::new);
			}
		}, EventValues.TIME_NOW);
		if (Skript.methodExists(PortalCreateEvent.class, "getEntity")) { // Minecraft 1.14+
			EventValues.registerEventValue(PortalCreateEvent.class, Entity.class, new Getter<>() {
				@Override
				@Nullable
				public Entity get(final PortalCreateEvent e) {
					return e.getEntity();
				}
			}, 0);
		}
		//PlayerEditBookEvent
		EventValues.registerEventValue(PlayerEditBookEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public ItemStack get(PlayerEditBookEvent event) {
				ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
				book.setItemMeta(event.getPreviousBookMeta());
				return book;
			}
		}, EventValues.TIME_PAST);
		EventValues.registerEventValue(PlayerEditBookEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public ItemStack get(PlayerEditBookEvent event) {
				ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
				book.setItemMeta(event.getNewBookMeta());
				return book;
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(PlayerEditBookEvent.class, String[].class, new Getter<>() {
			@Override
			public String[] get(PlayerEditBookEvent event) {
				return event.getPreviousBookMeta().getPages().toArray(new String[0]);
			}
		}, EventValues.TIME_PAST);
		EventValues.registerEventValue(PlayerEditBookEvent.class, String[].class, new Getter<>() {
			@Override
			public String[] get(PlayerEditBookEvent event) {
				return event.getNewBookMeta().getPages().toArray(new String[0]);
			}
		}, EventValues.TIME_NOW);
		//ItemDespawnEvent
		EventValues.registerEventValue(ItemDespawnEvent.class, Item.class, new Getter<>() {
			@Override
			public @NotNull Item get(ItemDespawnEvent e) {
				return e.getEntity();
			}
		}, 0);
		EventValues.registerEventValue(ItemDespawnEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public @NotNull ItemStack get(ItemDespawnEvent e) {
				return e.getEntity().getItemStack();
			}
		}, 0);
		//ItemMergeEvent
		EventValues.registerEventValue(ItemMergeEvent.class, Item.class, new Getter<>() {
			@Override
			public @NotNull Item get(ItemMergeEvent e) {
				return e.getEntity();
			}
		}, 0);
		EventValues.registerEventValue(ItemMergeEvent.class, Item.class, new Getter<>() {
			@Override
			public @NotNull Item get(ItemMergeEvent e) {
				return e.getTarget();
			}
		}, 1);
		EventValues.registerEventValue(ItemMergeEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public @NotNull ItemStack get(ItemMergeEvent e) {
				return e.getEntity().getItemStack();
			}
		}, 0);
		//PlayerTeleportEvent
		EventValues.registerEventValue(PlayerTeleportEvent.class, TeleportCause.class, new Getter<>() {
			@Override
			public @NotNull TeleportCause get(final PlayerTeleportEvent e) {
				return e.getCause();
			}
		}, 0);
		//EntityMoveEvent
		if (Skript.classExists("io.papermc.paper.event.entity.EntityMoveEvent")) {
			EventValues.registerEventValue(EntityMoveEvent.class, Location.class, new Getter<>() {
				@Override
				public @NotNull Location get(EntityMoveEvent e) {
					return e.getFrom();
				}
			}, EventValues.TIME_NOW);
			EventValues.registerEventValue(EntityMoveEvent.class, Location.class, new Getter<>() {
				@Override
				public @NotNull Location get(EntityMoveEvent e) {
					return e.getTo();
				}
			}, EventValues.TIME_FUTURE);
		}
		//PlayerToggleFlightEvent
		EventValues.registerEventValue(PlayerToggleFlightEvent.class, Player.class, new Getter<>() {
			@Override
			public @NotNull Player get(PlayerToggleFlightEvent e) {
				return e.getPlayer();
			}
		}, 0);
		//CreatureSpawnEvent
		EventValues.registerEventValue(CreatureSpawnEvent.class, SpawnReason.class, new Getter<>() {
			@Override
			public @NotNull SpawnReason get(CreatureSpawnEvent e) {
				return e.getSpawnReason();
			}
		}, 0);
		//FireworkExplodeEvent
		EventValues.registerEventValue(FireworkExplodeEvent.class, Firework.class, new Getter<>() {
			@Override
			public @NotNull Firework get(FireworkExplodeEvent e) {
				return e.getEntity();
			}
		}, 0);
		EventValues.registerEventValue(FireworkExplodeEvent.class, FireworkEffect.class, new Getter<>() {
			@Override
			@Nullable
			public FireworkEffect get(FireworkExplodeEvent e) {
				List<FireworkEffect> effects = e.getEntity().getFireworkMeta().getEffects();
				if (effects.isEmpty())
					return null;
				return effects.getFirst();
			}
		}, 0);
		//PlayerRiptideEvent
		EventValues.registerEventValue(PlayerRiptideEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public ItemStack get(PlayerRiptideEvent e) {
				return e.getItem();
			}
		}, 0);
		//PlayerArmorChangeEvent
		if (Skript.classExists("com.destroystokyo.paper.event.player.PlayerArmorChangeEvent")) {
			EventValues.registerEventValue(PlayerArmorChangeEvent.class, ItemStack.class, new Getter<>() {
				@Override
				public @NotNull ItemStack get(PlayerArmorChangeEvent e) {
					return e.getNewItem();
				}
			}, 0);
		}
		//PlayerInventorySlotChangeEvent
		if (Skript.classExists("io.papermc.paper.event.player.PlayerInventorySlotChangeEvent")) {
			EventValues.registerEventValue(PlayerInventorySlotChangeEvent.class, ItemStack.class, new Getter<>() {
				@Override
				public @NotNull ItemStack get(PlayerInventorySlotChangeEvent event) {
					return event.getNewItemStack();
				}
			}, EventValues.TIME_NOW);
			EventValues.registerEventValue(PlayerInventorySlotChangeEvent.class, ItemStack.class, new Getter<>() {
				@Override
				public @NotNull ItemStack get(PlayerInventorySlotChangeEvent event) {
					return event.getOldItemStack();
				}
			}, EventValues.TIME_PAST);
		}
		//PrepareItemEnchantEvent
		EventValues.registerEventValue(PrepareItemEnchantEvent.class, Player.class, new Getter<>() {
			@Override
			public @NotNull Player get(PrepareItemEnchantEvent e) {
				return e.getEnchanter();
			}
		}, 0);
		EventValues.registerEventValue(PrepareItemEnchantEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public @NotNull ItemStack get(PrepareItemEnchantEvent e) {
				return e.getItem();
			}
		}, 0);
		EventValues.registerEventValue(PrepareItemEnchantEvent.class, Block.class, new Getter<>() {
			@Override
			public @NotNull Block get(PrepareItemEnchantEvent e) {
				return e.getEnchantBlock();
			}
		}, 0);
		//EnchantItemEvent
		EventValues.registerEventValue(EnchantItemEvent.class, Player.class, new Getter<>() {
			@Override
			public @NotNull Player get(EnchantItemEvent e) {
				return e.getEnchanter();
			}
		}, 0);
		EventValues.registerEventValue(EnchantItemEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public @NotNull ItemStack get(EnchantItemEvent e) {
				return e.getItem();
			}
		}, 0);
		EventValues.registerEventValue(EnchantItemEvent.class, Block.class, new Getter<>() {
			@Override
			public @NotNull Block get(EnchantItemEvent e) {
				return e.getEnchantBlock();
			}
		}, 0);
		EventValues.registerEventValue(HorseJumpEvent.class, Entity.class, new Getter<>() {
			@Override
			public @NotNull Entity get(HorseJumpEvent evt) {
				return evt.getEntity();
			}
		}, 0);
		// PlayerTradeEvent
		if (Skript.classExists("io.papermc.paper.event.player.PlayerTradeEvent")) {
			EventValues.registerEventValue(PlayerTradeEvent.class, AbstractVillager.class, new Getter<>() {
				@Override
				public @NotNull AbstractVillager get(PlayerTradeEvent event) {
					return event.getVillager();
				}
			}, EventValues.TIME_NOW);
		}
		// PlayerChangedWorldEvent
		EventValues.registerEventValue(PlayerChangedWorldEvent.class, World.class, new Getter<>() {
			@Override
			public @NotNull World get(PlayerChangedWorldEvent e) {
				return e.getFrom();
			}
		}, -1);

		// PlayerEggThrowEvent
		EventValues.registerEventValue(PlayerEggThrowEvent.class, Egg.class, new Getter<>() {
			@Override
			public @NotNull Egg get(PlayerEggThrowEvent event) {
				return event.getEgg();
			}
		}, EventValues.TIME_NOW);

		// PlayerStopUsingItemEvent
		if (Skript.classExists("io.papermc.paper.event.player.PlayerStopUsingItemEvent")) {
			EventValues.registerEventValue(PlayerStopUsingItemEvent.class, Timespan.class, new Getter<>() {
				@Override
				public Timespan get(PlayerStopUsingItemEvent event) {
					return Timespan.fromTicks(event.getTicksHeldFor());
				}
			}, EventValues.TIME_NOW);
		}

		// LootGenerateEvent
		if (Skript.classExists("org.bukkit.event.world.LootGenerateEvent")) {
			EventValues.registerEventValue(LootGenerateEvent.class, Entity.class, new Getter<>() {
				@Override
				@Nullable
				public Entity get(LootGenerateEvent event) {
					return event.getEntity();
				}
			}, EventValues.TIME_NOW);
			EventValues.registerEventValue(LootGenerateEvent.class, Location.class, new Getter<>() {
				@Override
				public @NotNull Location get(LootGenerateEvent event) {
					return event.getLootContext().getLocation();
				}
			}, EventValues.TIME_NOW);
		}

		// EntityResurrectEvent
		EventValues.registerEventValue(EntityResurrectEvent.class, ItemStack.class, new Getter<>() {
			@Override
			@Nullable
			public ItemStack get(EntityResurrectEvent event) {
				EquipmentSlot hand = event.getHand();
				EntityEquipment equipment = event.getEntity().getEquipment();
				if (equipment == null || hand == null)
					return null;
				return hand == EquipmentSlot.HAND ? equipment.getItemInMainHand() : equipment.getItemInOffHand();
			}
		}, EventValues.TIME_NOW);

		// PlayerItemHeldEvent
		EventValues.registerEventValue(PlayerItemHeldEvent.class, ItemStack.class, new Getter<>() {
			@Override
			@Nullable
			public ItemStack get(PlayerItemHeldEvent event) {
				return event.getPlayer().getInventory().getItem(event.getNewSlot());
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(PlayerItemHeldEvent.class, ItemStack.class, new Getter<>() {
			@Override
			@Nullable
			public ItemStack get(PlayerItemHeldEvent event) {
				return event.getPlayer().getInventory().getItem(event.getPreviousSlot());
			}
		}, EventValues.TIME_PAST);

		// PlayerPickupArrowEvent
		// This event value is restricted to MC 1.14+ due to an API change which has the return type changed
		// which throws a NoSuchMethodError if used in a 1.13 server.
		if (Skript.isRunningMinecraft(1, 14))
			EventValues.registerEventValue(PlayerPickupArrowEvent.class, Projectile.class, new Getter<>() {
				@Override
				public Projectile get(PlayerPickupArrowEvent event) {
					return event.getArrow();
				}
			}, EventValues.TIME_NOW);

		EventValues.registerEventValue(PlayerPickupArrowEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public @NotNull ItemStack get(PlayerPickupArrowEvent event) {
				return event.getItem().getItemStack();
			}
		}, EventValues.TIME_NOW);

		//PlayerQuitEvent
		if (Skript.classExists("org.bukkit.event.player.PlayerQuitEvent$QuitReason"))
			EventValues.registerEventValue(PlayerQuitEvent.class, QuitReason.class, new Getter<>() {
				@Override
				public @NotNull QuitReason get(PlayerQuitEvent event) {
					return event.getReason();
				}
			}, EventValues.TIME_NOW);

		// PlayerStonecutterRecipeSelectEvent
		if (Skript.classExists("io.papermc.paper.event.player.PlayerStonecutterRecipeSelectEvent"))
			EventValues.registerEventValue(PlayerStonecutterRecipeSelectEvent.class, ItemStack.class, new Getter<>() {
				@Override
				public ItemStack get(PlayerStonecutterRecipeSelectEvent event) {
					return event.getStonecuttingRecipe().getResult();
				}
			}, EventValues.TIME_NOW);

		// EntityTransformEvent
		EventValues.registerEventValue(EntityTransformEvent.class, Entity[].class, new Getter<>() {
			@Override
			@Nullable
			public Entity[] get(EntityTransformEvent event) {
				return event.getTransformedEntities().toArray(Entity[]::new);
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(EntityTransformEvent.class, TransformReason.class, new Getter<>() {
			@Override
			public @NotNull TransformReason get(EntityTransformEvent event) {
				return event.getTransformReason();
			}
		}, EventValues.TIME_NOW);

		// BellRingEvent - these are BlockEvents and not EntityEvents, so they have declared methods for getEntity()
		if (Skript.classExists("org.bukkit.event.block.BellRingEvent")) {
			EventValues.registerEventValue(BellRingEvent.class, Entity.class, new Getter<>() {
				@Override
				@Nullable
				public Entity get(BellRingEvent event) {
					return event.getEntity();
				}
			}, EventValues.TIME_NOW);

			EventValues.registerEventValue(BellRingEvent.class, Direction.class, new Getter<>() {
				@Override
				public Direction get(BellRingEvent event) {
					return new Direction(event.getDirection(), 1);
				}
			}, EventValues.TIME_NOW);
		} else if (Skript.classExists("io.papermc.paper.event.block.BellRingEvent")) {
			EventValues.registerEventValue(
				io.papermc.paper.event.block.BellRingEvent.class, Entity.class,
				new Getter<>() {
					@Override
					@Nullable
					public Entity get(io.papermc.paper.event.block.BellRingEvent event) {
						return event.getEntity();
					}
				}, EventValues.TIME_NOW);
		}

		if (Skript.classExists("org.bukkit.event.block.BellResonateEvent")) {
			EventValues.registerEventValue(BellResonateEvent.class, Entity[].class, new Getter<>() {
				@Override
				@Nullable
				public Entity[] get(BellResonateEvent event) {
					return event.getResonatedEntities().toArray(new LivingEntity[0]);
				}
			}, EventValues.TIME_NOW);
		}

		// InventoryMoveItemEvent
		EventValues.registerEventValue(InventoryMoveItemEvent.class, Inventory.class, new Getter<>() {
			@Override
			public Inventory get(InventoryMoveItemEvent event) {
				return event.getSource();
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(InventoryMoveItemEvent.class, Inventory.class, new Getter<>() {
			@Override
			public Inventory get(InventoryMoveItemEvent event) {
				return event.getDestination();
			}
		}, EventValues.TIME_FUTURE);
		EventValues.registerEventValue(InventoryMoveItemEvent.class, Block.class, new Getter<>() {
			@Override
			public @Nullable Block get(InventoryMoveItemEvent event) {
				Location location = event.getSource().getLocation();
				if (location == null) return null;
				return location.getBlock();
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(InventoryMoveItemEvent.class, Block.class, new Getter<>() {
			@Override
			public @Nullable Block get(InventoryMoveItemEvent event) {
				Location location = event.getDestination().getLocation();
				if (location == null) return null;
				return location.getBlock();
			}
		}, EventValues.TIME_FUTURE);
		EventValues.registerEventValue(InventoryMoveItemEvent.class, ItemStack.class, new Getter<>() {
			@Override
			public ItemStack get(InventoryMoveItemEvent event) {
				return event.getItem();
			}
		}, EventValues.TIME_NOW);

		// EntityRegainHealthEvent
		EventValues.registerEventValue(EntityRegainHealthEvent.class, RegainReason.class, new Getter<>() {
			@Override
			public @NotNull RegainReason get(EntityRegainHealthEvent event) {
				return event.getRegainReason();
			}
		}, EventValues.TIME_NOW);
	}
}
