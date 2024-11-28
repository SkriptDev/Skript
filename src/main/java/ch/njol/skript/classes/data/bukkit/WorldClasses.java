package ch.njol.skript.classes.data.bukkit;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.EnumClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.classes.registry.RegistryClassInfo;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.yggdrasil.Fields;
import io.papermc.paper.world.MoonPhase;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.io.StreamCorruptedException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents {@link ClassInfo ClassInfos} relating to the {@link World}
 */
public class WorldClasses {

	private WorldClasses() {
	}

	public static void init() {
		Classes.registerClass(new ClassInfo<>(Location.class, "location")
			.user("locations?")
			.name("Location")
			.description("A location in a <a href='#world'>world</a>. Locations are world-specific and even store a <a href='#direction'>direction</a>, " +
				"e.g. if you save a location and later teleport to it you will face the exact same direction you did when you saved the location.")
			.usage("")
			.examples("")
			.since("1.0")
			.defaultExpression(new EventValueExpression<>(Location.class))
			.parser(new Parser<>() {
				@Override
				@Nullable
				public Location parse(final String s, final ParseContext context) {
					return null;
				}

				@Override
				public boolean canParse(final ParseContext context) {
					return false;
				}

				@Override
				public String toString(final Location l, final int flags) {
					String worldPart = l.getWorld() == null ? "" : " in '" + l.getWorld().getName() + "'"; // Safety: getWorld is marked as Nullable by spigot
					return "x: " + Skript.toString(l.getX()) + ", y: " + Skript.toString(l.getY()) + ", z: " + Skript.toString(l.getZ()) + ", yaw: " + Skript.toString(l.getYaw()) + ", pitch: " + Skript.toString(l.getPitch()) + worldPart;
				}

				@Override
				public String toVariableNameString(final Location l) {
					return l.getWorld().getName() + ":" + l.getX() + "," + l.getY() + "," + l.getZ();
				}

				@Override
				public String getDebugMessage(final Location l) {
					return "(" + l.getWorld().getName() + ":" + l.getX() + "," + l.getY() + "," + l.getZ() + "|yaw=" + l.getYaw() + "/pitch=" + l.getPitch() + ")";
				}
			}).serializer(new Serializer<>() {
				@Override
				public Fields serialize(Location location) {
					Fields fields = new Fields();
					World world = null;
					try {
						world = location.getWorld();
					} catch (IllegalArgumentException exception) {
						Skript.warning("A location failed to serialize with its defined world, as the world was unloaded.");
					}
					fields.putObject("world", world);
					fields.putPrimitive("x", location.getX());
					fields.putPrimitive("y", location.getY());
					fields.putPrimitive("z", location.getZ());
					fields.putPrimitive("yaw", location.getYaw());
					fields.putPrimitive("pitch", location.getPitch());
					return fields;
				}

				@Override
				public void deserialize(final Location o, final Fields f) {
					assert false;
				}

				@Override
				public Location deserialize(final Fields f) throws StreamCorruptedException {
					return new Location(f.getObject("world", World.class),
						f.getPrimitive("x", double.class), f.getPrimitive("y", double.class), f.getPrimitive("z", double.class),
						f.getPrimitive("yaw", float.class), f.getPrimitive("pitch", float.class));
				}

				@Override
				public boolean canBeInstantiated() {
					return false; // no nullary constructor - also, saving the location manually prevents errors should Location ever be changed
				}

				@Override
				public boolean mustSyncDeserialization() {
					return true;
				}
			})
			.cloner(Location::clone));

		Classes.registerClass(new ClassInfo<>(Vector.class, "vector")
			.user("vectors?")
			.name("Vector")
			.description("Vector is a collection of numbers. In Minecraft, 3D vectors are used to express velocities of entities.")
			.usage("vector(x, y, z)")
			.examples("")
			.since("2.2-dev23")
			.defaultExpression(new EventValueExpression<>(Vector.class))
			.parser(new Parser<>() {
				@Override
				@Nullable
				public Vector parse(final String s, final ParseContext context) {
					return null;
				}

				@Override
				public boolean canParse(final ParseContext context) {
					return false;
				}

				@Override
				public String toString(final Vector vec, final int flags) {
					return "x: " + Skript.toString(vec.getX()) + ", y: " + Skript.toString(vec.getY()) + ", z: " + Skript.toString(vec.getZ());
				}

				@Override
				public String toVariableNameString(final Vector vec) {
					return "vector:" + vec.getX() + "," + vec.getY() + "," + vec.getZ();
				}

				@Override
				public String getDebugMessage(final Vector vec) {
					return "(" + vec.getX() + "," + vec.getY() + "," + vec.getZ() + ")";
				}
			})
			.serializer(new Serializer<>() {
				@Override
				public Fields serialize(Vector o) {
					Fields f = new Fields();
					f.putPrimitive("x", o.getX());
					f.putPrimitive("y", o.getY());
					f.putPrimitive("z", o.getZ());
					return f;
				}

				@Override
				public void deserialize(Vector o, Fields f) {
					assert false;
				}

				@Override
				public Vector deserialize(final Fields f) throws StreamCorruptedException {
					return new Vector(f.getPrimitive("x", double.class), f.getPrimitive("y", double.class), f.getPrimitive("z", double.class));
				}

				@Override
				public boolean mustSyncDeserialization() {
					return false;
				}

				@Override
				protected boolean canBeInstantiated() {
					return false;
				}
			})
			.cloner(Vector::clone));

		Classes.registerClass(new ClassInfo<>(World.class, "world")
			.user("worlds?")
			.name("World")
			.description("One of the server's worlds. Worlds can be put into scripts by surrounding their name with double quotes, e.g. \"world_nether\", " +
				"but this might not work reliably as <a href='#string'>text</a> uses the same syntax.")
			.usage("<code>\"world_name\"</code>, e.g. \"world\"")
			.examples("broadcast \"Hello!\" to the world \"world_nether\"")
			.since("1.0, 2.2 (alternate syntax)")
			.after("string")
			.defaultExpression(new EventValueExpression<>(World.class))
			.parser(new Parser<>() {
				@SuppressWarnings("null")
				private final Pattern parsePattern = Pattern.compile("(?:(?:the )?world )?\"(.+)\"", Pattern.CASE_INSENSITIVE);

				@Override
				@Nullable
				public World parse(final String s, final ParseContext context) {
					// REMIND allow shortcuts '[over]world', 'nether' and '[the_]end' (server.properties: 'level-name=world') // inconsistent with 'world is "..."'
					if (context == ParseContext.COMMAND || context == ParseContext.PARSE || context == ParseContext.CONFIG)
						return Bukkit.getWorld(s);
					final Matcher m = parsePattern.matcher(s);
					if (m.matches())
						return Bukkit.getWorld(m.group(1));
					return null;
				}

				@Override
				public String toString(final World w, final int flags) {
					return "world \"" + w.getName() + "\"";
				}

				@Override
				public String toVariableNameString(final World w) {
					return w.getName();
				}
			}).serializer(new Serializer<>() {
				@Override
				public Fields serialize(final World w) {
					final Fields f = new Fields();
					f.putObject("name", w.getName());
					return f;
				}

				@Override
				public void deserialize(final World o, final Fields f) {
					assert false;
				}

				@Override
				public boolean canBeInstantiated() {
					return false;
				}

				@Override
				protected World deserialize(final Fields fields) throws StreamCorruptedException {
					final String name = fields.getObject("name", String.class);
					assert name != null;
					final World w = Bukkit.getWorld(name);
					if (w == null)
						throw new StreamCorruptedException("Missing world " + name);
					return w;
				}

				@Override
				public boolean mustSyncDeserialization() {
					return true;
				}
			}));

		Classes.registerClass(new RegistryClassInfo<>(Biome.class, Registry.BIOME, "biome")
			.user("biomes?")
			.name("Biome")
			.description("All possible biomes Minecraft uses to generate a world.",
				"NOTE: Minecraft namespaces are supported, ex: 'minecraft:basalt_deltas'.")
			.examples("biome at the player is desert")
			.since("1.4.4")
			.after("damagecause"));
		Classes.registerClass(new ClassInfo<>(Chunk.class, "chunk")
			.user("chunks?")
			.name("Chunk")
			.description("A chunk is a cuboid of 16×16×128 (x×z×y) blocks. Chunks are spread on a fixed rectangular grid in their world.")
			.usage("")
			.examples("")
			.since("2.0")
			.parser(new Parser<>() {
				@Override
				@Nullable
				public Chunk parse(final String s, final ParseContext context) {
					return null;
				}

				@Override
				public boolean canParse(final ParseContext context) {
					return false;
				}

				@Override
				public String toString(final Chunk c, final int flags) {
					return "chunk (" + c.getX() + "," + c.getZ() + ") of " + c.getWorld().getName();
				}

				@Override
				public String toVariableNameString(final Chunk c) {
					return c.getWorld().getName() + ":" + c.getX() + "," + c.getZ();
				}
			})
			.serializer(new Serializer<>() {
				@Override
				public Fields serialize(final Chunk c) {
					final Fields f = new Fields();
					f.putObject("world", c.getWorld());
					f.putPrimitive("x", c.getX());
					f.putPrimitive("z", c.getZ());
					return f;
				}

				@Override
				public void deserialize(final Chunk o, final Fields f) {
					assert false;
				}

				@Override
				public boolean canBeInstantiated() {
					return false;
				}

				@Override
				protected Chunk deserialize(final Fields fields) throws StreamCorruptedException {
					final World w = fields.getObject("world", World.class);
					final int x = fields.getPrimitive("x", int.class), z = fields.getPrimitive("z", int.class);
					if (w == null)
						throw new StreamCorruptedException();
					return w.getChunkAt(x, z);
				}

				@Override
				public boolean mustSyncDeserialization() {
					return true;
				}
			}));

		Classes.registerClass(new EnumClassInfo<>(World.Environment.class, "environment", "environments")
			.user("(world ?)?environments?")
			.name("World Environment")
			.description("Represents the environment of a world.")
			.since("2.7"));

		if (Skript.classExists("io.papermc.paper.world.MoonPhase"))
			Classes.registerClass(new EnumClassInfo<>(MoonPhase.class, "moonphase", "moon phases")
				.user("(lunar|moon) ?phases?")
				.name("Moon Phase")
				.description("Represents the phase of a moon.")
				.requiredPlugins("Paper 1.16+")
				.since("2.7"));

	}
}
