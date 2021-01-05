package me.dzikimlecz.discordchess.event;

import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.command.CommandManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class AbstractEventListener {
	
	protected final CommandManager manager;
	protected final IConfig<String> config;
	protected final ILogs logs;
	
	public AbstractEventListener(IConfig<String> config, ILogs logs) {
		manager = new CommandManager(config);
		this.config = config;
		this.logs = logs;
	}
	
}
