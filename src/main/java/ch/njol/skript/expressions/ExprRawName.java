package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Raw Name")
@Description({"The raw Minecraft material name of the given item.",
	"Note that this is not guaranteed to give same results on all servers."})
@Examples("raw name of tool of player")
@Since("unknown (2.2)")
public class ExprRawName extends SimpleExpression<String> {

	static {
		Skript.registerExpression(ExprRawName.class, String.class, ExpressionType.SIMPLE,
			"(minecraft|vanilla) name[s] of %materials/itemstacks%");
	}

	@SuppressWarnings("null")
	private Expression<?> types;

	@SuppressWarnings({"null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		this.types = exprs[0];
		return true;
	}

	@Override
	@Nullable
	protected String[] get(final Event event) {
		List<String> names = new ArrayList<>();
		for (Object object : this.types.getArray(event)) {
			if (object instanceof ItemStack itemStack) {
				names.add(itemStack.getType().getKey().toString());
			} else if (object instanceof Material material) {
				names.add(material.getKey().toString());
			}
		}
		return names.toArray(new String[0]);
	}

	@Override
	public boolean isSingle() {
		return types.isSingle();
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@SuppressWarnings("null")
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "minecraft name of " + types.toString(e, debug);
	}

}
