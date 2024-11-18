package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Getter;
import ch.njol.util.Kleenean;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

/**
 * @author Peter Güttinger
 */
@Name("Tool")
@Description("The item an entity is holding in their main or off hand.")
@Examples({"player's tool is a pickaxe",
	"player's off hand tool is a shield",
	"set tool of all players to a diamond sword",
	"set offhand tool of target entity to a bow"})
@Since("1.0")
public class ExprTool extends PropertyExpression<LivingEntity, ItemStack> {
	static {
		Skript.registerExpression(ExprTool.class, ItemStack.class, ExpressionType.PROPERTY,
			"[off:off[ ]hand] (tool|held item|weapon) [of %livingentities%]",
			"%livingentities%'[s] [off:off[ ]hand] (tool|held item|weapon)");
	}

	private boolean offHand;

	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
		setExpr((Expression<LivingEntity>) exprs[0]);
		offHand = parser.hasTag("off");
		return true;
	}

	@Override
	protected ItemStack[] get(final Event e, final LivingEntity[] source) {
		return get(source, new Getter<>() {
			@Override
			@Nullable
			public ItemStack get(final LivingEntity ent) {
				if (ent instanceof Player player) {
					PlayerInventory inventory = player.getInventory();
					return offHand ? inventory.getItemInOffHand() : inventory.getItemInMainHand();
				} else {
					EntityEquipment equipment = ent.getEquipment();
					if (equipment == null) return null;

					return offHand ? equipment.getItemInOffHand() : equipment.getItemInMainHand();
				}
			}
		});
	}

	@Override
	public Class<ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		String hand = offHand ? "off hand" : "";
		return String.format("%s tool of %s", hand, getExpr().toString(e, debug));
	}

}
