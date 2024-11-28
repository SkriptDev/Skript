/**
 * This file is part of Skript.
 * <p>
 * Skript is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Skript is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.util;

import ch.njol.skript.Skript;
import ch.njol.skript.registrations.Classes;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public abstract class PotionEffectUtils {

	private static final boolean HAS_SUSPICIOUS_META = Skript.classExists("org.bukkit.inventory.meta.SuspiciousStewMeta");

	private PotionEffectUtils() {
	}

	public static String toString(PotionEffect potionEffect) {
		StringBuilder builder = new StringBuilder();
		if (potionEffect.isAmbient())
			builder.append("ambient ");
		builder.append("potion effect of ");
		builder.append(Classes.toString(potionEffect.getType()));
		builder.append(" of tier ").append(potionEffect.getAmplifier() + 1);
		if (!potionEffect.hasParticles())
			builder.append(" without particles");
		builder.append(" for ").append(potionEffect.getDuration() == -1 ? "infinity" : Timespan.fromTicks(Math.abs(potionEffect.getDuration())));
		if (!potionEffect.hasIcon())
			builder.append(" without icon");
		return builder.toString();
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
	 * @param entity  Entity to add effects to
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
	 * @param entity  Entity to remove effects for
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
	 * @param itemStack Item to remove effects from
	 */
	public static void clearAllEffects(ItemStack itemStack) {
		ItemMeta meta = itemStack.getItemMeta();
		if (meta instanceof PotionMeta)
			((PotionMeta) meta).clearCustomEffects();
		else if (HAS_SUSPICIOUS_META && meta instanceof SuspiciousStewMeta)
			((SuspiciousStewMeta) meta).clearCustomEffects();
		itemStack.setItemMeta(meta);
	}

	/**
	 * Add PotionEffects to an ItemTye
	 *
	 * @param itemStack Item to add effects to
	 * @param effects   {@link PotionEffect} or {@link PotionEffectType} to add
	 */
	public static void addEffects(ItemStack itemStack, Object[] effects) {
		ItemMeta meta = itemStack.getItemMeta();
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
		itemStack.setItemMeta(meta);
	}

	/**
	 * Remove a PotionEffect from an ItemType
	 *
	 * @param itemStack Item to remove effects from
	 * @param effects   {@link PotionEffect} or {@link PotionEffectType} to remove
	 */
	public static void removeEffects(ItemStack itemStack, Object[] effects) {
		ItemMeta meta = itemStack.getItemMeta();

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
		itemStack.setItemMeta(meta);
	}

	private static final boolean HAS_POTION_TYPE_METHOD = Skript.methodExists(PotionMeta.class, "hasBasePotionType");

	/**
	 * Get all the PotionEffects of an ItemType
	 * <p>
	 * This will also include the base potion as well
	 *
	 * @param itemStack Item to get potions from
	 * @return List of PotionEffects on the item
	 */
	public static List<PotionEffect> getEffects(ItemStack itemStack) {
		List<PotionEffect> effects = new ArrayList<>();
		ItemMeta meta = itemStack.getItemMeta();
		if (meta instanceof PotionMeta) {
			PotionMeta potionMeta = ((PotionMeta) meta);
			if (potionMeta.hasCustomEffects())
				effects.addAll(potionMeta.getCustomEffects());
			if (HAS_POTION_TYPE_METHOD) {
				if (potionMeta.hasBasePotionType()) {
					//noinspection ConstantConditions - checked via hasBasePotionType
					effects.addAll(potionMeta.getBasePotionType().getPotionEffects());
				}
			} else { // use deprecated method
				if (potionMeta.hasBasePotionType()) {
					PotionType basePotionType = potionMeta.getBasePotionType();
					if (basePotionType != null) {
						effects.addAll(basePotionType.getPotionEffects());
					}
				}
			}
		} else if (HAS_SUSPICIOUS_META && meta instanceof SuspiciousStewMeta)
			effects.addAll(((SuspiciousStewMeta) meta).getCustomEffects());
		return effects;
	}

}
