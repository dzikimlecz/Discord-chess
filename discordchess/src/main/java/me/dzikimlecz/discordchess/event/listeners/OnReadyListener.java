package me.dzikimlecz.discordchess.event.listeners;

import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.event.CommandManager;
import net.dv8tion.jda.api.events.ReadyEvent;
import org.jetbrains.annotations.NotNull;

public class OnReadyListener extends AbstractEventListener {
	
	public OnReadyListener(IConfig<String> config, ILogs logs, CommandManager manager) {
		super(config, logs, manager);
	}
	

	public void onReady(@NotNull ReadyEvent event) {
		logs.write( "{} is ready.", event.getJDA().getSelfUser().getAsTag());
	}
}
