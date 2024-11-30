package ch.njol.skript.classes.data.bukkit;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.classes.data.DefaultChangers;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.bukkitutil.BlockUtils;
import ch.njol.yggdrasil.Fields;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

import java.io.StreamCorruptedException;

/**
 * Represents {@link ClassInfo ClassInfos} relating to {@link Block Blocks}
 */
public class BlockClasses {

	private BlockClasses() {
	}

	public static void init() {
		Classes.registerClass(new ClassInfo<>(Block.class, "block")
			.user("blocks?")
			.name("Block")
			.description("A block in a <a href='#world'>world</a>.",
				"It has a <a href='#location'>location</a> and a <a href='#material'>type</a>, " +
					"and can also have a <a href='#direction'>direction</a> (mostly a <a href='expressions.html#ExprFacing'>facing</a>), " +
					"an <a href='#inventory'>inventory</a>, or other special properties.")
			.since("1.0")
			.defaultExpression(new EventValueExpression<>(Block.class))
			.parser(new Parser<>() {
				@Override
				@Nullable
				public Block parse(final String s, final ParseContext context) {
					return null;
				}

				@Override
				public boolean canParse(final ParseContext context) {
					return false;
				}

				@Override
				public String toString(final Block b, final int flags) {
					return BlockUtils.blockToString(b, flags);
				}

				@Override
				public String toVariableNameString(final Block b) {
					return b.getWorld().getName() + ":" + b.getX() + "," + b.getY() + "," + b.getZ();
				}

				@Override
				public String getDebugMessage(final Block b) {
					return toString(b, 0) + " block (" + b.getWorld().getName() + ":" + b.getX() + "," + b.getY() + "," + b.getZ() + ")";
				}
			})
			.changer(DefaultChangers.blockChanger)
			.serializer(new Serializer<>() {
				@Override
				public Fields serialize(final Block b) {
					final Fields f = new Fields();
					f.putObject("world", b.getWorld());
					f.putPrimitive("x", b.getX());
					f.putPrimitive("y", b.getY());
					f.putPrimitive("z", b.getZ());
					return f;
				}

				@Override
				protected Block deserialize(final Fields fields) throws StreamCorruptedException {
					final World w = fields.getObject("world", World.class);
					final int x = fields.getPrimitive("x", int.class), y = fields.getPrimitive("y", int.class), z = fields.getPrimitive("z", int.class);
					if (w == null)
						throw new StreamCorruptedException();
					return w.getBlockAt(x, y, z);
				}

				@Override
				public boolean mustSyncDeserialization() {
					return true;
				}

				@Override
				public boolean canBeInstantiated() {
					return false;
				}
			}));

		Classes.registerClass(new ClassInfo<>(BlockData.class, "blockdata")
			.user("block ?datas?")
			.name("Block Data")
			.description("Block data is the detailed information about a block, referred to in Minecraft as BlockStates, " +
				"allowing for the manipulation of different aspects of the block, including shape, waterlogging, direction the block is facing, " +
				"and so much more. Information regarding each block's optional data can be found on Minecraft's Wiki. Find the block you're " +
				"looking for and scroll down to 'Block States'. Different states must be separated by a semicolon (see examples). " +
				"The 'minecraft:' namespace is optional, as well as are underscores.")
			.examples("set block at player to campfire[lit=false]",
				"set target block of player to oak stairs[facing=north;waterlogged=true]",
				"set block at player to grass_block[snowy=true]",
				"set loop-block to minecraft:chest[facing=north]",
				"set block above player to oak_log[axis=y]",
				"set target block of player to minecraft:oak_leaves[distance=2;persistent=false]")
			.after("material")
			.since("2.5")
			.parser(new Parser<>() {
				@Nullable
				@Override
				public BlockData parse(String input, ParseContext context) {
					return BlockUtils.createBlockData(input);
				}

				@Override
				public String toString(BlockData o, int flags) {
					return o.getAsString().replace(",", ";");
				}

				@Override
				public String toVariableNameString(BlockData o) {
					return "blockdata:" + o.getAsString();
				}
			})
			.serializer(new Serializer<>() {
				@Override
				public Fields serialize(BlockData o) {
					Fields f = new Fields();
					f.putObject("blockdata", o.getAsString());
					return f;
				}

				@Override
				protected BlockData deserialize(Fields f) throws StreamCorruptedException {
					String data = f.getObject("blockdata", String.class);
					assert data != null;
					try {
						return Bukkit.createBlockData(data);
					} catch (IllegalArgumentException ex) {
						throw new StreamCorruptedException("Invalid block data: " + data);
					}
				}

				@Override
				public boolean mustSyncDeserialization() {
					return true;
				}

				@Override
				protected boolean canBeInstantiated() {
					return false;
				}
			}).cloner(BlockData::clone));
	}

}
