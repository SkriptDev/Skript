package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Events;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Drops")
@Description({"Only works in death events.",
	"Holds the drops of the dying creature. Drops can be prevented by removing them with " +
		"\"remove ... from drops\" or \"clear drops\" if you don't want any drops at all."})
@Examples({"clear drops",
	"remove itemstack of 4 of oak planks from the drops"})
@Since("1.0")
@Events("death")
public class ExprDrops extends SimpleExpression<ItemStack> {

	static {
		Skript.registerExpression(ExprDrops.class, ItemStack.class, ExpressionType.SIMPLE,
			"[the] drops");
	}

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (!getParser().isCurrentEvent(EntityDeathEvent.class)) {
			Skript.error("The expression 'drops' can only be used in death events", ErrorQuality.SEMANTIC_ERROR);
			return false;
		}
		return true;
	}

	@Override
	@Nullable
	protected ItemStack[] get(Event e) {
		if (!(e instanceof EntityDeathEvent deathEvent))
			return null;

		return deathEvent.getDrops().toArray(new ItemStack[0]);
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (getParser().getHasDelayBefore().isTrue()) {
			Skript.error("Can't change the drops after the event has already passed");
			return null;
		}
		return switch (mode) {
			case ADD, REMOVE, SET ->
				CollectionUtils.array(Material[].class, ItemStack[].class, Inventory[].class); // handled by EffClearDrops
			default -> null;
		};
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		if (!(event instanceof EntityDeathEvent deathEvent))
			return;

		List<ItemStack> eventDrops = deathEvent.getDrops();
		assert delta != null;

		List<Object> deltaDrops = new ArrayList<>();
		for (Object object : delta) {
			switch (object) {
				case Inventory inventory -> {
					// inventories are unrolled into their contents
					for (ItemStack item : inventory.getContents()) {
						if (item != null)
							deltaDrops.add(item);
					}
				}
				case ItemStack itemStack -> deltaDrops.add(itemStack);
				case Material material -> deltaDrops.add(material);
				case null, default -> {
					assert false;
				}
			}
		}

		if (!deltaDrops.isEmpty()) {
			switch (mode) {
				case SET:
					// clear drops and fallthrough to add
					eventDrops.clear();
				case ADD:
					for (Object deltaDrop : deltaDrops) {
						if (deltaDrop instanceof ItemStack itemStack) {
							eventDrops.add(itemStack);
						} else if (deltaDrop instanceof Material material) {
							eventDrops.add(new ItemStack(material));
						}
					}
					break;
				case REMOVE:
					for (Object deltaDrop : deltaDrops) {
						if (deltaDrop instanceof ItemStack itemStack) {
							eventDrops.remove(itemStack);
						} else if (deltaDrop instanceof Material material) {
							eventDrops.removeIf(itemStack -> itemStack.getType() == material);
						}
					}
					break;
				case DELETE:
					eventDrops.clear();
				case RESET:
					assert false;
			}
		}
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "the drops";
	}

}
