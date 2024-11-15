package ch.njol.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockGrowEvent;
import org.jetbrains.annotations.Nullable;


public class EvtPlantGrowth extends SkriptEvent {
	static {
		Skript.registerEvent("Block Growth", EvtPlantGrowth.class, BlockGrowEvent.class,
				"(plant|crop|block) grow[(th|ing)] [[of] %-materials%]")
			.description("Called when a crop grows. Alternative to new form of generic grow event.")
			.examples("on crop growth:")
			.since("2.2-Fixes-V10");
	}

	@Nullable
	private Literal<Material> types;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
		types = (Literal<Material>) args[0];

		return true;
	}

	@Override
	public boolean check(Event e) {
		if (!(e instanceof BlockGrowEvent blockGrowEvent)) return false;

		if (types != null) {
			for (Material type : types.getAll()) {
				if (type == blockGrowEvent.getBlock().getType())
					return true;
			}
			return false; // Not one of given types
		}

		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "plant growth";
	}
}
