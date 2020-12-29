package me.dzikimlecz.discordchess.event;

import me.dzikimlecz.discordchess.config.Gettable;
import me.dzikimlecz.discordchess.config.Loggable;
import me.dzikimlecz.discordchess.command.CommandManager;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class AbstractEventListener extends ListenerAdapter {
	
	protected final CommandManager manager;
	protected final Gettable<String> config;
	protected final Loggable logs;
	
	public AbstractEventListener(Gettable<String> config, Loggable logs) {
		manager = new CommandManager(config);
		this.config = config;
		this.logs = logs;
	}
	
}
