package me.dzikimlecz.discordchess.event.commands;

import me.dzikimlecz.discordchess.util.CommandContext;

import java.util.List;

public abstract class AbstractCommand {
	
	public abstract void handle(CommandContext context);
	
	public abstract String getName();
	
	public abstract List<String> getHelp();
	
	public List<String> getAliases() {
		return List.of();
	}
	
}
