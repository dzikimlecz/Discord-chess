package me.dzikimlecz.discordchess.event.commands;

import me.duncte123.botcommons.BotCommons;
import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.util.CommandContext;
import me.dzikimlecz.discordchess.util.CommandHelpData;

import java.util.List;

public class ShutdownCommand extends AbstractCommand {

	public ShutdownCommand(IConfig<String> config, ILogs logs) {
		super(config, logs);
		help.setUsage("Command permitted only for bot developers. Shuts it down on all servers.");
	}

	@Override
	public void handle(CommandContext context) {
		var author = context.getAuthor();
		if (author.getId().equals(config.get("owner id"))) BotCommons.shutdown();
		else context.getChannel().sendMessage("You don't have permission to do this").queue();
	}

	@Override
	public String getName() {
		return "shutdown";
	}

	@Override
	public List<String> getAliases() {
		return List.of("off");
	}
}
