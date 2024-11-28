package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

@Name("Drop")
@Description("Drops one or more items.")
@Examples({"on death of creeper:",
	"\tdrop tnt"})
@Since("1.0")
public class EffDrop extends Effect {
//TODO this class needs some love
	static {
		Skript.registerEffect(EffDrop.class,
			"drop %materials/itemstacks% [%directions% %locations%] [(1¦without velocity)]");
	}

	@Nullable
	public static Entity lastSpawned = null;

	@SuppressWarnings("NotNullFieldNotInitialized")
	private Expression<?> drops;
	@SuppressWarnings("NotNullFieldNotInitialized")
	private Expression<Location> locations;

	private boolean useVelocity;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		drops = exprs[0];
		locations = Direction.combine((Expression<? extends Direction>) exprs[1], (Expression<? extends Location>) exprs[2]);
		useVelocity = parseResult.mark == 0;
		return true;
	}

	@Override
	public void execute(Event e) {
		for (Location location : locations.getArray(e)) {
			Location itemDropLoc = location.clone().subtract(0.5, 0.5, 0.5); // dropItemNaturally adds 0.15 to 0.85 randomly to all coordinates
			for (Object object : this.drops.getArray(e)) {
				if (object instanceof ItemStack itemStack) {
					dropItemstack(itemStack, location, itemDropLoc);
				} else if (object instanceof Material material) {
					dropItemstack(new ItemStack(material), location, itemDropLoc);
				}
			}
		}
	}

	private void dropItemstack(ItemStack itemStack, Location location, Location dropLocation) {
		if (!itemStack.getType().isAir() && itemStack.getAmount() > 0) {
			if (useVelocity) {
				lastSpawned = location.getWorld().dropItemNaturally(dropLocation, itemStack);
			} else {
				Item item = location.getWorld().dropItem(location, itemStack);
				item.teleport(location);
				item.setVelocity(new Vector(0, 0, 0));
				lastSpawned = item;
			}
		}
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "drop " + drops.toString(e, debug) + " " + locations.toString(e, debug);
	}

}
