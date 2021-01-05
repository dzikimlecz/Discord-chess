package me.dzikimlecz.discordchess.event;

import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class MessagesListener extends AbstractEventListener {
	
	public MessagesListener(IConfig<String> config, ILogs logs) {
		super(config, logs);
	}

	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
		String rawMessage = event.getMessage().getContentRaw();
		if (rawMessage.startsWith(config.get("prefix")))
			manager.handle(event);
		else if (rawMessage.toLowerCase().contains("szachy")) {

		}
	}
}
