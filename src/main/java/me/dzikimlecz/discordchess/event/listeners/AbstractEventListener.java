package me.dzikimlecz.discordchess.event.listeners;

import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.event.CommandManager;

public abstract class AbstractEventListener {
	
	protected final IConfig<String> config;
	protected final ILogs logs;
	protected final CommandManager commandManager;
	
	public AbstractEventListener(IConfig<String> config, ILogs logs, CommandManager manager) {
		this.config = config;
		this.logs = logs;
		this.commandManager = manager;
	}
	
}
