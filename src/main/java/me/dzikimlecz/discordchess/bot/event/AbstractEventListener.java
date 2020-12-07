package me.dzikimlecz.discordchess.bot.event;

import me.dzikimlecz.discordchess.bot.config.Gettable;
import me.dzikimlecz.discordchess.bot.config.Loggable;
import me.dzikimlecz.discordchess.bot.command.CommandManager;
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
