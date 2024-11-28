package ch.njol.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

@Name("Entity Type")
@Description("Get the EntityType of an entity.")
@Examples({"set {_type} to entity type of target entity",
	"if entity type of target = pig:"})
@Since("3.0.0")
public class ExprEntityTypeOf extends SimplePropertyExpression<Entity, EntityType> {

	static {
		register(ExprEntityTypeOf.class, EntityType.class, "entity[ ]type[s]", "entities");
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
