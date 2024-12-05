package ch.njol.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Multiple Conditions")
@Description({"Check multiple conditions in one line.",
	"The first pattern will match if both conditions are true.",
	"The second pattern will match if either of the conditions are true."})
@Examples({"if (type of player's tool = diamond sword) and (name of player's tool = \"Mr Sword\"):",
	"if (block below player is dirt) && (block at player is air):",
	"if (block above player is dirt) or (block below player is dirt):",
	"if (attacker is a zombie) || (victim is a sheep):",
	"",
	"# More than 2",
	"if ((\"a\" = \"a\") and (\"b\" = \"b\")) and (\"c\" = \"c\"):",
	"if ((\"a\" = \"a\") and (\"b\" = \"b\")) or (\"c\" = \"c\"):"})
@Since("INSERT VERSION")
public class CondMultiple extends Condition {

	static {
		Skript.registerCondition(CondMultiple.class,
			"\\(<.+>\\) (and|&&) \\(<.+>\\)", "\\(<.+>\\) (or|\\|\\|) \\(<.+>\\)");
	}

	private Condition condition1;
	private Condition condition2;
	private boolean and;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		this.condition1 = Condition.parse(parseResult.regexes.get(0).group(), null);
		this.condition2 = Condition.parse(parseResult.regexes.get(1).group(), null);
		this.and = matchedPattern == 0;
		return this.condition1 != null && this.condition2 != null;
	}

	@Override
	public boolean check(Event event) {
		if (this.and) {
			return this.condition1.check(event) && this.condition2.check(event);
		}
		return this.condition1.check(event) || this.condition2.check(event);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		String cond1 = this.condition1.toString(event, debug);
		String cond2 = this.condition2.toString(event, debug);
		String type = this.and ? "and" : "or";
		return String.format("(%s) %s (%s)", cond1, type, cond2);
	}

}
