package me.dzikimlecz.discordchess.command;

import java.util.List;

public abstract class AbstractCommand {
	
	protected abstract void handle(CommandContext context);
	
	public abstract String getName();
	
	public abstract List<String> getHelp();
	
	protected List<String> getAliases() {
		return List.of();
	}
	
}
