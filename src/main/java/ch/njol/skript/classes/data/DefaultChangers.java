package ch.njol.skript.classes.data;

import ch.njol.skript.bukkitutil.ItemUtils;
import ch.njol.skript.bukkitutil.PlayerUtils;
import ch.njol.skript.classes.Changer;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

/**
 * @author Peter Güttinger
 */
public class DefaultChangers {

	public DefaultChangers() {
	}

	public final static Changer<Entity> entityChanger = new Changer<>() {
		@Override
		@Nullable
		public Class<?>[] acceptChange(final ChangeMode mode) {
			return switch (mode) {
				case ADD ->
					CollectionUtils.array(Material[].class, ItemStack[].class, Inventory.class, Number[].class);
				case DELETE -> CollectionUtils.array();
				case REMOVE ->
					CollectionUtils.array(PotionEffectType[].class, Material[].class, ItemStack[].class, Inventory.class);
				case REMOVE_ALL ->
					CollectionUtils.array(PotionEffectType[].class, Material[].class, ItemStack[].class);
				case SET, RESET -> null;
			};
		}

		@Override
		public void change(final Entity[] entities, final @Nullable Object[] delta, final ChangeMode mode) {
			if (delta == null) {
				for (final Entity e : entities) {
					if (!(e instanceof Player))
						e.remove();
				}
				return;
			}
			boolean hasItem = false;
			for (final Entity entity : entities) {
				for (final Object object : delta) {
					if (object instanceof PotionEffectType potionEffectType) {
						assert mode == ChangeMode.REMOVE || mode == ChangeMode.REMOVE_ALL;
						if (!(entity instanceof LivingEntity livingEntity))
							continue;
						livingEntity.removePotionEffect(potionEffectType);
					} else {
						if (entity instanceof Player player) {
							final PlayerInventory playerInventory = player.getInventory();
							if (object instanceof Number exp) {
								player.giveExp(exp.intValue());
							} else if (object instanceof Inventory inventory) {
								inventoryChanger.change(new Inventory[]{inventory}, delta, mode);
							} else if (object instanceof ItemStack itemStack) {
								hasItem = true;
								if (mode == ChangeMode.ADD) {
									ItemUtils.addItemToList(itemStack, playerInventory);
								} else if (mode == ChangeMode.REMOVE) {
									ItemUtils.removeItemFromList(itemStack, playerInventory);
								}
							} else if (object instanceof Material material) {
								hasItem = true;
								if (mode == ChangeMode.ADD && material.isItem()) {
									ItemUtils.addItemToList(new ItemStack(material), playerInventory);
								} else if (mode == ChangeMode.REMOVE) {
									playerInventory.remove(material);
								}
							}
						}
					}
				}
				if (entity instanceof Player player && hasItem)
					PlayerUtils.updateInventory(player);
			}
		}
	};

	public final static Changer<Player> playerChanger = new Changer<>() {
		@Override
		@Nullable
		public Class<? extends Object>[] acceptChange(final ChangeMode mode) {
			if (mode == ChangeMode.DELETE)
				return null;
			return entityChanger.acceptChange(mode);
		}

		@Override
		public void change(final Player[] players, final @Nullable Object[] delta, final ChangeMode mode) {
			entityChanger.change(players, delta, mode);
		}
	};

	public final static Changer<Entity> nonLivingEntityChanger = new Changer<>() {
		@Override
		@Nullable
		public Class<Object>[] acceptChange(final ChangeMode mode) {
			if (mode == ChangeMode.DELETE)
				return CollectionUtils.array();
			return null;
		}

		@Override
		public void change(final Entity[] entities, final @Nullable Object[] delta, final ChangeMode mode) {
			assert mode == ChangeMode.DELETE;
			for (final Entity e : entities) {
				if (e instanceof Player)
					continue;
				e.remove();
			}
		}
	};

	public final static Changer<Item> itemChanger = new Changer<>() {
		@Override
		@Nullable
		public Class<?>[] acceptChange(final ChangeMode mode) {
			if (mode == ChangeMode.SET)
				return CollectionUtils.array(ItemStack.class);
			return nonLivingEntityChanger.acceptChange(mode);
		}

		@Override
		public void change(final Item[] what, final @Nullable Object[] delta, final ChangeMode mode) {
			if (mode == ChangeMode.SET) {
				assert delta != null;
				for (final Item i : what)
					i.setItemStack((ItemStack) delta[0]);
			} else {
				nonLivingEntityChanger.change(what, delta, mode);
			}
		}
	};

	public final static Changer<Inventory> inventoryChanger = new Changer<>() {

		@Override
		@Nullable
		public Class<? extends Object>[] acceptChange(final ChangeMode mode) {
			if (mode == ChangeMode.RESET)
				return null;
			if (mode == ChangeMode.REMOVE_ALL)
				return CollectionUtils.array(Material[].class, ItemStack[].class);
			return CollectionUtils.array(Material[].class, ItemStack[].class, Inventory[].class);
		}

		@Override
		public void change(final Inventory[] inventories, final @Nullable Object[] delta, final ChangeMode mode) {
			for (final Inventory inventory : inventories) {
				assert inventory != null;
				switch (mode) {
					case DELETE:
						inventory.clear();
						break;
					case SET:
						inventory.clear();
						//$FALL-THROUGH$
					case ADD:
						for (final Object object : delta) {
							if (object instanceof ItemStack itemStack) {
								ItemUtils.addItemToList(itemStack, inventory);
							} else if (object instanceof Material material) {
								ItemUtils.addItemToList(new ItemStack(material), inventory);
							} else if (object instanceof Inventory inv) {
								ItemUtils.addListToList(inv, inventory);
							}
						}

						break;
					case REMOVE:
					case REMOVE_ALL:
						assert delta != null;

						// Slow path
						for (final Object object : delta) {
							if (object instanceof Inventory inv) {
								for (ItemStack itemStack : inv) {
									ItemUtils.removeItemFromList(itemStack, inventory);
								}
							} else if (object instanceof ItemStack itemStack) {
								ItemUtils.removeItemFromList(itemStack, inventory);
							} else if (object instanceof Material material) {
								inventory.remove(material);
							}
						}
						break;
					case RESET:
						assert false;
				}
				InventoryHolder holder = inventory.getHolder();
				if (holder instanceof Player) {
					((Player) holder).updateInventory();
				}
			}
		}
	};

	public final static Changer<Block> blockChanger = new Changer<>() {
		@Override
		@Nullable
		public Class<?>[] acceptChange(final ChangeMode mode) {
			if (mode == ChangeMode.SET || mode == ChangeMode.DELETE)
				return CollectionUtils.array(Material.class, ItemStack.class, BlockData.class);
			return null;
		}

		@Override
		public void change(final Block[] blocks, final @Nullable Object[] delta, final ChangeMode mode) {
			for (Block block : blocks) {
				assert block != null;
				switch (mode) {
					case SET:
						assert delta != null;
						Object object = delta[0];
						if (object instanceof Material material && material.isBlock()) {
							block.setType(material);
						} else if (object instanceof ItemStack itemStack && itemStack.getType().isBlock()) {
							block.setType(itemStack.getType());
						} else if (object instanceof BlockData blockData) {
							block.setBlockData(blockData);
						}
						break;
					case DELETE:
						block.setType(Material.AIR);
				}
			}
		}
	};

}
