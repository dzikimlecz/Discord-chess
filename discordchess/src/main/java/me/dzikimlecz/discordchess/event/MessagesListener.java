package me.dzikimlecz.discordchess.event;

import me.dzikimlecz.discordchess.config.Gettable;
import me.dzikimlecz.discordchess.config.Loggable;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class MessagesListener extends AbstractEventListener {
	
	public MessagesListener(Gettable<String> config, Loggable logs) {
		super(config, logs);
	}
	
	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
		if (event.getAuthor().isBot() || event.isWebhookMessage())
			return;
		String rawMessage = event.getMessage().getContentRaw();
		if (rawMessage.startsWith(config.get("prefix")))
			manager.handle(event);
	}
}
