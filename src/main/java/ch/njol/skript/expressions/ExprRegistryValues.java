package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.RegistryUtils;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Registry Values")
@Description({"Returns a list of all values that belong to a registry.",
	"Registries cannot be changed via Skript."})
@Examples({"set {_values::*} to registry values of item registry",
	"loop registry values of biome registry:"})
@Since("INSERT VERSION")
public class ExprRegistryValues extends SimpleExpression<Object> {

	static {
		Skript.registerExpression(ExprRegistryValues.class, Object.class, ExpressionType.PROPERTY,
			"registry values of %registrykey%");
	}

	private Expression<RegistryKey<Keyed>> registryKey;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		this.registryKey = (Expression<RegistryKey<Keyed>>) exprs[0];
		return true;
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	protected @Nullable Object[] get(Event event) {
		List<Keyed> objects = new ArrayList<>();
		RegistryKey<Keyed> registryKey = this.registryKey.getSingle(event);
		if (registryKey == null) return null;

		Registry<Keyed> registry = RegistryAccess.registryAccess().getRegistry(registryKey);
		for (Keyed keyed : registry) {
			if (keyed instanceof BlockType blockType)
				//noinspection deprecation
				objects.add(blockType.asMaterial());
			else if (keyed instanceof ItemType itemType)
				//noinspection deprecation
				objects.add(itemType.asMaterial());
			else objects.add(keyed);
		}
		return objects.toArray();
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<?> getReturnType() {
		if (this.registryKey instanceof Literal<RegistryKey<Keyed>> literal) {
			return RegistryUtils.getRegistryClass(literal.getSingle());
		}
		return Object.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "registry values of " + this.registryKey.toString(event, debug);
	}

}
