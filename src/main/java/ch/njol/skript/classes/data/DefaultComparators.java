package ch.njol.skript.classes.data;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptConfig;
import ch.njol.skript.bukkitutil.BlockUtils;
import ch.njol.skript.bukkitutil.EntityCategory;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.util.Date;
import ch.njol.skript.util.Time;
import ch.njol.skript.util.Timeperiod;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Timespan.TimePeriod;
import ch.njol.skript.util.WeatherType;
import ch.njol.util.StringUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.skriptlang.skript.lang.comparator.Comparator;
import org.skriptlang.skript.lang.comparator.Comparators;
import org.skriptlang.skript.lang.comparator.Relation;

import java.util.Objects;
import java.util.UUID;

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

		// Enum comparators
		Comparators.registerComparator(Enum.class, Enum.class, (o1, o2) -> Relation.get(o1.ordinal() - o2.ordinal()));

		// Block - BlockData
		Comparators.registerComparator(Block.class, BlockData.class, (block, data) -> Relation.get(block.getBlockData().matches(data)));

		// BlockData - BlockData
		Comparators.registerComparator(BlockData.class, BlockData.class, (data1, data2) -> Relation.get(data1.matches(data2)));

		// Block - Block
		Comparators.registerComparator(Block.class, Block.class, (b1, b2) -> Relation.get(BlockUtils.extractBlock(b1).equals(BlockUtils.extractBlock(b2))));

		// CommandSender - CommandSender
		Comparators.registerComparator(CommandSender.class, CommandSender.class, (s1, s2) -> Relation.get(s1.equals(s2)));

		// ItemStack - ItemStack
		Comparators.registerComparator(ItemStack.class, ItemStack.class, (o1, o2) -> Relation.get(o1.equals(o2)));

		// Entity - EntityType
		Comparators.registerComparator(Entity.class, EntityType.class, (o1, o2) -> Relation.get(o1.getType() == o2));

		// Entity - EntityCategory
		Comparators.registerComparator(Entity.class, EntityCategory.class, (o1, o2) -> Relation.get(o2.isOfType(o1)));

		// OfflinePlayer - OfflinePlayer
		Comparators.registerComparator(OfflinePlayer.class, OfflinePlayer.class, (p1, p2) -> Relation.get(Objects.equals(p1.getName(), p2.getName())));

		// OfflinePlayer - String
		Comparators.registerComparator(OfflinePlayer.class, String.class, (p, name) -> {
			String offlineName = p.getName();
			return offlineName == null ? Relation.NOT_EQUAL : Relation.get(offlineName.equalsIgnoreCase(name));
		});

		// World - String
		Comparators.registerComparator(World.class, String.class, (w, name) -> Relation.get(w.getName().equalsIgnoreCase(name)));

		// String - String
		Comparators.registerComparator(String.class, String.class, (s1, s2) -> Relation.get(StringUtils.equals(s1, s2, SkriptConfig.caseSensitive.value())));

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
				return Relation.get(t1.getAs(TimePeriod.MILLISECOND) - t2.getAs(TimePeriod.MILLISECOND));
			}

			@Override
			public boolean supportsOrdering() {
				return true;
			}
		});

		// Time - Timeperiod
		Comparators.registerComparator(Time.class, Timeperiod.class, (t, p) -> Relation.get(p.contains(t)));

		// Object - ClassInfo
		Comparators.registerComparator(Object.class, ClassInfo.class, (o, c) -> Relation.get(c.getC().isInstance(o) || o instanceof ClassInfo && c.getC().isAssignableFrom(((ClassInfo<?>) o).getC())));

		// EnchantmentOffer Comparators
		// EnchantmentOffer - Number
		Comparators.registerComparator(EnchantmentOffer.class, Number.class, (eo, exp) -> Relation.get(eo.getCost() == exp.intValue()));

		// Inventory - InventoryType
		Comparators.registerComparator(Inventory.class, InventoryType.class, (inventory, inventoryType) -> Relation.get(inventory.getType() == inventoryType));

		// World - WeatherType
		Comparators.registerComparator(World.class, WeatherType.class, (world, weatherType) -> Relation.get(WeatherType.fromWorld(world) == weatherType));

		// Location - Location
		Comparators.registerComparator(Location.class, Location.class, (first, second) -> Relation.get(
			// compare worlds
			Objects.equals(first.getWorld(), second.getWorld()) &&
				// compare xyz coords
				first.toVector().equals(second.toVector()) &&
				// normalize yaw and pitch to [-180, 180) and [-90, 90] respectively
				// before comparing them
				Location.normalizeYaw(first.getYaw()) == Location.normalizeYaw(second.getYaw()) &&
				Location.normalizePitch(first.getPitch()) == Location.normalizePitch(second.getPitch())
		));

		// UUID - String
		Comparators.registerComparator(UUID.class, String.class, (o1, o2) -> Relation.get(o1.toString().equalsIgnoreCase(o2)));
	}

}
