package ch.njol.skript.effects.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.EffectSection;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Direction;
import ch.njol.skript.util.Getter;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Name("Spawn")
@Description({"Spawn a creature. This can be used as an effect and as a section.",
	"If it is used as a section, the section is run before the entity is added to the world.",
	"You can modify the entity in this section, using for example 'event-entity' or 'cow'. ",
	"Do note that other event values, such as 'player', won't work in this section."
})
@Examples({
	"spawn 3 creepers at the targeted block",
	"spawn a ghast 5 meters above the player",
	"spawn a zombie at the player:",
	"\tset name of the zombie to \"\""
})
@Since("1.0, 2.6.1 (with section), 2.8.6 (dropped items)")
public class EffSecSpawn extends EffectSection {

	public static class SpawnEvent extends Event {
		private final Entity entity;

		public SpawnEvent(Entity entity) {
			this.entity = entity;
		}

		public Entity getEntity() {
			return entity;
		}

		@Override
		@NotNull
		public HandlerList getHandlers() {
			throw new IllegalStateException();
		}
	}

	private static final BlockData DEFAULT_DATA = Material.STONE.createBlockData();

	static {
		Skript.registerSection(EffSecSpawn.class,
			"(spawn|summon) %entitytypes% [%directions% %locations%]",
			"(spawn|summon) %number% [of] %entitytypes%[s] [%directions% %locations%]"
		);
		EventValues.registerEventValue(SpawnEvent.class, Entity.class, new Getter<>() {
			@Override
			public Entity get(SpawnEvent spawnEvent) {
				return spawnEvent.getEntity();
			}
		}, EventValues.TIME_NOW);
	}

	private Expression<Location> locations;
	private Expression<EntityType> types;

	@Nullable
	private Expression<Number> amount;

	@Nullable
	public static Entity lastSpawned;

	@Nullable
	private Trigger trigger;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs,
						int matchedPattern,
						Kleenean isDelayed,
						ParseResult parseResult,
						@Nullable SectionNode sectionNode,
						@Nullable List<TriggerItem> triggerItems) {
		amount = matchedPattern == 0 ? null : (Expression<Number>) (exprs[0]);
		types = (Expression<EntityType>) exprs[matchedPattern];
		locations = Direction.combine((Expression<? extends Direction>) exprs[1 + matchedPattern], (Expression<? extends Location>) exprs[2 + matchedPattern]);

		if (types instanceof Literal<EntityType> et) {
			for (EntityType entityType : et.getArray()) {
				if (!entityType.isSpawnable()) {
					Skript.error("EntityType '" + Classes.toString(entityType) + "' cannot be spawned.");
					return false;
				}
			}
		}
		if (sectionNode != null) {
			AtomicBoolean delayed = new AtomicBoolean(false);
			Runnable afterLoading = () -> delayed.set(!getParser().getHasDelayBefore().isFalse());
			trigger = loadCode(sectionNode, "spawn", afterLoading, SpawnEvent.class);
			if (delayed.get()) {
				Skript.error("Delays can't be used within a Spawn Effect Section");
				return false;
			}
		}

		return true;
	}

	@Override
	@Nullable
	@SuppressWarnings({"unchecked", "rawtypes"})
	protected TriggerItem walk(Event event) {
		lastSpawned = null;

		Consumer<? extends Entity> consumer = getConsumer(event);

		Number numberAmount = amount != null ? amount.getSingle(event) : 1;
		if (numberAmount != null) {
			double amount = numberAmount.doubleValue();
			EntityType[] types = this.types.getArray(event);
			for (Location location : locations.getArray(event)) {
				World world = location.getWorld();
				if (world == null) continue;

				for (EntityType type : types) {
					if (!EntityUtils.canSpawn(type, world)) continue;

					Class<? extends Entity> entityClass = type.getEntityClass();
					if (entityClass == null) continue;

					for (int i = 0; i < amount; i++) {
						if (consumer != null) {
							lastSpawned = world.spawn(location, entityClass, (Consumer) consumer);
						} else {
							lastSpawned = world.spawn(location, entityClass, entity -> {
								if (entity instanceof FallingBlock block) {
									block.setBlockData(DEFAULT_DATA);
								}
							});
						}
					}
				}
			}
		}

		return super.walk(event, false);
	}

	private @Nullable Consumer<? extends Entity> getConsumer(Event event) {
		Consumer<? extends Entity> consumer;
		if (trigger != null) {
			consumer = entity -> {
				lastSpawned = entity;
				if (entity instanceof FallingBlock fallingBlock) {
					fallingBlock.setBlockData(DEFAULT_DATA);
				}
				SpawnEvent spawnEvent = new SpawnEvent(entity);
				// Copy the local variables from the calling code to this section
				Variables.setLocalVariables(spawnEvent, Variables.copyLocalVariables(event));
				TriggerItem.walk(trigger, spawnEvent);
				// And copy our (possibly modified) local variables back to the calling code
				Variables.setLocalVariables(event, Variables.copyLocalVariables(spawnEvent));
				// Clear spawnEvent's local variables as it won't be done automatically
				Variables.removeLocals(spawnEvent);
			};
		} else {
			consumer = null;
		}
		return consumer;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "spawn " + (amount != null ? amount.toString(event, debug) + " of " : "") +
			types.toString(event, debug) + " " + locations.toString(event, debug);
	}

}
