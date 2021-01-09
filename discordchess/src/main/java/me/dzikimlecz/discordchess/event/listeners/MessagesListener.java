package me.dzikimlecz.discordchess.event.listeners;

import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.event.CommandManager;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class MessagesListener extends AbstractEventListener {
	
	public MessagesListener(IConfig<String> config, ILogs logs, CommandManager manager) {
		super(config, logs, manager);
	}

	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
		String rawMessage = event.getMessage().getContentRaw();
		if (rawMessage.startsWith(config.get("prefix")))
			commandManager.handle(event);
		else if (rawMessage.toLowerCase().contains("szachy")) {

		}
	}
}
