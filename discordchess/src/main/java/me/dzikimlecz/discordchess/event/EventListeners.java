package me.dzikimlecz.discordchess.event;

import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.event.listeners.MessagesListener;
import me.dzikimlecz.discordchess.event.listeners.OnReadyListener;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class EventListeners extends ListenerAdapter {

	private final OnReadyListener onReadyListener;
	private final MessagesListener messagesListener;
	private final CommandManager manager;
	
	public EventListeners(IConfig<String> config, ILogs logs) {
		manager = new CommandManager(config, logs);
		onReadyListener = new OnReadyListener(config, logs, manager);
		messagesListener = new MessagesListener(config, logs, manager);
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
