package ch.njol.skript.classes.data.defaults;

import ch.njol.skript.Skript;
import ch.njol.skript.command.Commands;
import ch.njol.skript.util.BlockInventoryHolder;
import ch.njol.skript.util.Direction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.skriptlang.skript.lang.converter.Converter;
import org.skriptlang.skript.lang.converter.Converters;

import java.util.UUID;

public class DefaultConverters {

	private DefaultConverters() {
	}

	static void init() {
		// Number to subtypes converters
		Converters.registerConverter(Number.class, Byte.class, Number::byteValue);
		Converters.registerConverter(Number.class, Double.class, Number::doubleValue);
		Converters.registerConverter(Number.class, Float.class, Number::floatValue);
		Converters.registerConverter(Number.class, Integer.class, Number::intValue);
		Converters.registerConverter(Number.class, Long.class, Number::longValue);
		Converters.registerConverter(Number.class, Short.class, Number::shortValue);

		// OfflinePlayer - Player
		Converters.registerConverter(OfflinePlayer.class, Player.class, OfflinePlayer::getPlayer, Commands.CONVERTER_NO_COMMAND_ARGUMENTS);

		// CommandSender - Player
		Converters.registerConverter(CommandSender.class, Player.class, s -> {
			if (s instanceof Player)
				return (Player) s;
			return null;
		});

		// BlockCommandSender - Block
		Converters.registerConverter(BlockCommandSender.class, Block.class, BlockCommandSender::getBlock);

		// Entity - Player
		Converters.registerConverter(Entity.class, Player.class, e -> {
			if (e instanceof Player)
				return (Player) e;
			return null;
		});

		// Entity - LivingEntity // Entity->Player is used if this doesn't exist
		Converters.registerConverter(Entity.class, LivingEntity.class, e -> {
			if (e instanceof LivingEntity)
				return (LivingEntity) e;
			return null;
		});

		// Block - Inventory
		Converters.registerConverter(Block.class, Inventory.class, b -> {
			if (b.getState() instanceof InventoryHolder)
				return ((InventoryHolder) b.getState()).getInventory();
			return null;
		}, Commands.CONVERTER_NO_COMMAND_ARGUMENTS);

		// Block - Location
		Converters.registerConverter(Block.class, Location.class, Block::getLocation, Commands.CONVERTER_NO_COMMAND_ARGUMENTS);

		// Entity - Location
		Converters.registerConverter(Entity.class, Location.class, Entity::getLocation, Commands.CONVERTER_NO_COMMAND_ARGUMENTS);

		// Block - InventoryHolder
		Converters.registerConverter(Block.class, InventoryHolder.class, b -> {
			BlockState s = b.getState();
			if (s instanceof InventoryHolder)
				return (InventoryHolder) s;
			return null;
		}, Converter.NO_RIGHT_CHAINING | Commands.CONVERTER_NO_COMMAND_ARGUMENTS);

		Converters.registerConverter(InventoryHolder.class, Block.class, holder -> {
			if (holder instanceof BlockState)
				return new BlockInventoryHolder((BlockState) holder);
			if (holder instanceof DoubleChest)
				return holder.getInventory().getLocation().getBlock();
			return null;
		}, Converter.NO_CHAINING);

		// InventoryHolder - Entity
		Converters.registerConverter(InventoryHolder.class, Entity.class, holder -> {
			if (holder instanceof Entity)
				return (Entity) holder;
			return null;
		}, Converter.NO_CHAINING);

		// InventoryHolder - Location
		// since the individual ones can't be trusted to chain.
		Converters.registerConverter(InventoryHolder.class, Location.class, holder -> {
			if (holder instanceof Entity)
				return ((Entity) holder).getLocation();
			if (holder instanceof Block)
				return ((Block) holder).getLocation();
			if (holder instanceof BlockState)
				return ((BlockState) holder).getBlock().getLocation();
			if (holder instanceof DoubleChest) {
				DoubleChest doubleChest = (DoubleChest) holder;
				if (doubleChest.getLeftSide() != null) {
					return ((BlockState) doubleChest.getLeftSide()).getBlock().getLocation();
				} else if (((DoubleChest) holder).getRightSide() != null) {
					return ((BlockState) doubleChest.getRightSide()).getBlock().getLocation();
				}
			}
			return null;
		});

		// Vector - Direction
		Converters.registerConverter(Vector.class, Direction.class, Direction::new);

		Converters.registerConverter(String.class, World.class, Bukkit::getWorld);

		// Material - ItemStack
		Converters.registerConverter(Material.class, ItemStack.class, material -> {
			Skript.warning("While Materials can be converted to ItemStacks, you should use the ItemStack expression instead.");
			if (!material.isItem()) return null;
			return new ItemStack(material);
		});

		// UUID - String
		Converters.registerConverter(UUID.class, String.class, UUID::toString);
	}

}
