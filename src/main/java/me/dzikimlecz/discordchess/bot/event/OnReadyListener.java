package me.dzikimlecz.discordchess.bot.event;

import me.dzikimlecz.discordchess.bot.config.Gettable;
import me.dzikimlecz.discordchess.bot.config.Loggable;
import net.dv8tion.jda.api.events.ReadyEvent;
import org.jetbrains.annotations.NotNull;

public class OnReadyListener extends AbstractEventListener {
	
	public OnReadyListener(Gettable<String> config, Loggable logs) {
		super(config, logs);
	}
	
	@Override
	public void onReady(@NotNull ReadyEvent event) {
		logs.write(AbstractEventListener.class, "{} is ready.",
		           event.getJDA().getSelfUser().getAsTag());
	}
}
