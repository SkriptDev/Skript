/**
 * This file is part of Skript.
 *
 * Skript is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Skript is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.entity;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Golem;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Illager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Raider;
import org.bukkit.entity.Spellcaster;
import org.bukkit.entity.WaterMob;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.yggdrasil.Fields;

/**
 * @author Peter Güttinger
 */
public class SimpleEntityData extends EntityData<Entity> {
	
	public final static class SimpleEntityDataInfo {
		final String codeName;
		final Class<? extends Entity> c;
		final boolean isSupertype;
		@Nullable
		EntityType type;
		
		SimpleEntityDataInfo(final String codeName, final EntityType type) {
			this(codeName, type.getEntityClass(), type, false);
		}
		
		SimpleEntityDataInfo(final String codeName, final EntityType type, boolean isSupertype) {
			this(codeName, type.getEntityClass(), type, isSupertype);
		}
		
		SimpleEntityDataInfo(final String codeName, final Class<? extends Entity> c, boolean isSupertype) {
			this(codeName, c, null, isSupertype);
		}
		
		SimpleEntityDataInfo(final String codeName, @Nullable final Class<? extends Entity> c, @Nullable EntityType t, final boolean isSupertype) {
			this.codeName = codeName;
			if (c != null) {
				this.c = c;
			} else {
				this.c = Entity.class;
			}
			this.type = t;
			this.isSupertype = isSupertype;
		}
		
		@Override
		public int hashCode() {
			return c.hashCode();
		}
		
		@Override
		public boolean equals(final @Nullable Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof SimpleEntityDataInfo))
				return false;
			final SimpleEntityDataInfo other = (SimpleEntityDataInfo) obj;
			if (c != other.c)
				return false;
			assert codeName.equals(other.codeName);
			assert isSupertype == other.isSupertype;
			return true;
		}
	}
	
	private final static List<SimpleEntityDataInfo> types = new ArrayList<>();
	
	static {
		types.add(new SimpleEntityDataInfo("area effect cloud", EntityType.AREA_EFFECT_CLOUD));
		types.add(new SimpleEntityDataInfo("armor stand", EntityType.ARMOR_STAND));
		types.add(new SimpleEntityDataInfo("arrow", EntityType.ARROW));
		types.add(new SimpleEntityDataInfo("bat", EntityType.BAT));
		types.add(new SimpleEntityDataInfo("blaze", EntityType.BLAZE));
		types.add(new SimpleEntityDataInfo("bottle of enchanting", EntityType.THROWN_EXP_BOTTLE));
		types.add(new SimpleEntityDataInfo("cave spider", EntityType.CAVE_SPIDER));
		types.add(new SimpleEntityDataInfo("chicken", EntityType.CHICKEN));
		types.add(new SimpleEntityDataInfo("cod", EntityType.COD));
		types.add(new SimpleEntityDataInfo("cow", EntityType.COW));
		types.add(new SimpleEntityDataInfo("dolphin", EntityType.DOLPHIN));
		types.add(new SimpleEntityDataInfo("donkey", EntityType.DONKEY));
		types.add(new SimpleEntityDataInfo("dragon fireball", EntityType.DRAGON_FIREBALL));
		types.add(new SimpleEntityDataInfo("drowned", EntityType.DROWNED));
		types.add(new SimpleEntityDataInfo("egg", EntityType.EGG));
		types.add(new SimpleEntityDataInfo("elder guardian", EntityType.ELDER_GUARDIAN));
		types.add(new SimpleEntityDataInfo("ender crystal", EntityType.ENDER_CRYSTAL));
		types.add(new SimpleEntityDataInfo("ender dragon", EntityType.ENDER_DRAGON));
		types.add(new SimpleEntityDataInfo("ender pearl", EntityType.ENDER_PEARL));
		types.add(new SimpleEntityDataInfo("endermite", EntityType.ENDERMITE));
		types.add(new SimpleEntityDataInfo("evoker", EntityType.EVOKER));
		types.add(new SimpleEntityDataInfo("evoker fangs", EntityType.EVOKER_FANGS));
		types.add(new SimpleEntityDataInfo("firework", EntityType.FIREWORK));
		types.add(new SimpleEntityDataInfo("fish hook", EntityType.FISHING_HOOK));
		types.add(new SimpleEntityDataInfo("ghast", EntityType.GHAST));
		types.add(new SimpleEntityDataInfo("giant", EntityType.GIANT));
		types.add(new SimpleEntityDataInfo("husk", EntityType.HUSK));
		types.add(new SimpleEntityDataInfo("illusioner", EntityType.ILLUSIONER));
		types.add(new SimpleEntityDataInfo("iron golem", EntityType.IRON_GOLEM));
		types.add(new SimpleEntityDataInfo("item frame", EntityType.ITEM_FRAME));
		types.add(new SimpleEntityDataInfo("large fireball", EntityType.FIREBALL));
		types.add(new SimpleEntityDataInfo("llama spit", EntityType.LLAMA_SPIT));
		types.add(new SimpleEntityDataInfo("leash hitch", EntityType.LEASH_HITCH));
		types.add(new SimpleEntityDataInfo("magma cube", EntityType.MAGMA_CUBE));
		types.add(new SimpleEntityDataInfo("mooshroom", EntityType.MUSHROOM_COW));
		types.add(new SimpleEntityDataInfo("mule", EntityType.MULE));
		types.add(new SimpleEntityDataInfo("normal guardian", EntityType.GUARDIAN));
		types.add(new SimpleEntityDataInfo("painting", EntityType.PAINTING));
		types.add(new SimpleEntityDataInfo("phantom", EntityType.PHANTOM));
		types.add(new SimpleEntityDataInfo("polar bear", EntityType.POLAR_BEAR));
		types.add(new SimpleEntityDataInfo("puffer fish", EntityType.PUFFERFISH));
		types.add(new SimpleEntityDataInfo("salmon", EntityType.SALMON));
		types.add(new SimpleEntityDataInfo("shulker", EntityType.SHULKER));
		types.add(new SimpleEntityDataInfo("shulker bullet", EntityType.SHULKER_BULLET));
		types.add(new SimpleEntityDataInfo("silverfish", EntityType.SILVERFISH));
		types.add(new SimpleEntityDataInfo("skeleton", EntityType.SKELETON, true));
		types.add(new SimpleEntityDataInfo("skeleton horse", EntityType.SKELETON_HORSE));
		types.add(new SimpleEntityDataInfo("slime", EntityType.SLIME));
		types.add(new SimpleEntityDataInfo("small fireball", EntityType.SMALL_FIREBALL));
		types.add(new SimpleEntityDataInfo("snowball", EntityType.SNOWBALL));
		types.add(new SimpleEntityDataInfo("snow golem", EntityType.SNOWMAN));
		types.add(new SimpleEntityDataInfo("spectral arrow", EntityType.SPECTRAL_ARROW));
		types.add(new SimpleEntityDataInfo("spider", EntityType.SPIDER));
		types.add(new SimpleEntityDataInfo("squid", EntityType.SQUID));
		types.add(new SimpleEntityDataInfo("stray", EntityType.STRAY));
		types.add(new SimpleEntityDataInfo("tnt", EntityType.PRIMED_TNT));
		types.add(new SimpleEntityDataInfo("trident", EntityType.TRIDENT));
		types.add(new SimpleEntityDataInfo("turtle", EntityType.TURTLE));
		types.add(new SimpleEntityDataInfo("undead horse", EntityType.ZOMBIE_HORSE));
		types.add(new SimpleEntityDataInfo("vex", EntityType.VEX));
		types.add(new SimpleEntityDataInfo("vindicator", EntityType.VINDICATOR));
		types.add(new SimpleEntityDataInfo("witch", EntityType.WITCH));
		types.add(new SimpleEntityDataInfo("wither", EntityType.WITHER));
		types.add(new SimpleEntityDataInfo("wither skeleton", EntityType.WITHER_SKELETON));
		types.add(new SimpleEntityDataInfo("wither skull", EntityType.WITHER_SKULL));
		types.add(new SimpleEntityDataInfo("zombie", EntityType.ZOMBIE));
		
		if (Skript.isRunningMinecraft(1, 14)) {
			types.add(new SimpleEntityDataInfo("pillager", EntityType.PILLAGER));
			types.add(new SimpleEntityDataInfo("ravager", EntityType.RAVAGER));
			types.add(new SimpleEntityDataInfo("wandering trader", EntityType.WANDERING_TRADER));
		}
		
		if (Skript.isRunningMinecraft(1, 16)) {
			types.add(new SimpleEntityDataInfo("piglin", EntityType.PIGLIN));
			types.add(new SimpleEntityDataInfo("hoglin", EntityType.HOGLIN));
			types.add(new SimpleEntityDataInfo("zoglin", EntityType.ZOGLIN));
			types.add(new SimpleEntityDataInfo("strider", EntityType.STRIDER));
			types.add(new SimpleEntityDataInfo("zombie pigman", EntityType.ZOMBIFIED_PIGLIN));
		} else {
			// This name was changed in 1.16
			EntityType type = EntityType.valueOf("PIG_ZOMBIE");
			types.add(new SimpleEntityDataInfo("zombie pigman", type));
		}
		
		if (Skript.isRunningMinecraft(1, 16, 2))
			types.add(new SimpleEntityDataInfo("piglin brute", EntityType.PIGLIN_BRUTE));
		
		// SUPERTYPES
		types.add(new SimpleEntityDataInfo("any horse", AbstractHorse.class, true));
		types.add(new SimpleEntityDataInfo("any fireball", Fireball.class, true));
		
		types.add(new SimpleEntityDataInfo("animal", Animals.class, true));
		types.add(new SimpleEntityDataInfo("chested horse", ChestedHorse.class, true));
		types.add(new SimpleEntityDataInfo("creature", Creature.class, true));
		types.add(new SimpleEntityDataInfo("damageable", Damageable.class, true));
		types.add(new SimpleEntityDataInfo("fish", Fish.class, true));
		types.add(new SimpleEntityDataInfo("entity", Entity.class, true));
		types.add(new SimpleEntityDataInfo("golem", Golem.class, true));
		types.add(new SimpleEntityDataInfo("guardian", Guardian.class, true));
		types.add(new SimpleEntityDataInfo("human", HumanEntity.class, true));
		types.add(new SimpleEntityDataInfo("illager", Illager.class, true));
		types.add(new SimpleEntityDataInfo("living entity", LivingEntity.class, true));
		types.add(new SimpleEntityDataInfo("monster", Monster.class, true));
		types.add(new SimpleEntityDataInfo("mob", Mob.class, true));
		types.add(new SimpleEntityDataInfo("projectile", Projectile.class, true));
		types.add(new SimpleEntityDataInfo("spellcaster", Spellcaster.class, true));
		types.add(new SimpleEntityDataInfo("water mob", WaterMob.class, true));
		
		if (Skript.isRunningMinecraft(1, 14))
			types.add(new SimpleEntityDataInfo("raider", Raider.class, true));
	}
	
	static {
		final String[] codeNames = new String[types.size()];
		int i = 0;
		for (final SimpleEntityDataInfo info : types) {
			codeNames[i++] = info.codeName;
		}
		EntityData.register(SimpleEntityData.class, "simple", Entity.class, 0, codeNames);
	}
	
	private transient SimpleEntityDataInfo info;
	
	public SimpleEntityData() {
		this(Entity.class);
	}
	
	private SimpleEntityData(final SimpleEntityDataInfo info) {
		assert info != null;
		this.info = info;
		matchedPattern = types.indexOf(info);
	}
	
	public SimpleEntityData(final Class<? extends Entity> c) {
		assert c != null && c.isInterface() : c;
		int i = 0;
		for (final SimpleEntityDataInfo info : types) {
			if (info.c.isAssignableFrom(c)) {
				this.info = info;
				matchedPattern = i;
				return;
			}
			i++;
		}
		throw new IllegalStateException();
	}
	
	public SimpleEntityData(final Entity e) {
		int i = 0;
		for (final SimpleEntityDataInfo info : types) {
			if (info.type == e.getType()) {
				this.info = info;
				matchedPattern = i;
				return;
			}
			i++;
		}
		throw new IllegalStateException();
	}
	
	@SuppressWarnings("null")
	@Override
	protected boolean init(final Literal<?>[] exprs, final int matchedPattern, final ParseResult parseResult) {
		info = types.get(matchedPattern);
		assert info != null : matchedPattern;
		return true;
	}
	
	@Override
	protected boolean init(final @Nullable Class<? extends Entity> c, final @Nullable Entity e) {
		assert false;
		return false;
	}
	
	@Override
	public void set(final Entity entity) {
	}
	
	@Override
	public boolean match(final Entity e) {
		if (info.type == e.getType()) {
			return true;
		}
		return info.c.isInstance(e);
	}
	
	@Override
	public Class<? extends Entity> getType() {
		return info.c;
	}
	
	@Override
	protected int hashCode_i() {
		return info.hashCode();
	}
	
	@Override
	protected boolean equals_i(final EntityData<?> obj) {
		if (!(obj instanceof SimpleEntityData))
			return false;
		final SimpleEntityData other = (SimpleEntityData) obj;
		return info.equals(other.info);
	}
	
	@Override
	public Fields serialize() throws NotSerializableException {
		final Fields f = super.serialize();
		f.putObject("info.codeName", info.codeName);
		return f;
	}
	
	@Override
	public void deserialize(final Fields fields) throws StreamCorruptedException, NotSerializableException {
		final String codeName = fields.getAndRemoveObject("info.codeName", String.class);
		for (final SimpleEntityDataInfo i : types) {
			if (i.codeName.equals(codeName)) {
				info = i;
				super.deserialize(fields);
				return;
			}
		}
		throw new StreamCorruptedException("Invalid SimpleEntityDataInfo code name " + codeName);
	}
	
	@Override
	@Deprecated
	protected boolean deserialize(final String s) {
		try {
			final Class<?> c = Class.forName(s);
			for (final SimpleEntityDataInfo i : types) {
				if (i.c == c) {
					info = i;
					return true;
				}
			}
			return false;
		} catch (final ClassNotFoundException e) {
			return false;
		}
	}
	
	@Override
	public boolean isSupertypeOf(final EntityData<?> e) {
		return info.c == e.getType() || info.isSupertype && info.c.isAssignableFrom(e.getType());
	}
	
	@Override
	public EntityData getSuperType() {
		return new SimpleEntityData(info);
	}
	
}
