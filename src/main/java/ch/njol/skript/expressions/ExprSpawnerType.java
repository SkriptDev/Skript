package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.TrialSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.spawner.TrialSpawnerConfiguration;
import org.jetbrains.annotations.Nullable;

@Name("Spawner Type")
@Description("Retrieves, sets, or resets the spawner's entity type")
@Examples({
	"on right click:",
	"\tif event-block is spawner:",
	"\t\tsend \"Spawner's type is %target block's entity type%\""
})
@Since("2.4, 2.9.2 (trial spawner)")
public class ExprSpawnerType extends SimplePropertyExpression<Block, EntityType> {

	private static final boolean HAS_TRIAL_SPAWNER = Skript.classExists("org.bukkit.block.TrialSpawner");

	static {
		register(ExprSpawnerType.class, EntityType.class, "(spawner|entity|creature) type[s]", "blocks");
	}

	@Nullable
	public EntityType convert(Block block) {
		if (block.getState() instanceof CreatureSpawner creatureSpawner) {
			return creatureSpawner.getSpawnedType();
		}
		if (HAS_TRIAL_SPAWNER && block.getState() instanceof TrialSpawner trialSpawner) {
			EntityType type;
			if (trialSpawner.isOminous()) {
				type = trialSpawner.getOminousConfiguration().getSpawnedType();
			} else {
				type = trialSpawner.getNormalConfiguration().getSpawnedType();
			}
			return type;
		}
		return null;
	}

	@Nullable
	@Override
	public Class<?>[] acceptChange(Changer.ChangeMode mode) {
		switch (mode) {
			case SET:
			case RESET:
				return CollectionUtils.array(EntityType.class);
			default:
				return null;
		}
	}

	@SuppressWarnings("null")
	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		for (Block block : getExpr().getArray(event)) {
			if (block.getState() instanceof CreatureSpawner) {
				CreatureSpawner spawner = (CreatureSpawner) block.getState();
				switch (mode) {
					case SET:
						assert delta != null;
						spawner.setSpawnedType((EntityType) delta[0]);
						break;
					case RESET:
						spawner.setSpawnedType(null);
						break;
				}
				spawner.update(); // Actually trigger the spawner's update
			} else if (HAS_TRIAL_SPAWNER && block.getState() instanceof TrialSpawner) {
				TrialSpawner trialSpawner = (TrialSpawner) block.getState();
				TrialSpawnerConfiguration config;
				if (trialSpawner.isOminous()) {
					config = trialSpawner.getOminousConfiguration();
				} else {
					config = trialSpawner.getNormalConfiguration();
				}
				switch (mode) {
					case SET:
						assert delta != null;
						config.setSpawnedType((EntityType) delta[0]);
						break;
					case RESET:
						config.setSpawnedType(null);
						break;
				}
				trialSpawner.update();
			}
		}
	}

	@Override
	public Class<EntityType> getReturnType() {
		return EntityType.class;
	}

	@Override
	protected String getPropertyName() {
		return "entity type";
	}

}
