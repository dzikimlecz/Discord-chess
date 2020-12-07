package me.dzikimlecz.discordchess.bot.event;

import me.dzikimlecz.discordchess.bot.config.Gettable;
import me.dzikimlecz.discordchess.bot.config.Loggable;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class EventListeners extends ListenerAdapter {
	private final Gettable<String> config;
	private final Loggable logs;
	
	private OnReadyListener onReadyListener;
	private MessagesListener messagesListener;
	
	public EventListeners(Gettable<String> config, Loggable logs) {
		this.config = config;
		this.logs = logs;
		onReadyListener = new OnReadyListener(config, logs);
		messagesListener = new MessagesListener(config, logs);
	}
	
	@Override
	public void onReady(@NotNull ReadyEvent event) {
		onReadyListener.onReady(event);
	}
	
	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
		messagesListener.onGuildMessageReceived(event);
	}
	
	
}
