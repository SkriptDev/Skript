package ch.njol.skript.util;

import org.bukkit.DyeColor;
import org.jetbrains.annotations.Nullable;

import ch.njol.yggdrasil.YggdrasilSerializable.YggdrasilExtendedSerializable;

public interface Color extends YggdrasilExtendedSerializable {
	
	/**
	 * Gets Bukkit color representing this color.
	 * @return Bukkit color.
	 */
	org.bukkit.Color asBukkitColor();
	
	
	/**
	 * Gets Bukkit dye color representing this color, if one exists.
	 * @return Dye color or null.
	 */
	@Nullable
	DyeColor asDyeColor();
	
	/**
	 * @return Name of the color.
	 */
	String getName();
	
}
