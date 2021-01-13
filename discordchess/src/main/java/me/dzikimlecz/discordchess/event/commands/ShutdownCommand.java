package me.dzikimlecz.discordchess.event.commands;

import me.duncte123.botcommons.BotCommons;
import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.util.CommandContext;

import java.text.MessageFormat;
import java.util.List;

public class ShutdownCommand extends AbstractCommand {

	public ShutdownCommand(IConfig<String> config, ILogs logs) {
		super("shutdown", List.of("off"), config, logs);
		help.setCmdInfo("Command permitted only for bot developers. Shuts it down on all servers.");
		help.setUsage(MessageFormat.format("{0}{1}", config.get("prefix"), name()));
	}

	@Override
	public void handle(CommandContext context) {
		var author = context.getAuthor();
		if (!author.getId().equals(config.get("owner id"))) {
			context.getChannel().sendMessage("You don't have permission to do this").queue();
			return;
		}
		logs.write("Shutting down.", ShutdownCommand.class);
		var jda = context.getJDA();
		jda.shutdown();
		BotCommons.shutdown(jda);
	}

}
