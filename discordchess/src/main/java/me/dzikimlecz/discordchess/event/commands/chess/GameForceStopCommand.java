package me.dzikimlecz.discordchess.event.commands.chess;

import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.game.ChessGameManager;
import me.dzikimlecz.discordchess.util.CommandContext;
import net.dv8tion.jda.api.Permission;

import java.text.MessageFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameForceStopCommand extends ChessCommand {



	public GameForceStopCommand(IConfig<String> config, ILogs logs, ChessGameManager manager) {
		super(config, logs, manager);
		name = "gamestop";
		aliases = List.of("gs");
		help.setCmdInfo("Mod-only command. Forces end of the game on the channel. " +
				              "(options may be used to set timeout of closure)");
		help.setUsage(MessageFormat.format(
				"""
						{0}{1}
						Optional settings:
						-w <time in seconds> //sets timeout of the closure""",
				config.get("prefix"), name()
		));
	}

	@Override
	public void handle(CommandContext context) {
		var member = context.getMember();
		var channel = context.getChannel();
		boolean permissionGranted = member.isOwner()
				|| member.hasPermission(Permission.MANAGE_SERVER)
				|| member.getId().equals(config.get("owner id"));
		if (!permissionGranted) {
			channel.sendMessage("You don't have permission to do this!").queue();
			return;
		}
		var args = context.getArgs();
		if (args.size() <= 0) gamesManager.forceClose(channel);
		else if (args.get(0).equals("-w"))
			try {
				var arg1 = args.get(1).replace(',', '.');
				var timeout = Double.parseDouble(arg1);
				new Timer().schedule(new TimerTask() {
					public void run() {
						gamesManager.forceClose(channel);
					}
				}, (long) (timeout * 1E3));
			} catch(IndexOutOfBoundsException | NumberFormatException e) {
				channel.sendMessage("Usage: " + help.usage()).queue();
			}
		else if (args.get(0).startsWith("-"))
			channel.sendMessage("Usage: " + help.usage()).queue();
	}

}
