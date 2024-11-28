// TODO i think i want to redo or remove this
//package ch.njol.skript.expressions;
//
//import org.bukkit.entity.Item;
//import org.bukkit.event.Event;
//import org.bukkit.inventory.ItemStack;
//import org.jetbrains.annotations.Nullable;
//
//import ch.njol.skript.classes.Changer.ChangeMode;
//import ch.njol.skript.doc.Description;
//import ch.njol.skript.doc.Examples;
//import ch.njol.skript.doc.Name;
//import ch.njol.skript.doc.Since;
//import ch.njol.skript.expressions.base.EventValueExpression;
//import ch.njol.skript.util.slot.Slot;
//
//@Name("Item")
//@Description("The item involved in an event, e.g. in a drop, dispense, pickup or craft event.")
//@Examples({
//	"on dispense:",
//		"\titem is a clock",
//		"\tset the time to 6:00"
//})
//@Since("<i>unknown</i> (before 2.1)")
//public class ExprItem extends EventValueExpression<ItemStack> {
//
//	static {
//		register(ExprItem.class, ItemStack.class, "item");
//	}
//
//	public ExprItem() {
//		super(ItemStack.class);
//	}
//
//	@Nullable
//	private EventValueExpression<Item> item;
//
//	@Nullable
//	private EventValueExpression<Slot> slot;
//
//	@Override
//	@Nullable
//	public Class<?>[] acceptChange(ChangeMode mode) {
//		if (mode == ChangeMode.RESET)
//			return null;
//		item = new EventValueExpression<>(Item.class);
//		if (item.init())
//			return new Class[] {ItemStack.class};
//		item = null;
//		slot = new EventValueExpression<>(Slot.class);
//		if (slot.init())
//			return new Class[] {ItemStack.class};
//		slot = null;
//		return null;
//	}
//
//	@Override
//	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
//		assert mode != ChangeMode.RESET;
//		ItemStack itemStack = delta == null ? null : (ItemStack) delta[0];
//		Item item = this.item != null ? this.item.getSingle(event) : null;
//		Slot slot = this.slot != null ? this.slot.getSingle(event) : null;
//		if (item == null && slot == null)
//			return;
//		ItemStack itemstack = item != null ? item.getItemStack() : slot != null ? slot.getItem() : null;
//		switch (mode) {
//			case SET:
//				assert itemStack != null;
//				break;
//			case ADD:
//			case REMOVE:
//			case REMOVE_ALL:
//				assert itemStack != null;
//				if (itemStack.isOfType(itemstack)) {
//					if (mode == ChangeMode.ADD)
//						itemstack = itemStack.addTo(itemstack);
//					else if (mode == ChangeMode.REMOVE)
//						itemstack = itemStack.removeFrom(itemstack);
//					else
//						itemstack = itemStack.removeAll(itemstack);
//				}
//				break;
//			case DELETE:
//				itemstack = null;
//				if (item != null)
//					item.remove();
//				break;
//			case RESET:
//				assert false;
//		}
//		if (item != null && itemstack != null) {
//			item.setItemStack(itemstack);
//		} else if (slot != null) {
//			slot.setItem(itemstack);
//		} else {
//			assert false;
//		}
//	}
//
//}
