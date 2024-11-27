package ch.njol.skript.classes.data.bukkit;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.ConfigurationSerializer;
import ch.njol.skript.classes.EnumClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.data.DefaultChangers;
import ch.njol.skript.classes.registry.RegistryClassInfo;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.StringMode;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Represents {@link ClassInfo ClassInfos} relating to {@link Inventory Inventories} and Items
 */
public class InventoryClasses {

	private InventoryClasses() {

	}

	public static void init() {
		Classes.registerClass(new EnumClassInfo<>(ClickType.class, "clicktype", "click types")
			.user("click ?types?")
			.name("Click Type")
			.description("Click type, mostly for inventory events. Tells exactly which keys/buttons player pressed, " +
				"assuming that default keybindings are used in client side.")
			.examples("")
			.since("2.2-dev16b, 2.2-dev35 (renamed to click type)"));

		Classes.registerClass(new RegistryClassInfo<>(Enchantment.class, Registry.ENCHANTMENT, "enchantment")
			.user("enchantments?")
			.name("Enchantment")
			.description("Represents an enchantment, e.g. 'sharpness' or 'fortune'.",
				"NOTE: Minecraft namespaces are supported, ex: 'minecraft:vanishing_curse'.",
				"As of Minecraft 1.21 this will also support custom enchantments using namespaces, ex: 'myenchants:explosive'.")
			.examples("")
			.since("1.4.6"));

		Classes.registerClass(new EnumClassInfo<>(EquipmentSlot.class, "equipmentslot", "equipment slots")
			.user("equipment ?slots?")
			.name("Equipment Slot")
			.description("Represents the different slot types of an entity.")
			.since("INSERT VERSION"));

		Classes.registerClass(new EnumClassInfo<>(FireworkEffect.Type.class, "fireworktype", "firework types")
			.user("firework ?types?")
			.name("Firework Type")
			.description("The type of a <a href='#fireworkeffect'>fireworkeffect</a>.")
			.since("2.4")
			.documentationId("FireworkType"));

		Classes.registerClass(new ClassInfo<>(FireworkEffect.class, "fireworkeffect")
			.user("firework ?effects?")
			.name("Firework Effect")
			.usage("See <a href='/classes.html#FireworkType'>Firework Types</a>")
			.description(
				"A configuration of effects that defines the firework when exploded",
				"which can be used in the <a href='effects.html#EffFireworkLaunch'>launch firework</a> effect.",
				"See the <a href='expressions.html#ExprFireworkEffect'>firework effect</a> expression for detailed patterns."
			).defaultExpression(new EventValueExpression<>(FireworkEffect.class))
			.examples(
				"launch flickering trailing burst firework colored blue and green at player",
				"launch trailing flickering star colored purple, yellow, blue, green and red fading to pink at target entity",
				"launch ball large colored red, purple and white fading to light green and black at player's location with duration 1"
			).since("2.4")
			.parser(new Parser<>() {
				@Override
				@Nullable
				public FireworkEffect parse(String input, ParseContext context) {
					return null;
				}

				@Override
				public boolean canParse(ParseContext context) {
					return false;
				}

				@Override
				public String toString(FireworkEffect effect, int flags) {
					return "Firework effect " + effect.toString();
				}

				@Override
				public String toVariableNameString(FireworkEffect effect) {
					return "firework effect " + effect.toString();
				}
			}));

		Classes.registerClass(new ClassInfo<>(Inventory.class, "inventory")
			.user("inventor(y|ies)")
			.name("Inventory")
			.description("An inventory of a <a href='#player'>player</a> or <a href='#block'>block</a>. " +
					"Inventories have many effects and conditions regarding the items contained.",
				"An inventory has a fixed amount of <a href='#slot'>slots</a> which represent a specific place in the inventory, " +
					"e.g. the <a href='expressions.html#ExprArmorSlot'>helmet slot</a> for players " +
					"(Please note that slot support is still very limited but will be improved eventually).")
			.usage("")
			.examples("")
			.since("1.0")
			.defaultExpression(new EventValueExpression<>(Inventory.class))
			.parser(new Parser<>() {
				@Override
				@Nullable
				public Inventory parse(final String s, final ParseContext context) {
					return null;
				}

				@Override
				public boolean canParse(final ParseContext context) {
					return false;
				}

				@Override
				public String toString(final Inventory i, final int flags) {
					return "inventory of " + Classes.toString(i.getHolder());
				}

				@Override
				public String getDebugMessage(final Inventory i) {
					return "inventory of " + Classes.getDebugMessage(i.getHolder());
				}

				@Override
				public String toVariableNameString(final Inventory i) {
					return "inventory of " + Classes.toString(i.getHolder(), StringMode.VARIABLE_NAME);
				}
			}).changer(DefaultChangers.inventoryChanger));

		Classes.registerClass(new EnumClassInfo<>(InventoryAction.class, "inventoryaction", "inventory actions")
			.user("inventory ?actions?")
			.name("Inventory Action")
			.description("What player just did in inventory event. Note that when in creative game mode, most actions do not work correctly.")
			.examples("")
			.since("2.2-dev16"));

		Classes.registerClass(new ClassInfo<>(InventoryHolder.class, "inventoryholder")
			.name(ClassInfo.NO_DOC)
			.defaultExpression(new EventValueExpression<>(InventoryHolder.class))
			.after("entity", "block")
			.parser(new Parser<>() {
				@Override
				public boolean canParse(ParseContext context) {
					return false;
				}

				@Override
				public String toString(InventoryHolder holder, int flags) {
					if (holder instanceof BlockState) {
						return Classes.toString(((BlockState) holder).getBlock());
					} else if (holder instanceof DoubleChest) {
						return Classes.toString(holder.getInventory().getLocation().getBlock());
					} else if (holder instanceof BlockInventoryHolder) {
						return Classes.toString(((BlockInventoryHolder) holder).getBlock());
					} else if (Classes.getSuperClassInfo(holder.getClass()).getC() == InventoryHolder.class) {
						return holder.getClass().getSimpleName(); // an inventory holder and only that
					} else {
						return Classes.toString(holder);
					}
				}

				@Override
				public String toVariableNameString(InventoryHolder holder) {
					return toString(holder, 0);
				}
			}));

		Classes.registerClass(new EnumClassInfo<>(InventoryType.class, "inventorytype", "inventory types")
			.user("inventory ?types?")
			.name("Inventory Type")
			.description("Minecraft has several different inventory types with their own use cases.")
			.examples("")
			.since("2.2-dev32"));

		Classes.registerClass(new RegistryClassInfo<>(Material.class, Registry.MATERIAL, "material")
			.user("materials?")
			.name("Material")
			.usage("") // Override RegistryClassInfo usage as there's too many options
			.description("Represents the different types of items and blocks.",
				"NOTE: Minecraft namespaces are supported, ex: 'minecraft:oak_log'.")
			.examples("if material of player's tool = diamond:",
				"if material of target block = oak stairs:",
				"set material of target block to diamond ore",
				"set {_item} to itemstack of diamond axe")
			.since("INSERT VERSION"));

		Classes.registerClass(new ClassInfo<>(ItemStack.class, "itemstack")
			.user("items?", "itemstacks?")
			.name("ItemStack")
			.description("Represents a stack of items in an inventory. May be a single item.")
			.examples("set {_item} to itemstack of diamond sword")
			.since("1.0")
			.after("number")
			.parser(new Parser<>() {
				@Override
				public boolean canParse(ParseContext context) {
					return false;
				}

				@Override
				public String toString(final ItemStack itemStack, final int flags) {
					int amount = itemStack.getAmount();
					String a = amount > 1 ? amount + " " : "";
					return "itemstack of " + a + Classes.toString(itemStack.getType());
				}

				@Override
				public String toVariableNameString(final ItemStack i) {
					return toString(i, 0);
				}
			})
			.cloner(ItemStack::clone)
			.serializer(new ConfigurationSerializer<>()));
	}

}
