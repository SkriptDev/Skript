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
package ch.njol.skript.bukkitutil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.util.Timespan;
import ch.njol.util.StringUtils;

/**
 * Util class for managing {@link PotionEffectType PotionEffectsTypes}
 * @author Peter Güttinger
 */
public abstract class PotionEffectUtils {
	
	// Bukkit does not have the minecraft namespaces for potion effect types
	// and some Bukkit names do not match the Minecraft namespaces
	// so we create a small class to manage these
	enum PotionEffectTypes {
		SLOWNESS(PotionEffectType.SLOW, "slowness"),
		HASTE(PotionEffectType.FAST_DIGGING, "haste"),
		MINING_FATIGUE(PotionEffectType.SLOW_DIGGING, "mining_fatigue"),
		STRENGTH(PotionEffectType.INCREASE_DAMAGE, "strength"),
		INSTANT_HEALTH(PotionEffectType.HEAL, "instant_health"),
		INSTANT_DAMAGE(PotionEffectType.HARM, "instant_damage"),
		JUMP_BOOST(PotionEffectType.JUMP, "jump_boost"),
		NAUSEA(PotionEffectType.CONFUSION, "nausea"),
		RESISTANCE(PotionEffectType.DAMAGE_RESISTANCE, "resistance");
		
		private final PotionEffectType bukkit;
		private final String minecraft;
		private static final Map<String, PotionEffectType> BY_NAME = new HashMap<>();
		private static final Map<PotionEffectType, String> BY_TYPE = new HashMap<>();
		
		PotionEffectTypes(PotionEffectType bukkit, String minecraft) {
			this.bukkit = bukkit;
			this.minecraft = minecraft;
		}
		
		static {
			// Register potion types which Bukkit has a different name than Minecraft
			for (PotionEffectTypes p : values()) {
				String name = p.minecraft.replace("_", " ");
				BY_NAME.put(name, p.bukkit);
				BY_TYPE.put(p.bukkit, name);
			}
			// Register potion types which Bukkit has the same name as Minecraft
			for (PotionEffectType value : PotionEffectType.values()) {
				if (!BY_TYPE.containsKey(value)) {
					String name = value.getName().toLowerCase(Locale.ROOT).replace("_", " ");
					BY_NAME.put(name, value);
					BY_TYPE.put(value, name);
				}
			}
		}
		
		public static String getNames() {
			List<String> names = new ArrayList<>(BY_NAME.keySet());
			Collections.sort(names);
			return StringUtils.join(names, ", ");
		}
		
		@Nullable
		public static PotionEffectType parse(String name) {
			String n = name.toLowerCase(Locale.ROOT);
			if (BY_NAME.containsKey(n)) {
				return BY_NAME.get(n);
			}
			return null;
		}
		
		@Nullable
		public static String getName(PotionEffectType potionEffectType) {
			if (BY_TYPE.containsKey(potionEffectType)) {
				return BY_TYPE.get(potionEffectType);
			}
			return null;
		}
	}
	
	private static final boolean HAS_SUSPICIOUS_META = Skript.classExists("org.bukkit.inventory.meta.SuspiciousStewMeta");
	
	private PotionEffectUtils() {}
	
	@Nullable
	public static PotionEffectType parseType(final String s) {
		return PotionEffectTypes.parse(s);
	}
	
	@SuppressWarnings({"null", "ConstantConditions"})
	public static String toString(final PotionEffectType t) {
		return PotionEffectTypes.getName(t);
	}
	
	// REMIND flags?
	@SuppressWarnings({"null", "unused"})
	public static String toString(final PotionEffectType t, final int flags) {
		return toString(t);
	}
	
	public static String toString(PotionEffect potionEffect) {
		StringBuilder builder = new StringBuilder();
		if (potionEffect.isAmbient())
			builder.append("ambient ");
		builder.append("potion effect of ");
		builder.append(toString(potionEffect.getType()));
		
		builder.append(" of tier ").append(potionEffect.getAmplifier() + 1);
		
		if (!potionEffect.hasParticles())
			builder.append(" without particles");
		builder.append(" for ").append(Timespan.fromTicks_i(potionEffect.getDuration()));
		return builder.toString();
	}
	
	public static String getNames() {
		return PotionEffectTypes.getNames();
	}
	
	/**
	 * Clear all the active {@link PotionEffect PotionEffects} from an Entity
	 *
	 * @param entity Entity to clear effects for
	 */
	public static void clearAllEffects(LivingEntity entity) {
		entity.getActivePotionEffects().forEach(potionEffect -> entity.removePotionEffect(potionEffect.getType()));
	}
	
	/**
	 * Add PotionEffects to an entity
	 *
	 * @param entity Entity to add effects to
	 * @param effects {@link PotionEffect} or {@link PotionEffectType} to add
	 */
	public static void addEffects(LivingEntity entity, Object[] effects) {
		for (Object object : effects) {
			PotionEffect effect;
			if (object instanceof PotionEffect)
				effect = (PotionEffect) object;
			else if (object instanceof PotionEffectType)
				effect = new PotionEffect((PotionEffectType) object, 15 * 20, 0, false);
			else
				continue;
			
			entity.addPotionEffect(effect);
		}
	}
	
	/**
	 * Remove a PotionEffect from an entity
	 *
	 * @param entity Entity to remove effects for
	 * @param effects {@link PotionEffect} or {@link PotionEffectType} to remove
	 */
	public static void removeEffects(LivingEntity entity, Object[] effects) {
		for (Object object : effects) {
			PotionEffectType effectType;
			if (object instanceof PotionEffect)
				effectType = ((PotionEffect) object).getType();
			else if (object instanceof PotionEffectType)
				effectType = (PotionEffectType) object;
			else
				continue;
			
			entity.removePotionEffect(effectType);
		}
	}
	
	/**
	 * Clear all {@link PotionEffect PotionEffects} from an ItemType
	 *
	 * @param itemType Item to remove effects from
	 */
	public static void clearAllEffects(ItemType itemType) {
		ItemMeta meta = itemType.getItemMeta();
		if (meta instanceof PotionMeta)
			((PotionMeta) meta).clearCustomEffects();
		else if (HAS_SUSPICIOUS_META && meta instanceof SuspiciousStewMeta)
			((SuspiciousStewMeta) meta).clearCustomEffects();
		itemType.setItemMeta(meta);
	}
	
	/**
	 * Add PotionEffects to an ItemTye
	 *
	 * @param itemType Item to add effects to
	 * @param effects {@link PotionEffect} or {@link PotionEffectType} to add
	 */
	public static void addEffects(ItemType itemType, Object[] effects) {
		ItemMeta meta = itemType.getItemMeta();
		for (Object object : effects) {
			PotionEffect effect;
			if (object instanceof PotionEffect)
				effect = (PotionEffect) object;
			else if (object instanceof PotionEffectType)
				effect = new PotionEffect((PotionEffectType) object, 15 * 20, 0, false);
			else
				continue;
			
			if (meta instanceof PotionMeta)
				((PotionMeta) meta).addCustomEffect(effect, false);
			else if (HAS_SUSPICIOUS_META && meta instanceof SuspiciousStewMeta)
				((SuspiciousStewMeta) meta).addCustomEffect(effect, false);
		}
		itemType.setItemMeta(meta);
	}
	
	/**
	 * Remove a PotionEffect from an ItemType
	 *
	 * @param itemType Item to remove effects from
	 * @param effects {@link PotionEffect} or {@link PotionEffectType} to remove
	 */
	public static void removeEffects(ItemType itemType, Object[] effects) {
		ItemMeta meta = itemType.getItemMeta();
		
		for (Object object : effects) {
			PotionEffectType effectType;
			if (object instanceof PotionEffect)
				effectType = ((PotionEffect) object).getType();
			else if (object instanceof PotionEffectType)
				effectType = (PotionEffectType) object;
			else
				continue;
			
			if (meta instanceof PotionMeta)
				((PotionMeta) meta).removeCustomEffect(effectType);
			else if (HAS_SUSPICIOUS_META && meta instanceof SuspiciousStewMeta)
				((SuspiciousStewMeta) meta).removeCustomEffect(effectType);
		}
		itemType.setItemMeta(meta);
	}
	
	/**
	 * Get all the PotionEffects of an ItemType
	 *
	 * This will also include the base potion as well
	 *
	 * @param itemType Item to get potions from
	 * @return List of PotionEffects on the item
	 */
	public static List<PotionEffect> getEffects(ItemType itemType) {
		List<PotionEffect> effects = new ArrayList<>();
		ItemMeta meta = itemType.getItemMeta();
		if (meta instanceof PotionMeta) {
			PotionMeta potionMeta = ((PotionMeta) meta);
			effects.addAll(potionMeta.getCustomEffects());
			effects.addAll(PotionDataUtils.getPotionEffects(potionMeta.getBasePotionData()));
		} else if (HAS_SUSPICIOUS_META && meta instanceof SuspiciousStewMeta)
			effects.addAll(((SuspiciousStewMeta) meta).getCustomEffects());
		return effects;
	}
	
}
