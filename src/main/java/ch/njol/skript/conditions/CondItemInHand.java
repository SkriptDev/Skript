package ch.njol.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("Is Holding")
@Description({"Checks whether a player is holding a specific Material/ItemStack.",
	"Cannot be used with endermen, use 'entity is [not] an enderman holding &lt;item type&gt;' instead."})
@Examples({"player is holding a stick",
	"victim isn't holding a sword of sharpness"})
@Since("1.0")
public class CondItemInHand extends Condition {

	static {
		Skript.registerCondition(CondItemInHand.class,
			"[%livingentities%] ha(s|ve) %materials/itemstacks% in [main] hand",
			"[%livingentities%] (is|are) holding %materials/itemstacks% [in main hand]",
			"[%livingentities%] ha(s|ve) %materials/itemstacks% in off[(-| )]hand",
			"[%livingentities%] (is|are) holding %materials/itemstacks% in off[(-| )]hand",
			"[%livingentities%] (ha(s|ve) not|do[es]n't have) %materials/itemstacks% in [main] hand",
			"[%livingentities%] (is not|isn't) holding %materials/itemstacks% [in main hand]",
			"[%livingentities%] (ha(s|ve) not|do[es]n't have) %materials/itemstacks% in off[(-| )]hand",
			"[%livingentities%] (is not|isn't) holding %materials/itemstacks% in off[(-| )]hand"
		);
	}

	private Expression<LivingEntity> entities;
	private Expression<?> items;

	private boolean offTool;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		entities = (Expression<LivingEntity>) exprs[0];
		items = LiteralUtils.defendExpression(exprs[1]);
		offTool = (matchedPattern == 2 || matchedPattern == 3 || matchedPattern == 6 || matchedPattern == 7);
		setNegated(matchedPattern >= 4);
		return true;
	}

	@Override
	public boolean check(Event event) {
		return entities.check(event,
			livingEntity -> items.check(event,
				itemType -> {
					EntityEquipment equipment = livingEntity.getEquipment();
					if (equipment == null)
						return false; // No equipment -> no item in hand
					ItemStack handItem = offTool ? equipment.getItemInOffHand() : equipment.getItemInMainHand();
					if (itemType instanceof Material material) {
						return handItem.getType() == material;
					} else if (itemType instanceof ItemStack itemStack) {
						return handItem.isSimilar(itemStack);
					}
					return false;
				}), isNegated());
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return entities.toString(e, debug) + " " + (entities.isSingle() ? "is" : "are")
			+ " holding " + items.toString(e, debug)
			+ (offTool ? " in off-hand" : "");
	}

}
