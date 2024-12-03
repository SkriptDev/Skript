package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.PlayerUtils;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;
import org.bukkit.entity.Steerable;
import org.bukkit.event.Event;
import org.bukkit.inventory.AbstractHorseInventory;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.LlamaInventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Equip")
@Description({"Equips an entity with some given armor.",
	"This will replace any armor that the entity is wearing."})
@Examples({"equip player with diamond helmet",
	"equip player with diamond helmet, leather chestplate, golden leggings and iron boots",
	"equip all horses with a saddle and diamond horse armor"})
@Since("1.0, 2.7 (multiple entities)")
public class EffEquip extends Effect {

	// Added in Paper 1.21.3
	private static final boolean HAS_COMPONENTS = Skript.classExists("io.papermc.paper.datacomponent.item.Equippable");

	static {
		Skript.registerEffect(EffEquip.class,
			"equip %livingentities% with %materials/itemstacks%",
			"make %livingentities% wear %materials/itemstacks%");
	}

	private Expression<LivingEntity> entities;
	private Expression<?> items;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
		this.entities = (Expression<LivingEntity>) exprs[0];
		this.items = LiteralUtils.defendExpression(exprs[1]);
		return true;
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	protected void execute(Event event) {
		List<ItemStack> itemStacks = new ArrayList<>();
		for (Object object : this.items.getArray(event)) {
			if (object instanceof ItemStack itemStack) {
				itemStacks.add(itemStack);
			} else if (object instanceof Material material) {
				itemStacks.add(new ItemStack(material));
			}
		}

		for (LivingEntity entity : this.entities.getArray(event)) {
			if (entity instanceof Steerable steerable) {
				for (ItemStack itemStack : itemStacks) {
					if (itemStack.getType() == Material.SADDLE) {
						steerable.setSaddle(true);
						break;
					}
				}
			} else if (entity instanceof Llama llama) {
				LlamaInventory inv = llama.getInventory();
				for (ItemStack itemStack : itemStacks) {
					if (Tag.WOOL_CARPETS.isTagged(itemStack.getType())) {
						inv.setDecor(itemStack);
					} else if (itemStack.getType() == Material.CHEST) {
						llama.setCarryingChest(true);
					}
				}
			} else if (entity instanceof AbstractHorse horse) {
				AbstractHorseInventory inv = horse.getInventory();
				for (ItemStack itemStack : itemStacks) {
					if (itemStack.getType() == Material.SADDLE) {
						inv.setItem(0, itemStack); // Slot 0=saddle
					} else if (itemStack.getType() == Material.CHEST && entity instanceof ChestedHorse chestedHorse) {
						chestedHorse.setCarryingChest(true);
					} else {
						inv.setItem(1, itemStack);
					}
				}
			} else if (HAS_COMPONENTS) {
				EntityEquipment equipment = entity.getEquipment();
				if (equipment == null) return;

				for (ItemStack itemStack : itemStacks) {
					Equippable data = itemStack.getData(DataComponentTypes.EQUIPPABLE);
					if (data == null) continue;

					equipment.setItem(data.slot(), itemStack);
				}
			} else {
				EntityEquipment equipment = entity.getEquipment();
				if (equipment == null) continue;

				for (ItemStack itemStack : itemStacks) {
					Material type = itemStack.getType();

					if (Tag.ITEMS_CHEST_ARMOR.isTagged(type)) {
						equipment.setChestplate(itemStack);
					} else if (Tag.ITEMS_LEG_ARMOR.isTagged(type)) {
						equipment.setLeggings(itemStack);
					} else if (Tag.ITEMS_FOOT_ARMOR.isTagged(type)) {
						equipment.setBoots(itemStack);
					} else {
						// Apply all other items to head, as all items will appear on a player's head
						equipment.setHelmet(itemStack);
					}
				}
				if (entity instanceof Player player)
					PlayerUtils.updateInventory(player);
			}
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		assert items != null;
		return "equip " + entities.toString(event, debug) + " with " + items.toString(event, debug);
	}

}
