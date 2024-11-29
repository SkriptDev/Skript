package ch.njol.skript.classes.data.bukkit;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.EnumClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.classes.registry.RegistryClassInfo;
import ch.njol.skript.expressions.ExprDamageCause;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.PotionEffectUtils;
import ch.njol.yggdrasil.Fields;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Registry;
import org.bukkit.SoundCategory;
import org.bukkit.TreeType;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.Metadatable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.stream.Collectors;
/**
 * Represents {@link ClassInfo ClassInfos} relating to general server objects
 */
public class ServerClasses {

	private ServerClasses() {
	}

	public static void init() {
		Classes.registerClass(new RegistryClassInfo<>(Attribute.class, Registry.ATTRIBUTE, "attribute")
			.user("attributes?")
			.name("Attribute")
			.description("Represents an attribute.",
				"Note that this type does not contain any numerical values.",
				"See <a href='https://minecraft.wiki/w/Attribute#Attributes'>attributes</a> for more info.",
				"These are auto-generated and may differ between MC versions.",
				"NOTE: Minecraft namespaces and underscores are supported, ex: 'minecraft:attack_damage'.")
			.since("2.5"));

		Classes.registerClass(new EnumClassInfo<>(EntityDamageEvent.DamageCause.class, "damagecause", "damage causes", new ExprDamageCause())
			.user("damage ?causes?")
			.name("Damage Cause")
			.description("The cause/type of a <a href='events.html#damage'>damage event</a>, e.g. lava, fall, fire, drowning, explosion, poison, etc.",
				"Please note that support for this type is very rudimentary, e.g. lava, fire and burning, " +
					"as well as projectile and attack are considered different types.")
			.examples("")
			.since("2.0")
			.after("material", "itemstack", "entitytype"));

		Classes.registerClass(new EnumClassInfo<>(Difficulty.class, "difficulty", "difficulties")
			.user("difficult(y|ies)")
			.name("Difficulty")
			.description("The difficulty of a <a href='#world'>world</a>.")
			.since("2.3"));

		Classes.registerClass(new ClassInfo<>(GameRule.class, "gamerule")
			.user("gamerules?")
			.name("Gamerule")
			.description("A gamerule")
			.usage(Arrays.stream(GameRule.values()).map(GameRule::getName).collect(Collectors.joining(", ")))
			.since("2.5")
			.requiredPlugins("Minecraft 1.13 or newer")
			.supplier(GameRule.values())
			.parser(new Parser<>() {
				@Override
				@Nullable
				public GameRule parse(final String input, final ParseContext context) {
					return GameRule.getByName(input);
				}

				@Override
				public String toString(GameRule o, int flags) {
					return o.getName();
				}

				@Override
				public String toVariableNameString(GameRule o) {
					return o.getName();
				}
			})
		);

		Classes.registerClass(new EnumClassInfo<>(GameMode.class, "gamemode", "game modes", new SimpleLiteral<>(GameMode.SURVIVAL, true))
			.user("game ?modes?")
			.name("Game Mode")
			.description("The game modes survival, creative, adventure and spectator.")
			.examples("player's gamemode is survival",
				"set the player argument's game mode to creative")
			.since("1.0"));

		Classes.registerClass(new ClassInfo<>(Metadatable.class, "metadataholder")
			.user("metadata ?holders?")
			.name("Metadata Holder")
			.description("Something that can hold metadata (e.g. an entity or block)")
			.examples("set metadata value \"super cool\" of player to true")
			.since("2.2-dev36"));

		Classes.registerClass(new ClassInfo<>(PotionEffect.class, "potioneffect")
			.user("potion ?effects?")
			.name("Potion Effect")
			.description("A potion effect, including the potion effect type, tier and duration.")
			.usage("speed of tier 1 for 10 seconds")
			.since("2.5.2")
			.parser(new Parser<>() {

				@Override
				public boolean canParse(ParseContext context) {
					return false;
				}

				@Override
				public String toString(PotionEffect potionEffect, int flags) {
					return PotionEffectUtils.toString(potionEffect);
				}

				@Override
				public String toVariableNameString(PotionEffect o) {
					return "potion_effect:" + o.getType().getKey();
				}

			}).serializer(new Serializer<>() {
				@Override
				public Fields serialize(PotionEffect o) {
					Fields fields = new Fields();
					fields.putObject("type", o.getType().getName());
					fields.putPrimitive("amplifier", o.getAmplifier());
					fields.putPrimitive("duration", o.getDuration());
					fields.putPrimitive("particles", o.hasParticles());
					fields.putPrimitive("ambient", o.isAmbient());
					return fields;
				}

				@Override
				public void deserialize(PotionEffect o, Fields f) {
					assert false;
				}

				@Override
				protected PotionEffect deserialize(Fields fields) throws StreamCorruptedException {
					String typeName = fields.getObject("type", String.class);
					assert typeName != null;
					PotionEffectType type = PotionEffectType.getByName(typeName);
					if (type == null)
						throw new StreamCorruptedException("Invalid PotionEffectType " + typeName);
					int amplifier = fields.getPrimitive("amplifier", int.class);
					int duration = fields.getPrimitive("duration", int.class);
					boolean particles = fields.getPrimitive("particles", boolean.class);
					boolean ambient = fields.getPrimitive("ambient", boolean.class);
					return new PotionEffect(type, duration, amplifier, ambient, particles);
				}

				@Override
				public boolean mustSyncDeserialization() {
					return false;
				}

				@Override
				protected boolean canBeInstantiated() {
					return false;
				}
			}));

		Classes.registerClass(new RegistryClassInfo<>(PotionEffectType.class, Registry.POTION_EFFECT_TYPE, "potioneffecttype")
			.user("potion( ?effect)? ?types?") // "type" had to be made non-optional to prevent clashing with potion effects
			.name("Potion Effect Type")
			.description("A potion effect type, e.g. 'strength' or 'swiftness'.")
			.examples("apply swiftness 5 to the player",
				"apply potion of speed 2 to the player for 60 seconds",
				"remove invisibility from the victim")
			.since(""));

		Classes.registerClass(new EnumClassInfo<>(SoundCategory.class, "soundcategory", "sound categories")
			.user("sound ?categor(y|ies)")
			.name("Sound Category")
			.description("The category of a sound, they are used for sound options of Minecraft. " +
				"See the <a href='effects.html#EffPlaySound'>play sound</a> and <a href='effects.html#EffStopSound'>stop sound</a> effects.")
			.since("2.4"));

		Classes.registerClass(new EnumClassInfo<>(TreeType.class, "treetype", "treetype")
			.user("tree ?types?")
			.name("Tree Type")
			.description("Represents different types of trees that can be grown.")
			.since("3.0.0"));
	}

}
