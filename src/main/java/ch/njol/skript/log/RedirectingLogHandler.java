package ch.njol.skript.log;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

/**
 * Redirects the log to a {@link CommandSender}.
 */
public class RedirectingLogHandler extends LogHandler {

	private final CommandSender recipient;
	
	private final String prefix;
	
	private int numErrors = 0;
	
	public RedirectingLogHandler(CommandSender recipient, @Nullable String prefix) {
		this.recipient = recipient;
		this.prefix = prefix == null ? "" : prefix;
	}
	
	@Override
	public LogResult log(LogEntry entry) {
		SkriptLogger.sendFormatted(recipient, prefix + entry.toFormattedString());
		if (entry.level == Level.SEVERE)
			numErrors++;
		return LogResult.DO_NOT_LOG;
	}
	
	@Override
	public RedirectingLogHandler start() {
		return SkriptLogger.startLogHandler(this);
	}
	
	public int numErrors() {
		return numErrors;
	}

}
