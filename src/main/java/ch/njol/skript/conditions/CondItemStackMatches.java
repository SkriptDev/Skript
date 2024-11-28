package ch.njol.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import io.papermc.paper.datacomponent.DataComponentType;
import org.bukkit.Registry;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@Name("ItemStack Match")
@Description({"A condition to check if an ItemStack matches another ItemStack.",
	"This will ignore stack count.",
	"Optionally you can do a partial match, this will only check the data components on the second ItemStack " +
		"while ignoring other data components."})
@Examples({"set {_i} to itemstack of diamond sword named \"Le Name\" with lore \"Some Lore\"",
	"if {_i} matches itemstack of diamond sword: # This will fail as the items don't match",
	"if {_i} partially matches itemstack of diamond sword: # This will pass as only the components of the second item are checked.",
	"if player's tool matches {_i}:"})
@RequiredPlugins("Paper 1.21.3+ for Partial Matching")
@Since("INSERT VERSION")
public class CondItemStackMatches extends Condition {

	private static final boolean HAS_DATA_COMP = Skript.classExists("io.papermc.paper.datacomponent.DataComponentType");

	static {
		Skript.registerCondition(CondItemStackMatches.class,
			"%itemstacks% [:partially] match[es] %itemstack%",
			"%itemstacks% (is|are) [:partially] simlar to %itemstack%",
			"%itemstacks% (does not|do not|doesn't|don't) [:partially] match %itemstack%",
			"%itemstacks% ((is|are) not|isn't|aren't) [:partially] simlar to %itemstack%");
	}

	private boolean partial;
	private Expression<ItemStack> itemsToCheck;
	private Expression<ItemStack> itemToMatch;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		this.partial = parseResult.hasTag("partially");
		if (this.partial && !HAS_DATA_COMP) {
			Skript.error("partial matching require Paper 1.21.3+");
			return false;
		}
		this.itemsToCheck = (Expression<ItemStack>) exprs[0];
		this.itemToMatch = (Expression<ItemStack>) exprs[1];
		setNegated(matchedPattern > 1);
		return true;
	}

	@Override
	public boolean check(Event event) {
		return this.itemsToCheck.check(event, toCheck ->
			CondItemStackMatches.this.itemToMatch.check(event, toMatch -> {
				if (CondItemStackMatches.this.partial)
					return partiallyMatch(toCheck, toMatch);
				return toCheck.isSimilar(toMatch);
			}, isNegated()));
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		String match = isNegated() ? " does not match " : " matches ";
		return this.itemsToCheck.toString(event, debug) + match + this.itemToMatch.toString(event, debug);
	}

	@SuppressWarnings("UnstableApiUsage")
	private boolean partiallyMatch(ItemStack toCheck, ItemStack toMatch) {
		Set<DataComponentType> comps = new HashSet<>();
		for (DataComponentType dataType : Registry.DATA_COMPONENT_TYPE) {
			if (!toMatch.hasData(dataType) || !toMatch.isDataOverridden(dataType)) {
				comps.add(dataType);
			}
		}
		return toCheck.matchesWithoutData(toMatch, comps, true);
	}

}
