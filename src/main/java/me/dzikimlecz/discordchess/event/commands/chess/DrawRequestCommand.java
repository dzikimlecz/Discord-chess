package me.dzikimlecz.discordchess.event.commands.chess;

import me.dzikimlecz.chessapi.game.board.Color;
import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.game.ChessGameManager;
import me.dzikimlecz.discordchess.util.CommandContext;

import java.text.MessageFormat;
import java.util.List;

public class DrawRequestCommand extends ChessCommand {
	public DrawRequestCommand(IConfig<String> config, ILogs logs, ChessGameManager manager) {
		super("drawreq", List.of("reqdraw", "rd", "dr"), config, logs, manager);
		help.setCmdInfo("Command used for requesting a draw during a chess game");
		help.setUsage(MessageFormat.format("{0}{1}", config.get("prefix"), name()));
	}

	@Override
	public void handle(CommandContext context) {
		var channel = context.getChannel();
		var author = context.getAuthor();
		var info = gamesManager.getInfo(channel);
		for (Color color : Color.values()) {
			if (info.getPlayer(color).equals(author)) {
				gamesManager.requestDraw(channel, color);
				return;
			}
		}
		channel.sendMessage("You aren't in any game on this channel!").queue();
	}
}
