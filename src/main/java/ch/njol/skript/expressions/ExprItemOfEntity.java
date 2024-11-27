package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("Item of an Entity")
@Description({"An ItemStack associated with an entity.",
	"For dropped item entities, it gets the item that was dropped.",
	"For item frames, the item inside the frame is returned.",
	"For throwable projectiles (snowballs, enderpearls etc.) it gets the displayed item.",
	"For ItemDisplay entities it gets the item show on display.",
	"Other entities do not have items associated with them.",
	"Deleting the item of an ItemFrame will just remove the ItemStack from the frame but deleting the item of " +
		"a dropped item or projectile will remove the entity itself."})
@Examples({"set {_item} to item of target entity of player",
	"set item of target entity of player to itemstack of diamond"})
@Since("2.2-dev35, 2.2-dev36 (improved), 2.5.2 (throwable projectiles)")
public class ExprItemOfEntity extends SimplePropertyExpression<Entity, ItemStack> {

	static {
		register(ExprItemOfEntity.class, ItemStack.class,
			"item[stack]", "entities");
	}

	@Override
	@Nullable
	public ItemStack convert(Entity entity) {
		ItemStack itemStack = null;
		if (entity instanceof ItemFrame itemFrame) {
			itemStack = itemFrame.getItem();
		} else if (entity instanceof Item item) {
			itemStack = item.getItemStack();
		} else if (entity instanceof ThrowableProjectile projectile) {
			itemStack = projectile.getItem();
		} else if (entity instanceof ItemDisplay itemDisplay) {
			itemStack = itemDisplay.getItemStack();
		}
		if (itemStack != null && itemStack.getType().isAir()) return null;
		return itemStack;
	}

	@Override
	public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.DELETE) {
			return CollectionUtils.array(ItemStack.class);
		}
		return null;
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		ItemStack itemStack = null;
		if (delta != null && delta[0] instanceof ItemStack is) itemStack = is;

		Expression<? extends Entity> expr = getExpr();
		if (expr == null) return;

		for (Entity entity : expr.getArray(event)) {
			if (entity instanceof ItemFrame itemFrame) {
				itemFrame.setItem(itemStack);
			} else if (entity instanceof Item item) {
				if (itemStack == null) {
					item.remove();
				} else {
					item.setItemStack(itemStack);
				}
			} else if (entity instanceof ThrowableProjectile projectile) {
				if (itemStack == null) {
					projectile.remove();
				} else {
					projectile.setItem(itemStack);
				}
			} else if (entity instanceof ItemDisplay itemDisplay) {
				itemDisplay.setItemStack(itemStack);
			}
		}
	}

	@Override
	protected String getPropertyName() {
		return "item of entity";
	}

	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

}
