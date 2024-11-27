package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Events;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("Clicked Block/Entity/Inventory/Slot")
@Description("The clicked block, entity, inventory, slot, inventory click type or inventory action.")
@Examples({
	"message \"You clicked on a %type of clicked entity%!\"",
	"if the clicked block is a chest:",
	"\tshow the inventory of the clicked block to the player"
})
@Since("1.0, 2.2-dev35 (more clickable things)")
@Events({"click", "inventory click"})
public class ExprClicked extends SimpleExpression<Object> {

	private static enum ClickableType {
		ENCHANT_BUTTON(1, Number.class, "clicked enchantment button", "clicked [enchant[ment]] (button|option)"),
		BLOCK_AND_ITEMS(2, Block.class, "clicked block/itemstack/entity", "clicked (block|entity|%-*itemstack/entitytype%)"),
		SLOT(3, Number.class, "clicked slot", "clicked slot"),
		INVENTORY(4, Inventory.class, "clicked inventory", "clicked inventory"),
		TYPE(5, ClickType.class, "click type", "click (type|action)"),
		ACTION(6, InventoryAction.class, "inventory action", "inventory action"),
		;

		private String name, syntax;
		private Class<?> c;
		private int value;

		private ClickableType(int value, Class<?> c, String name, String syntax) {
			this.syntax = syntax;
			this.value = value;
			this.c = c;
			this.name = name;
		}

		public int getValue() {
			return value;
		}

		public Class<?> getClickableClass() {
			return c;
		}

		public String getName() {
			return name;
		}

		public String getSyntax(boolean last) {
			return value + "¦" + syntax + (!last ? "|" : "");
		}

		public static ClickableType getClickable(int num) {
			for (ClickableType clickable : ClickableType.values())
				if (clickable.getValue() == num)
					return clickable;

			return BLOCK_AND_ITEMS;
		}
	}

	static {
		Skript.registerExpression(ExprClicked.class, Object.class, ExpressionType.SIMPLE, "[the] ("
			// 'clicked enchantment button' must be before 'clicked block' otherwise 'button' will be considered as an itemtype
			+ ClickableType.ENCHANT_BUTTON.getSyntax(false)
			+ ClickableType.BLOCK_AND_ITEMS.getSyntax(false)
			+ ClickableType.SLOT.getSyntax(false)
			+ ClickableType.INVENTORY.getSyntax(false)
			+ ClickableType.TYPE.getSyntax(false)
			+ ClickableType.ACTION.getSyntax(true) + ")");
	}

	@Nullable
	private EntityType entityType;
	@Nullable
	private ItemStack itemStack; // null results in any itemtype
	private ClickableType clickable = ClickableType.BLOCK_AND_ITEMS;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		clickable = ClickableType.getClickable(parseResult.mark);
		switch (clickable) {
			case BLOCK_AND_ITEMS:
				Object type = exprs[0] == null ? null : ((Literal<?>) exprs[0]).getSingle();
				if (type instanceof EntityType et) {
					entityType = et;
					if (!getParser().isCurrentEvent(PlayerInteractEntityEvent.class) && !getParser().isCurrentEvent(PlayerInteractAtEntityEvent.class)) {
						Skript.error("The expression 'clicked entity' may only be used in a click event");
						return false;
					}
				} else {
					itemStack = (ItemStack) type;
					if (!getParser().isCurrentEvent(PlayerInteractEvent.class)) {
						Skript.error("The expression 'clicked block' may only be used in a click event");
						return false;
					}
				}
				break;
			case INVENTORY:
			case ACTION:
			case TYPE:
			case SLOT:
				if (!getParser().isCurrentEvent(InventoryClickEvent.class)) {
					Skript.error("The expression '" + clickable.getName() + "' may only be used in an inventory click event");
					return false;
				}
				break;
			case ENCHANT_BUTTON:
				if (!getParser().isCurrentEvent(EnchantItemEvent.class)) {
					Skript.error("The expression 'clicked enchantment button' is only usable in an enchant event.");
					return false;
				}
				break;
		}
		return true;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Object> getReturnType() {
		return (clickable != ClickableType.BLOCK_AND_ITEMS) ? clickable.getClickableClass() : entityType != null ? Entity.class : Block.class;
	}

	@Override
	@Nullable
	protected Object[] get(Event event) {
		if (!(event instanceof InventoryClickEvent) && clickable != ClickableType.BLOCK_AND_ITEMS && clickable != ClickableType.ENCHANT_BUTTON)
			return null;

		switch (clickable) {
			case BLOCK_AND_ITEMS:
				if (event instanceof PlayerInteractEvent) {
					if (entityType != null) // This is supposed to be null as this event should be for blocks
						return null;
					Block block = ((PlayerInteractEvent) event).getClickedBlock();

					if (itemStack == null)
						return new Block[]{block};
					assert itemStack != null;
					if (itemStack.getType() == block.getType())
						return new Block[]{block};
					return null;
				} else if (event instanceof PlayerInteractEntityEvent) {
					if (entityType == null) //We're testing for the entity in this event
						return null;
					Entity entity = ((PlayerInteractEntityEvent) event).getRightClicked();

					assert entityType != null;
					if (entityType == entity.getType()) {
						assert entityType != null;
						return new Entity[]{entity};
					}
					return null;
				}
				break;
			case TYPE:
				return new ClickType[]{((InventoryClickEvent) event).getClick()};
			case ACTION:
				return new InventoryAction[]{((InventoryClickEvent) event).getAction()};
			case INVENTORY:
				return new Inventory[]{((InventoryClickEvent) event).getClickedInventory()};
			case SLOT:
				InventoryClickEvent clickEvent = ((InventoryClickEvent) event);
				return new Number[]{clickEvent.getSlot()};
			case ENCHANT_BUTTON:
				if (event instanceof EnchantItemEvent)
					return new Number[]{((EnchantItemEvent) event).whichButton() + 1};
				break;
		}
		return null;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "the " + (clickable != ClickableType.BLOCK_AND_ITEMS ? clickable.getName() : "clicked " + (entityType != null ? entityType : itemStack != null ? itemStack : "block"));
	}

}
