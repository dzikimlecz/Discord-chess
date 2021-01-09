package me.dzikimlecz.discordchess.event.commands;

import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.util.CommandContext;
import me.dzikimlecz.discordchess.util.CommandHelpData;

import java.util.List;

public abstract class AbstractCommand {

	protected final IConfig<String> config;
	protected final ILogs logs;
	protected final CommandHelpData help;

	public AbstractCommand(IConfig<String> config, ILogs logs) {
		this.config = config;
		this.logs = logs;
		help = new CommandHelpData(getName(), getAliases(), "");
	}

	public abstract void handle(CommandContext context);
	
	public abstract String getName();
	
	public CommandHelpData getHelp() {
		return help;
	}
	
	public List<String> getAliases() {
		return List.of();
	}
	
}
