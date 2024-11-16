package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@NoDoc
public class EffDebug extends Effect {

	private static final CommandSender CONSOLE = Bukkit.getConsoleSender();

	static {
		Skript.registerEffect(EffDebug.class, "debug %objects% [to %commandsender%]");
	}

	private Expression<?> objects;
	private Expression<CommandSender> receiver;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		this.objects = LiteralUtils.defendExpression(exprs[0]);
		this.receiver = (Expression<CommandSender>) exprs[1];
		return true;
	}

	@Override
	protected void execute(Event event) {
		CommandSender receiver = this.receiver != null ? this.receiver.getSingle(event) : null;
		if (receiver == null) {
			receiver = CONSOLE;
		}

		for (Object object : this.objects.getArray(event)) {
			String s = "Debug: " + Classes.toString(object) + " // " + object.getClass().getName();
			receiver.sendMessage(s);
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		String to = this.receiver != null ? " to " + this.receiver.toString(event, debug) : "";
		return "debug " + objects.toString(event, debug) + to;
	}

}
