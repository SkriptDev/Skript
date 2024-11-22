package ch.njol.skript.classes.data;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptConfig;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.util.BlockUtils;
import ch.njol.skript.util.Date;
import ch.njol.skript.util.GameruleValue;
import ch.njol.skript.util.Time;
import ch.njol.skript.util.Timeperiod;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.WeatherType;
import ch.njol.skript.util.slot.EquipmentSlot;
import ch.njol.skript.util.slot.Slot;
import ch.njol.skript.util.slot.SlotWithIndex;
import ch.njol.util.StringUtils;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.EntityType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.skriptlang.skript.lang.comparator.Comparator;
import org.skriptlang.skript.lang.comparator.Comparators;
import org.skriptlang.skript.lang.comparator.Relation;

import java.util.Objects;

@SuppressWarnings({"rawtypes"})
public class DefaultComparators {

	public DefaultComparators() {
	}

	static {

		// Number - Number
		Comparators.registerComparator(Number.class, Number.class, new Comparator<>() {
			@Override
			public Relation compare(Number n1, Number n2) {
				if (n1 instanceof Long && n2 instanceof Long)
					return Relation.get(n1.longValue() - n2.longValue());
				Double d1 = n1.doubleValue(),
					d2 = n2.doubleValue();
				if (d1.isNaN() || d2.isNaN()) {
					return Relation.SMALLER;
				} else if (d1.isInfinite() || d2.isInfinite()) {
					return d1 > d2 ? Relation.GREATER : d1 < d2 ? Relation.SMALLER : Relation.EQUAL;
				} else {
					double diff = d1 - d2;
					if (Math.abs(diff) < Skript.EPSILON)
						return Relation.EQUAL;
					return Relation.get(diff);
				}
			}

			@Override
			public boolean supportsOrdering() {
				return true;
			}
		});

		// Slot - Slot
		Comparators.registerComparator(Slot.class, Slot.class, new Comparator<>() {

			@Override
			public Relation compare(Slot o1, Slot o2) {
				if (o1 instanceof EquipmentSlot != o2 instanceof EquipmentSlot)
					return Relation.NOT_EQUAL;
				if (o1.isSameSlot(o2))
					return Relation.EQUAL;
				return Relation.NOT_EQUAL;
			}

			@Override
			public boolean supportsOrdering() {
				return false;
			}

		});

		// Slot - Number
		Comparators.registerComparator(Slot.class, Number.class, new Comparator<>() {

			@Override
			public Relation compare(Slot o1, Number o2) {
				if (o1 instanceof SlotWithIndex) {
					return Relation.get(((SlotWithIndex) o1).getIndex() - o2.intValue());
				}
				return Relation.NOT_EQUAL;
			}

			@Override
			public boolean supportsOrdering() {
				return true;
			}

		});

		// Block - BlockData
		Comparators.registerComparator(Block.class, BlockData.class, new Comparator<>() {
			@Override
			public Relation compare(Block block, BlockData data) {
				return Relation.get(block.getBlockData().matches(data));
			}

			@Override
			public boolean supportsOrdering() {
				return false;
			}
		});

		// BlockData - BlockData
		Comparators.registerComparator(BlockData.class, BlockData.class, new Comparator<>() {
			@Override
			public Relation compare(BlockData data1, BlockData data2) {
				return Relation.get(data1.matches(data2));
			}

			@Override
			public boolean supportsOrdering() {
				return false;
			}
		});

		// Block - Block
		Comparators.registerComparator(Block.class, Block.class, new Comparator<>() {
			@Override
			public Relation compare(Block b1, Block b2) {
				return Relation.get(BlockUtils.extractBlock(b1).equals(BlockUtils.extractBlock(b2)));
			}

			@Override
			public boolean supportsOrdering() {
				return false;
			}
		});

		// CommandSender - CommandSender
		Comparators.registerComparator(CommandSender.class, CommandSender.class, new Comparator<>() {
			@Override
			public Relation compare(CommandSender s1, CommandSender s2) {
				return Relation.get(s1.equals(s2));
			}

			@Override
			public boolean supportsOrdering() {
				return false;
			}
		});

		// ItemStack - ItemStack
		Comparators.registerComparator(ItemStack.class, ItemStack.class, (o1, o2) -> Relation.get(o1.equals(o2)));

		// ItemStack - Slot
		Comparators.registerComparator(ItemStack.class, Slot.class, (itemStack, slot) -> Relation.get(itemStack.equals(slot.getItem())));
		// EntityType - EntityType
		Comparators.registerComparator(EntityType.class, EntityType.class, (o1, o2) -> Relation.get(o1.equals(o2)));

		// OfflinePlayer - OfflinePlayer
		Comparators.registerComparator(OfflinePlayer.class, OfflinePlayer.class, new Comparator<>() {
			@Override
			public Relation compare(OfflinePlayer p1, OfflinePlayer p2) {
				return Relation.get(Objects.equals(p1.getName(), p2.getName()));
			}

			@Override
			public boolean supportsOrdering() {
				return false;
			}
		});

		// OfflinePlayer - String
		Comparators.registerComparator(OfflinePlayer.class, String.class, new Comparator<>() {
			@Override
			public Relation compare(OfflinePlayer p, String name) {
				String offlineName = p.getName();
				return offlineName == null ? Relation.NOT_EQUAL : Relation.get(offlineName.equalsIgnoreCase(name));
			}

			@Override
			public boolean supportsOrdering() {
				return false;
			}
		});

		// World - String
		Comparators.registerComparator(World.class, String.class, new Comparator<>() {
			@Override
			public Relation compare(World w, String name) {
				return Relation.get(w.getName().equalsIgnoreCase(name));
			}

			@Override
			public boolean supportsOrdering() {
				return false;
			}
		});

		// String - String
		Comparators.registerComparator(String.class, String.class, new Comparator<>() {
			@Override
			public Relation compare(String s1, String s2) {
				return Relation.get(StringUtils.equals(s1, s2, SkriptConfig.caseSensitive.value()));
			}

			@Override
			public boolean supportsOrdering() {
				return false;
			}
		});

		// Date - Date
		Comparators.registerComparator(Date.class, Date.class, new Comparator<>() {
			@Override
			public Relation compare(Date d1, Date d2) {
				return Relation.get(d1.compareTo(d2));
			}

			@Override
			public boolean supportsOrdering() {
				return true;
			}
		});

		// Time - Time
		Comparators.registerComparator(Time.class, Time.class, new Comparator<>() {
			@Override
			public Relation compare(Time t1, Time t2) {
				return Relation.get(t1.getTime() - t2.getTime());
			}

			@Override
			public boolean supportsOrdering() {
				return true;
			}
		});

		// Timespan - Timespan
		Comparators.registerComparator(Timespan.class, Timespan.class, new Comparator<>() {
			@Override
			public Relation compare(Timespan t1, Timespan t2) {
				return Relation.get(t1.getMilliSeconds() - t2.getMilliSeconds());
			}

			@Override
			public boolean supportsOrdering() {
				return true;
			}
		});

		// Time - Timeperiod
		Comparators.registerComparator(Time.class, Timeperiod.class, new Comparator<>() {
			@Override
			public Relation compare(Time t, Timeperiod p) {
				return Relation.get(p.contains(t));
			}

			@Override
			public boolean supportsOrdering() {
				return false;
			}
		});

		// TreeType - TreeType
		Comparators.registerComparator(TreeType.class, TreeType.class, new Comparator<>() {
			@Override
			public Relation compare(TreeType s1, TreeType s2) {
				return Relation.get(s1.equals(s2));
			}

			@Override
			public boolean supportsOrdering() {
				return false;
			}
		});

		// Object - ClassInfo
		Comparators.registerComparator(Object.class, ClassInfo.class, new Comparator<>() {
			@Override
			public Relation compare(Object o, ClassInfo c) {
				return Relation.get(c.getC().isInstance(o) || o instanceof ClassInfo && c.getC().isAssignableFrom(((ClassInfo<?>) o).getC()));
			}

			@Override
			public boolean supportsOrdering() {
				return false;
			}
		});

		Comparators.registerComparator(GameruleValue.class, GameruleValue.class, new Comparator<>() {
			@Override
			public Relation compare(GameruleValue o1, GameruleValue o2) {
				return Relation.get(o1.equals(o2));
			}

			@Override
			public boolean supportsOrdering() {
				return false;
			}
		});

		Comparators.registerComparator(GameruleValue.class, Number.class, new Comparator<>() {
			@Override
			public Relation compare(GameruleValue o1, Number o2) {
				if (!(o1.getGameruleValue() instanceof Number))
					return Relation.NOT_EQUAL;
				Number gameruleValue = (Number) o1.getGameruleValue();
				return Comparators.compare(gameruleValue, o2);
			}

			@Override
			public boolean supportsOrdering() {
				return true;
			}
		});

		Comparators.registerComparator(GameruleValue.class, Boolean.class, new Comparator<>() {
			@Override
			public Relation compare(GameruleValue o1, Boolean o2) {
				if (!(o1.getGameruleValue() instanceof Boolean))
					return Relation.NOT_EQUAL;
				return Relation.get(o2.equals(o1.getGameruleValue()));
			}

			@Override
			public boolean supportsOrdering() {
				return false;
			}
		});

		// EnchantmentOffer Comparators
		// EnchantmentOffer - Number
		Comparators.registerComparator(EnchantmentOffer.class, Number.class, new Comparator<>() {
			@Override
			public Relation compare(EnchantmentOffer eo, Number exp) {
				return Relation.get(eo.getCost() == exp.intValue());
			}

			@Override
			public boolean supportsOrdering() {
				return false;
			}
		});

		Comparators.registerComparator(Inventory.class, InventoryType.class, new Comparator<>() {
			@Override
			public Relation compare(Inventory inventory, InventoryType inventoryType) {
				return Relation.get(inventory.getType() == inventoryType);
			}

			@Override
			public boolean supportsOrdering() {
				return false;
			}
		});

		// World - WeatherType
		Comparators.registerComparator(World.class, WeatherType.class, new Comparator<>() {
			@Override
			public Relation compare(World world, WeatherType weatherType) {
				return Relation.get(WeatherType.fromWorld(world) == weatherType);
			}

			@Override
			public boolean supportsOrdering() {
				return false;
			}
		});

		// Location - Location
		Comparators.registerComparator(Location.class, Location.class, new Comparator<>() {
			@Override
			public Relation compare(Location first, Location second) {
				return Relation.get(
					// compare worlds
					Objects.equals(first.getWorld(), second.getWorld()) &&
						// compare xyz coords
						first.toVector().equals(second.toVector()) &&
						// normalize yaw and pitch to [-180, 180) and [-90, 90] respectively
						// before comparing them
						Location.normalizeYaw(first.getYaw()) == Location.normalizeYaw(second.getYaw()) &&
						Location.normalizePitch(first.getPitch()) == Location.normalizePitch(second.getPitch())
				);
			}

			@Override
			public boolean supportsOrdering() {
				return false;
			}
		});

		// Potion Effect Type
		Comparators.registerComparator(PotionEffectType.class, PotionEffectType.class, (one, two) -> Relation.get(one.equals(two)));
	}

}
