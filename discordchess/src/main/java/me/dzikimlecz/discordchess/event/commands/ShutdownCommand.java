package me.dzikimlecz.discordchess.event.commands;

import me.dzikimlecz.discordchess.util.CommandContext;

import java.util.List;

public class ShutdownCommand extends AbstractCommand {
	@Override
	public void handle(CommandContext context) {

	}

	@Override
	public String getName() {
		return "shutdown";
	}

	@Override
	public List<String> getAliases() {
		return List.of("off");
	}

	@Override
	public List<String> getHelp() {
		return null;
	}
}
