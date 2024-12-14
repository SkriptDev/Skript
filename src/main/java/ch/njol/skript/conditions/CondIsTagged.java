package ch.njol.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.TagUtils;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Checker;
import ch.njol.util.Kleenean;
import io.papermc.paper.registry.tag.Tag;
import org.bukkit.Keyed;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Name("Is Tagged")
@Description("Check if an object is tagged as a specific Minecraft tag.")
@Examples({"if type of player's tool is tagged as item registry tag \"minecraft:axes\":",
	"if target entity is tagged as entity type registry tag \"minecraft:arthropod\":",
	"if player's tool is not tagged as item registry tag \"minecraft:buttons\":"})
@RequiredPlugins("Minecraft 1.21+")
@Since("INSERT VERSION")
@SuppressWarnings({"rawtypes", "UnstableApiUsage"})
public class CondIsTagged extends Condition {

	static {
		if (TagUtils.HAS_TAG) {
			Skript.registerCondition(CondIsTagged.class,
				"%objects% (is|are) tagged as %tags%",
				"%objects% (is|are) not tagged as %tags%",
				"%objects% (is|are)n't tagged as %tags%");
		}
	}

	private Expression<?> objects;
	private Expression<Tag> tag;

	@SuppressWarnings({"unchecked", "UnstableApiUsage"})
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		this.objects = LiteralUtils.defendExpression(exprs[0]);
		this.tag = (Expression<Tag>) exprs[1];
		setNegated(matchedPattern > 0);
		return true;
	}

	@Override
	public boolean check(Event event) {
		return this.objects.check(event, (Checker<Object>) object -> CondIsTagged.this.tag.check(event, tag -> {
			Keyed keyed = null;
			if (object instanceof Keyed k) keyed = k;
			// Convert some objects which won't directly get checked for tagged
			else if (object instanceof Entity entity) keyed = entity.getType();
			else if (object instanceof ItemStack itemStack) keyed = itemStack.getType();
			if (keyed != null) {
				return TagUtils.isTagged(keyed, tag);
			}
			return false;
		}), isNegated());
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		String plural = this.objects.isSingle() ? " is" : " are";
		String neg = isNegated() ? " not" : "";
		return this.objects.toString(event, debug) + plural + neg + " tagged as " + this.tag.toString(event, debug);
	}

}
