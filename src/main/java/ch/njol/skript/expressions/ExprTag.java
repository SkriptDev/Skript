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
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.Tag;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Tag")
@Description({"Represents a Minecraft tag.",
	"See <a href='https://minecraft.wiki/w/Tag'>McWiki Tag</a> for more information on tags.",
	"`registrykey` = Refers to the type of registry this tag is from.",
	"`string` = You can use the `minecraft:` namespace and/or namespaces from DataPacks. " +
		"If you omit this value it defaults to 'minecraft'."})
@Examples({"set {_tag} to item registry tag \"minecraft:axes\"",
	"set {_tag} to block registry tag \"mineable/pickaxe\"",
	"set {_tag} to block reigstry tag \"my_pack:some_item_tag\"",
	"set {_tag} to enchantment registry tag \"treasure\""})
@RequiredPlugins("Minecraft 1.21+")
@Since("INSERT VERSION")
@SuppressWarnings({"rawtypes", "UnstableApiUsage"})
public class ExprTag extends SimpleExpression<Tag> {

	static {
		if (TagUtils.HAS_TAG) {
			Skript.registerExpression(ExprTag.class, Tag.class, ExpressionType.COMBINED,
				"%registrykey% tag %string%");
		}
	}

	private Expression<RegistryKey> registryKey;
	private Expression<String> tag;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		this.registryKey = (Expression<RegistryKey>) exprs[0];
		this.tag = (Expression<String>) exprs[1];
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected @Nullable Tag[] get(Event event) {
		RegistryKey registryKey = this.registryKey.getSingle(event);
		String tag = this.tag.getSingle(event);
		if (registryKey == null || tag == null) return null;

		return new Tag[]{TagUtils.getTag(registryKey, tag)};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Tag> getReturnType() {
		return Tag.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return this.registryKey.toString(event, debug) + " tag '" + this.tag.toString(event, debug) + "'";
	}

}
