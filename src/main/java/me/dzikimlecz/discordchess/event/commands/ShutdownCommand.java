package me.dzikimlecz.discordchess.event.commands;

import me.duncte123.botcommons.BotCommons;
import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.util.CommandContext;
import net.dv8tion.jda.api.JDA;

import java.text.MessageFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ShutdownCommand extends AbstractCommand {

	public ShutdownCommand(IConfig<String> config, ILogs logs) {
		super("shutdown", List.of("off"), config, logs);
		help.setCmdInfo("Command permitted only for bot developers. Shuts it down on all servers.");
		help.setUsage(MessageFormat.format("{0}{1}", config.get("prefix"), name()));
	}

	@Override
	public void handle(CommandContext context) {
		var author = context.getAuthor();
		var channel = context.getChannel();
		if (!author.getId().equals(config.get("owner id"))) {
			channel.sendMessage("You don't have permission to do this").queue();
			return;
		}
		var jda = context.getJDA();
		var args = context.getArgs();
		if (args.isEmpty()) closeOnTimeOut(jda,"0");
		else if (args.get(0).equals("-w"))
			try {
				closeOnTimeOut(jda, args.get(1));
			} catch(IndexOutOfBoundsException | NumberFormatException e) {
				sendUsage(channel);
			}
		else if (args.get(0).startsWith("-"))
			sendUsage(channel);
		else closeOnTimeOut(jda, "0");
	}

	private void closeOnTimeOut(JDA jda, String arg) {
		var timeout = Double.parseDouble(arg.replace(',', '.'));
		var msg = new StringBuilder("Shutting down");
		if (timeout != 0)
			msg.append("in ").append(timeout).append("s");
		logs.write(msg.toString(), ShutdownCommand.class);
		new Timer().schedule(new TimerTask() {
			public void run() {
				BotCommons.shutdown(jda);
				jda.shutdownNow();
			}
		}, (long) (timeout * 1E3));
	}
}
