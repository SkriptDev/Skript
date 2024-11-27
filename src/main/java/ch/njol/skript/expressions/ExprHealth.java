package ch.njol.skript.expressions;

import ch.njol.skript.bukkitutil.HealthUtils;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Events;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Health")
@Description({"The health of an entity, e.g. a player, mob, villager, etc.",
	"The minimum value is 0, and the maximum is the entity's max health (e.g. 20 for players)."})
@Examples({"message \"You have %health% HP left.\""})
@Since("1.0")
@Events("damage")
public class ExprHealth extends SimplePropertyExpression<LivingEntity, Number> {

	static {
		register(ExprHealth.class, Number.class, "health", "livingentities");
	}

	@Override
	public @Nullable Number convert(LivingEntity entity) {
		return HealthUtils.getHealth(entity);
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode == ChangeMode.REMOVE_ALL)
			return null;
		return CollectionUtils.array(Number.class);
	}

	@Override
	public void change(final Event event, final @Nullable Object[] delta, final ChangeMode mode) {
		double d = delta != null && delta[0] instanceof Number number ? number.doubleValue() : 0;
		Expression<? extends LivingEntity> expr = getExpr();
		if (expr == null) return;

		switch (mode) {
			case DELETE:
			case SET:
				for (final LivingEntity entity : expr.getArray(event)) {
					HealthUtils.setHealth(entity, d);
				}
				break;
			case REMOVE:
				d = -d;
				//$FALL-THROUGH$
			case ADD:
				for (final LivingEntity entity : expr.getArray(event)) {
					assert entity != null : expr;
					HealthUtils.heal(entity, d);
				}
				break;
			case RESET:
				for (final LivingEntity entity : expr.getArray(event)) {
					assert entity != null : expr;
					HealthUtils.setHealth(entity, HealthUtils.getMaxHealth(entity));
				}
				break;
			case REMOVE_ALL:
				assert false;
		}
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	protected String getPropertyName() {
		return "the health";
	}

}
