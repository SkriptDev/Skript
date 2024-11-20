package ch.njol.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

public class ExprEntityTypeOf extends SimplePropertyExpression<Entity, EntityType> {

	static {
		register(ExprEntityTypeOf.class, EntityType.class, "entity[ ]type", "entities");
	}

	@Override
	public @Nullable EntityType convert(Entity from) {
		return from.getType();
	}

	@Override
	protected String getPropertyName() {
		return "entity type";
	}

	@Override
	public Class<? extends EntityType> getReturnType() {
		return EntityType.class;
	}

}
