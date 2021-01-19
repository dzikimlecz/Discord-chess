package me.dzikimlecz.discordchess.event.commands.chess;

import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.game.ChessGameManager;
import me.dzikimlecz.discordchess.util.CommandContext;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.text.MessageFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameForceStopCommand extends ChessCommand {



	public GameForceStopCommand(IConfig<String> config, ILogs logs, ChessGameManager manager) {
		super("gamestop", List.of("gs"), config, logs, manager);
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
		if (!checkPermissions(member)) {
			channel.sendMessage("You don't have permission to do this!").queue();
			return;
		}
		var args = context.getArgs();
		if (args.isEmpty()) closeOnTimeOut(channel, "0");
		else if (args.get(0).equals("-w"))
			try {
				closeOnTimeOut(channel, args.get(1));
			} catch(IndexOutOfBoundsException | NumberFormatException e) {
				sendUsage(channel);
			}
		else if (args.get(0).startsWith("-"))
			sendUsage(channel);
		else closeOnTimeOut(channel, "0");
	}

	private void closeOnTimeOut(TextChannel channel, String arg) {
		var timeout = Double.parseDouble(arg.replace(',', '.'));
		new Timer().schedule(new TimerTask() {
			public void run() {
				gamesManager.forceClose(channel);
			}
		}, (long) (timeout * 1E3));
	}

	private boolean checkPermissions(Member member) {
		return member.isOwner()
				|| member.hasPermission(Permission.MANAGE_SERVER)
				|| member.getId().equals(config.get("owner id"));
	}
}
