package ch.njol.skript.classes.data.bukkit;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptConfig;
import ch.njol.skript.bukkitutil.BukkitUtils;
import ch.njol.skript.bukkitutil.EntityCategory;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.EnumClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.classes.data.DefaultChangers;
import ch.njol.skript.classes.registry.RegistryClassInfo;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.localization.Language;
import ch.njol.skript.registrations.Classes;
import ch.njol.yggdrasil.Fields;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.jetbrains.annotations.Nullable;

import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
/**
 * Represents {@link ClassInfo ClassInfos} relating to {@link Entity Entities}
 */
public class EntityClasses {

	private static final Pattern UUID_PATTERN = Pattern.compile("(?i)[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}");

	private EntityClasses() {
	}

	public static void init() {
		entityClasses();
		entityExtra();
	}

	public static void entityClasses() {
		Classes.registerClass(new ClassInfo<>(Entity.class, "entity")
			.user("entit(y|ies)")
			.name("Entity")
			.description("An entity is something in a <a href='#world'>world</a> that's not a <a href='#block'>block</a>, " +
				"e.g. a <a href='#player'>player</a>, a skeleton, or a zombie, but also " +
				"<a href='#projectile'>projectiles</a> like arrows, fireballs or thrown potions, " +
				"or special entities like dropped items, falling blocks or paintings.")
			.usage("player, op, wolf, tamed ocelot, powered creeper, zombie, unsaddled pig, fireball, arrow, dropped item, item frame, etc.")
			.examples("entity is a zombie or creeper",
				"player is an op",
				"projectile is an arrow",
				"shoot a fireball from the player")
			.since("1.0")
			.defaultExpression(new EventValueExpression<>(Entity.class))
			.parser(new Parser<>() {
				@Override
				@Nullable
				public Entity parse(final String s, final ParseContext context) {
					UUID uuid;
					try {
						uuid = UUID.fromString(s);
					} catch (IllegalArgumentException iae) {
						return null;
					}
					return Bukkit.getEntity(uuid);
				}

				@Override
				public boolean canParse(final ParseContext context) {
					return context == ParseContext.COMMAND || context == ParseContext.PARSE;
				}

				@Override
				public String toVariableNameString(final Entity e) {
					return "entity:" + e.getUniqueId().toString().toLowerCase(Locale.ENGLISH);
				}

				@SuppressWarnings("deprecation")
				@Override
				public String toString(final Entity entity, final int flags) {
					if (entity.getCustomName() != null)
						return entity.getCustomName();
					return Classes.toString(entity.getType());
				}
			})
			.changer(DefaultChangers.entityChanger));

		Classes.registerClass(new EnumClassInfo<>(EntityCategory.class, "entitycategory", "entity categories")
			.user("entity ?categor(y|ies)")
			.name("Entity Category")
			.description("Represents different categories of entities.")
			.since("3.0.0"));

		Classes.registerClass(new RegistryClassInfo<>(EntityType.class, Registry.ENTITY_TYPE, "entitytype")
			.user("entity ?types?")
			.name("Entity Type")
			.description("Represents different types of entities.")
			.since("3.0.0"));

		Classes.registerClass(new ClassInfo<>(CommandSender.class, "commandsender")
			.user("((commands?)? ?)?(sender|executor)s?")
			.name("Command Sender")
			.description("A player or the console.")
			.usage("use <a href='expressions.html#LitConsole'>the console</a> for the console",
				"see <a href='#player'>player</a> for players.")
			.examples("command /push [&lt;player&gt;]:",
				"\ttrigger:",
				"\t\tif arg-1 is not set:",
				"\t\t\tif command sender is console:",
				"\t\t\t\tsend \"You can't push yourself as a console :\\\" to sender",
				"\t\t\t\tstop",
				"\t\t\tpush sender upwards with force 2",
				"\t\t\tsend \"Yay!\"",
				"\t\telse:",
				"\t\t\tpush arg-1 upwards with force 2",
				"\t\t\tsend \"Yay!\" to sender and arg-1")
			.since("1.0")
			.defaultExpression(new EventValueExpression<>(CommandSender.class))
			.parser(new Parser<>() {
				@Override
				@Nullable
				public CommandSender parse(final String s, final ParseContext context) {
					return null;
				}

				@Override
				public boolean canParse(final ParseContext context) {
					return false;
				}

				@Override
				public String toString(final CommandSender s, final int flags) {
					return s.getName();
				}

				@Override
				public String toVariableNameString(final CommandSender s) {
					return s.getName();
				}
			}));

		Classes.registerClass(new ClassInfo<>(LivingEntity.class, "livingentity")
			.user("living ?entit(y|ies)")
			.name("Living Entity")
			.description("A living <a href='#entity'>entity</a>, i.e. a mob or <a href='#player'>player</a>, " +
				"not inanimate entities like <a href='#projectile'>projectiles</a> or dropped items.")
			.usage("see <a href='#entity'>entity</a>, but ignore inanimate objects")
			.examples("spawn 5 powered creepers",
				"shoot a zombie from the creeper")
			.since("1.0")
			.defaultExpression(new EventValueExpression<>(LivingEntity.class))
			.changer(DefaultChangers.entityChanger));

		Classes.registerClass(new ClassInfo<>(Item.class, "itementity")
			.name(ClassInfo.NO_DOC)
			.since("2.0")
			.changer(DefaultChangers.itemChanger));

		Classes.registerClass(new ClassInfo<>(OfflinePlayer.class, "offlineplayer")
			.user("offline ?players?")
			.name("Offline Player")
			.description(
				"A player that is possibly offline. See <a href='#player'>player</a> for more information. " +
					"Please note that while all effects and conditions that require a player can be used with an " +
					"offline player as well, they will not work if the player is not actually online."
			).usage(
				"Parsing an offline player as a player (online) will return nothing (none), for that case you would need to parse as " +
					"offlineplayer which only returns nothing (none) if player doesn't exist in Minecraft databases (name not taken) otherwise it will return the player regardless of their online status."
			).examples("set {_p} to \"Notch\" parsed as an offlineplayer # returns Notch even if they're offline")
			.since("2.0 beta 8")
			.defaultExpression(new EventValueExpression<>(OfflinePlayer.class))
			.after("string", "world")
			.parser(new Parser<>() {
				@Override
				@Nullable
				public OfflinePlayer parse(final String s, final ParseContext context) {
					if (context == ParseContext.COMMAND || context == ParseContext.PARSE) {
						if (UUID_PATTERN.matcher(s).matches())
							return Bukkit.getOfflinePlayer(UUID.fromString(s));
						else if (!SkriptConfig.playerNameRegexPattern.value().matcher(s).matches())
							return null;
						return Bukkit.getOfflinePlayer(s);
					}
					assert false;
					return null;
				}

				@Override
				public boolean canParse(ParseContext context) {
					return context == ParseContext.COMMAND || context == ParseContext.PARSE;
				}

				@Override
				public String toString(OfflinePlayer p, int flags) {
					return p.getName() == null ? p.getUniqueId().toString() : p.getName();
				}

				@Override
				public String toVariableNameString(OfflinePlayer p) {
					if (SkriptConfig.usePlayerUUIDsInVariableNames.value() || p.getName() == null)
						return "" + p.getUniqueId();
					else
						return p.getName();
				}

				@Override
				public String getDebugMessage(OfflinePlayer p) {
					if (p.isOnline())
						return Classes.getDebugMessage(p.getPlayer());
					return toString(p, 0);
				}
			}).serializer(new Serializer<>() {
				@Override
				public Fields serialize(final OfflinePlayer p) {
					final Fields f = new Fields();
					f.putObject("uuid", p.getUniqueId());
					return f;
				}

				@Override
				public void deserialize(final OfflinePlayer o, final Fields f) {
					assert false;
				}

				@Override
				public boolean canBeInstantiated() {
					return false;
				}

				@SuppressWarnings("deprecation")
				@Override
				protected OfflinePlayer deserialize(final Fields fields) throws StreamCorruptedException {
					if (fields.contains("uuid")) {
						final UUID uuid = fields.getObject("uuid", UUID.class);
						if (uuid == null)
							throw new StreamCorruptedException();
						return Bukkit.getOfflinePlayer(uuid);
					} else {
						final String name = fields.getObject("name", String.class);
						if (name == null)
							throw new StreamCorruptedException();
						return Bukkit.getOfflinePlayer(name);
					}
				}

				@Override
				public boolean mustSyncDeserialization() {
					return true;
				}
			}));

		Classes.registerClass(new ClassInfo<>(Player.class, "player")
			.user("players?")
			.name("Player")
			.description(
				"A player. Depending on whether a player is online or offline several actions can be performed with them, " +
					"though you won't get any errors when using effects that only work if the player is online (e.g. changing their inventory) on an offline player.",
				"You have two possibilities to use players as command arguments: &lt;player&gt; and &lt;offline player&gt;. " +
					"The first requires that the player is online and also accepts only part of the name, " +
					"while the latter doesn't require that the player is online, but the player's name has to be entered exactly."
			).usage(
				"Parsing an offline player as a player (online) will return nothing (none), for that case you would need to parse as " +
					"offlineplayer which only returns nothing (none) if player doesn't exist in Minecraft databases (name not taken) otherwise it will return the player regardless of their online status."
			).examples(
				"set {_p} to \"Notch\" parsed as a player # returns <none> unless Notch is actually online or starts with Notch like Notchan",
				"set {_p} to \"N\" parsed as a player # returns Notch if Notch is online because their name starts with 'N' (case insensitive) however, it would return nothing if no player whose name starts with 'N' is online."
			).since("1.0")
			.defaultExpression(new EventValueExpression<>(Player.class))
			.after("string", "world")
			.parser(new Parser<>() {
				@Override
				@Nullable
				public Player parse(String string, ParseContext context) {
					if (context == ParseContext.COMMAND || context == ParseContext.PARSE) {
						if (string.isEmpty())
							return null;
						if (UUID_PATTERN.matcher(string).matches())
							return Bukkit.getPlayer(UUID.fromString(string));
						String name = string.toLowerCase(Locale.ENGLISH);
						int nameLength = name.length(); // caching
						List<Player> players = new ArrayList<>();
						for (Player player : Bukkit.getOnlinePlayers()) {
							if (player.getName().toLowerCase(Locale.ENGLISH).startsWith(name)) {
								if (player.getName().length() == nameLength) // a little better in performance than String#equals()
									return player;
								players.add(player);
							}
						}
						if (players.size() == 1)
							return players.get(0);
						if (players.size() == 0)
							Skript.error(String.format(Language.get("commands.no player starts with"), string));
						else
							Skript.error(String.format(Language.get("commands.multiple players start with"), string));
						return null;
					}
					assert false;
					return null;
				}

				@Override
				public boolean canParse(final ParseContext context) {
					return context == ParseContext.COMMAND || context == ParseContext.PARSE;
				}

				@Override
				public String toString(final Player p, final int flags) {
					return p.getName();
				}

				@Override
				public String toVariableNameString(final Player p) {
					if (SkriptConfig.usePlayerUUIDsInVariableNames.value())
						return "" + p.getUniqueId();
					else
						return p.getName();
				}

				@Override
				public String getDebugMessage(final Player p) {
					return p.getName() + " " + Classes.getDebugMessage(p.getLocation());
				}
			})
			.changer(DefaultChangers.playerChanger)
			.serializeAs(OfflinePlayer.class));

		Classes.registerClass(new ClassInfo<>(Projectile.class, "projectile")
			.user("projectiles?")
			.name("Projectile")
			.description("A projectile, e.g. an arrow, snowball or thrown potion.")
			.usage("arrow, fireball, snowball, thrown potion, etc.")
			.examples("projectile is a snowball",
				"shoot an arrow at speed 5 from the player")
			.since("1.0")
			.defaultExpression(new EventValueExpression<>(Projectile.class))
			.changer(DefaultChangers.nonLivingEntityChanger));
	}

	private static void entityExtra() {
		if (Skript.classExists("org.bukkit.entity.Cat$Type")) {
			ClassInfo<Cat.Type> catTypeClassInfo;
			if (BukkitUtils.registryExists("CAT_VARIANT")) {
				catTypeClassInfo = new RegistryClassInfo<>(Cat.Type.class, Registry.CAT_VARIANT, "cattype");
			} else {
				//noinspection unchecked, rawtypes - it is an enum on other versions
				catTypeClassInfo = new EnumClassInfo<>((Class) Cat.Type.class, "cattype", "cat types");
			}
			Classes.registerClass(catTypeClassInfo
				.user("cat ?(type|race)s?")
				.name("Cat Type")
				.description("Represents the race/type of a cat entity.",
					"NOTE: Minecraft namespaces are supported, ex: 'minecraft:british_shorthair'.")
				.since("2.4")
				.requiredPlugins("Minecraft 1.14 or newer")
				.documentationId("CatType"));
		}

		Classes.registerClass(new EnumClassInfo<>(Panda.Gene.class, "gene", "genes")
			.user("(panda )?genes?")
			.name("Gene")
			.description("Represents a Panda's main or hidden gene. " +
				"See <a href='https://minecraft.wiki/w/Panda#Genetics'>genetics</a> for more info.")
			.since("2.4")
			.requiredPlugins("Minecraft 1.14 or newer"));
	}

}
