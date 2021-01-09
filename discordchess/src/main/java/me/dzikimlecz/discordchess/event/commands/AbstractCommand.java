package me.dzikimlecz.discordchess.event.commands;

import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.util.CommandContext;
import me.dzikimlecz.discordchess.util.CommandHelpData;

import java.util.List;

public abstract class AbstractCommand {

	protected String name;
	protected List<String> aliases;
	protected final IConfig<String> config;
	protected final ILogs logs;
	protected final CommandHelpData help;

	public AbstractCommand(IConfig<String> config, ILogs logs) {
		this.config = config;
		this.logs = logs;
		help = new CommandHelpData(name(), aliases(), "");
	}

	public abstract void handle(CommandContext context);
	
	public String name() {
		return name;
	}
	
	public CommandHelpData help() {
		return help;
	}
	
	public List<String> aliases() {
		return List.of();
	}
	
}
