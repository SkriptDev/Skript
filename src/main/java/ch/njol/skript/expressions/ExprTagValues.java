package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.TagUtils;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import io.papermc.paper.registry.tag.Tag;
import org.bukkit.Keyed;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("Tag Values")
@Description({"Returns the values of a tag. This could be entity types, items/blocks (materials), enchantments, etc.",
	"Tags and their values are created by Minecraft and DataPacks, and the values cannot be edited via Skript."})
@Examples({"loop tag values of block registry tag \"minecraft:all_signs\":",
	"if tag values of item registry tag \"minecraft:banners\" contains material of player's tool:",
	"if tag values of entity type registry tag \"arthropod\" contains type of target entity:"})
@RequiredPlugins("Minecraft 1.21+")
@Since("INSERT VERSION")
@SuppressWarnings({"rawtypes", "UnstableApiUsage"})
public class ExprTagValues extends SimpleExpression<Object> {

	static {
		if (TagUtils.HAS_TAG) {
			Skript.registerExpression(ExprTagValues.class, Object.class, ExpressionType.PROPERTY,
				"tag values of %tag%", "%tag%'[s] tag values");
		}
	}

	private Expression<Tag> tag;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		this.tag = (Expression<Tag>) exprs[0];
		return true;
	}

	@Override
	protected @Nullable Object[] get(Event event) {
		Tag<?> tag = this.tag.getSingle(event);
		if (tag == null) return null;

		List<Keyed> tagValues = TagUtils.getTagValues(tag);
		return tagValues.toArray(new Keyed[0]);
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<?> getReturnType() {
		return Object.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "tag values of " + this.tag.toString(event, debug);
	}

}
