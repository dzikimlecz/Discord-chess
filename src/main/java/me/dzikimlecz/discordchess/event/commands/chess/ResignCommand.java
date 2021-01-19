package me.dzikimlecz.discordchess.event.commands.chess;

import me.dzikimlecz.chessapi.game.board.Color;
import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.game.ChessGameManager;
import me.dzikimlecz.discordchess.util.CommandContext;

import java.util.List;

public class ResignCommand extends ChessCommand {
	public ResignCommand(IConfig<String> config, ILogs logs, ChessGameManager manager) {
		super("resign", List.of(), config, logs, manager);
	}

	@Override
	public void handle(CommandContext context) {
		var channel = context.getChannel();
		var author = context.getAuthor();
		var info = gamesManager.getInfo(channel);
		var userOptional =
				List.of(info.getPlayer(Color.WHITE), info.getPlayer(Color.BLACK)).stream().filter(
						author::equals).findFirst();
		if (userOptional.isEmpty()) {
			channel.sendMessage("You aren't in any game on this channel").queue();
			return;
		}
		gamesManager.forceClose(channel);
		var loser = userOptional.get();
		channel.sendMessage(loser.getAsMention() + " resigned!").queue();
		var winnerColor = info.getPlayer(Color.WHITE).equals(loser) ? Color.BLACK :Color.WHITE;
		info.setWinner(winnerColor);
		var winner = info.getWinner();
		assert winner != null;
		channel.sendMessage("Winner: " + winner.getAsMention()).queue();
	}
}
