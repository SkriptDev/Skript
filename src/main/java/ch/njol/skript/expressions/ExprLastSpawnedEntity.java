// TODO this needs a rework
package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.effects.EffDrop;
import ch.njol.skript.effects.EffFireworkLaunch;
import ch.njol.skript.effects.EffLightning;
import ch.njol.skript.effects.EffShoot;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.sections.EffSecSpawn;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Last Spawned Entity")
@Description("Holds the entity that was spawned most recently with the spawn effect (section), dropped with the <a href='../effects/#EffDrop'>drop effect</a>, shot with the <a href='../effects/#EffShoot'>shoot effect</a> or created with the <a href='../effects/#EffLightning'>lightning effect</a>. " +
	"Please note that even though you can spawn multiple mobs simultaneously (e.g. with 'spawn 5 creepers'), only the last spawned mob is saved and can be used. " +
	"If you spawn an entity, shoot a projectile and drop an item you can however access all them together.")
@Examples({
	"spawn a priest",
	"set {healer::%spawned priest%} to true",
	"shoot an arrow from the last spawned entity",
	"ignite the shot projectile",
	"drop a diamond sword",
	"push last dropped item upwards",
	"teleport player to last struck lightning",
	"delete last launched firework"
})
@Since("1.3 (spawned entity), 2.0 (shot entity), 2.2-dev26 (dropped item), 2.7 (struck lightning, firework)")
public class ExprLastSpawnedEntity extends SimpleExpression<Entity> {

	static {
		Skript.registerExpression(ExprLastSpawnedEntity.class, Entity.class, ExpressionType.SIMPLE,
			"[the] [last[ly]] (0:spawned|1:shot) %*entitytype%",
			"[the] [last[ly]] dropped (2:item)",
			"[the] [last[ly]] (created|struck) (3:lightning)",
			"[the] [last[ly]] (launched|deployed) (4:firework)");
	}

	@SuppressWarnings("NotNullFieldNotInitialized")
	private EntityType type;
	private int from;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		from = parseResult.mark;
		if (from == 2) { // It's just to make an extra expression for item only
			type = EntityType.ITEM;
		} else if (from == 3) {
			type = EntityType.LIGHTNING_BOLT;
		} else if (from == 4) {
			type = EntityType.FIREWORK_ROCKET;
		} else {
			type = ((Literal<EntityType>) exprs[0]).getSingle();
		}
		return true;
	}

	@Override
	@Nullable
	protected Entity[] get(Event event) {
		Entity entity = switch (from) {
			case 0 -> EffSecSpawn.lastSpawned;
			case 1 -> EffShoot.lastSpawned;
			case 2 -> EffDrop.lastSpawned;
			case 3 -> EffLightning.lastSpawned;
			case 4 -> EffFireworkLaunch.lastSpawned;
			default -> null;
		};

		if (entity == null)
			return null;
		if (type != entity.getType())
			return null;

		return new Entity[]{entity};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Entity> getReturnType() {
		return type.getEntityClass();
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		String word = "";
		switch (from) {
			case 0:
				word = "spawned";
				break;
			case 1:
				word = "shot";
				break;
			case 2:
				word = "dropped";
				break;
			case 3:
				word = "struck";
				break;
			case 4:
				word = "launched";
				break;
			default:
				assert false;
		}
		return "the last " + word + " " + type;
	}

}
