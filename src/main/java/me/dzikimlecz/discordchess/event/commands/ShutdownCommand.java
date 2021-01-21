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
		else closeOnTimeOut(jda, args.get(0));
	}

	private void closeOnTimeOut(JDA jda, String arg) {
		var timeout = Double.parseDouble(arg.replace(',', '.'));
		var msg = new StringBuilder("Shutting down");
		if (timeout != 0) {
			msg.append("in ").append(timeout).append("s");
			new Timer().schedule(new TimerTask() {
				public void run() {
					BotCommons.shutdown(jda);
					jda.shutdownNow();
				}
			}, (long) (timeout * 1E3));
		} else {
			logs.write(msg.toString(), ShutdownCommand.class);
			BotCommons.shutdown(jda);
			jda.shutdownNow();
		}
		var exitForSure = new Thread(() -> {
			try {
				Thread.sleep(4000);
			} catch (InterruptedException ignored) {}
			System.exit(0);
		});
		exitForSure.setDaemon(true);
		exitForSure.start();
	}
}
