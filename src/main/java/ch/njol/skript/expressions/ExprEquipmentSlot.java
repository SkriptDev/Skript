package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Keywords;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Equipment Slot")
@Description("Equipment of living entities, i.e. the head, chest, legs, feet slots.")
@Examples({"set chest slot of player to itemstack of diamond chestplate",
	"delete chest slot of player",
	"set {_i::*} to feet slot of all players",
	"set body slot of all llamas to red carpet"})
@Keywords("armor")
@Since("1.0, 2.8.0 (Armour)")
public class ExprEquipmentSlot extends SimpleExpression<ItemStack> {

	static {
		Skript.registerExpression(ExprEquipmentSlot.class, ItemStack.class, ExpressionType.COMBINED,
			"%equipmentslot% slot[s] of %livingentities%",
			"%livingentities%'[s] %equipmentslot% slot[s]");
	}

	private Expression<EquipmentSlot> equipmentSlot;
	private Expression<LivingEntity> livingEntity;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		this.equipmentSlot = (Expression<EquipmentSlot>) exprs[matchedPattern];
		this.livingEntity = (Expression<LivingEntity>) exprs[matchedPattern ^ 1];
		return true;
	}

	@Override
	protected @Nullable ItemStack[] get(Event event) {
		List<ItemStack> items = new ArrayList<>();

		EquipmentSlot slot = this.equipmentSlot.getSingle(event);
		if (slot == null) return null;

		for (LivingEntity livingEntity : this.livingEntity.getArray(event)) {
			EntityEquipment equipment = livingEntity.getEquipment();
			if (equipment == null) continue;

			if (livingEntity.canUseEquipmentSlot(slot)) {
				ItemStack item = equipment.getItem(slot);
				items.add(item);
			}
		}
		return items.toArray(new ItemStack[0]);
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
		EquipmentSlot slot = this.equipmentSlot.getSingle(event);
		if (slot == null) return;

		ItemStack itemStack = null;
		if (mode == ChangeMode.SET && delta != null && delta[0] instanceof ItemStack is) {
			itemStack = is;
		}
		for (LivingEntity entity : this.livingEntity.getArray(event)) {
			EntityEquipment equipment = entity.getEquipment();
			if (equipment == null) continue;

			equipment.setItem(slot, itemStack);
		}
	}

	@Override
	public boolean isSingle() {
		return this.livingEntity.isSingle();
	}

	@Override
	public Class<ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return this.equipmentSlot.toString(event, debug) + " slot of " + this.livingEntity.toString(event, debug);
	}

}
