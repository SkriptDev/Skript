package ch.njol.skript.registrations;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.Date;
import ch.njol.skript.util.Timespan;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

// When using these fields, be aware all ClassInfo's must be registered!
public class DefaultClasses {

	// Java classes
	public static ClassInfo<Object> OBJECT = getClassInfo(Object.class);
	public static ClassInfo<Number> NUMBER = getClassInfo(Number.class);
	public static ClassInfo<Long> LONG = getClassInfo(Long.class);
	public static ClassInfo<Boolean> BOOLEAN = getClassInfo(Boolean.class);
	public static ClassInfo<String> STRING = getClassInfo(String.class);

	// Bukkit classes
	public static ClassInfo<World> WORLD = getClassInfo(World.class);
	public static ClassInfo<Location> LOCATION = getClassInfo(Location.class);
	public static ClassInfo<Vector> VECTOR = getClassInfo(Vector.class);
	public static ClassInfo<Enchantment> ENCHANTMENT = getClassInfo(Enchantment.class);
	public static ClassInfo<EnchantmentOffer> ENCHANTMENT_OFFER = getClassInfo(EnchantmentOffer.class);

	// Skript classes
	public static ClassInfo<Color> COLOR = getClassInfo(Color.class);
	public static ClassInfo<Date> DATE = getClassInfo(Date.class);
	public static ClassInfo<Timespan> TIMESPAN = getClassInfo(Timespan.class);
	public static ClassInfo<OfflinePlayer> OFFLINE_PLAYER = getClassInfo(OfflinePlayer.class);
	public static ClassInfo<Player> PLAYER = getClassInfo(Player.class);

	@NotNull
	private static <T> ClassInfo<T> getClassInfo(Class<T> tClass) {
		//noinspection ConstantConditions
		ClassInfo<T> classInfo = Classes.getExactClassInfo(tClass);
		if (classInfo == null)
			throw new NullPointerException();
		return classInfo;
	}

}
