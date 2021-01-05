package me.dzikimlecz.discordchess.event;

import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class EventListeners extends ListenerAdapter {

	private final OnReadyListener onReadyListener;
	private final MessagesListener messagesListener;
	
	public EventListeners(IConfig<String> config, ILogs logs) {
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
