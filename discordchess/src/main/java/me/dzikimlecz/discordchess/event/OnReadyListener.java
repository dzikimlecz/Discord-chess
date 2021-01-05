package me.dzikimlecz.discordchess.event;

import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import net.dv8tion.jda.api.events.ReadyEvent;
import org.jetbrains.annotations.NotNull;

public class OnReadyListener extends AbstractEventListener {
	
	public OnReadyListener(IConfig<String> config, ILogs logs) {
		super(config, logs);
	}
	

	public void onReady(@NotNull ReadyEvent event) {
		logs.write( "{} is ready.", event.getJDA().getSelfUser().getAsTag());
	}
}
