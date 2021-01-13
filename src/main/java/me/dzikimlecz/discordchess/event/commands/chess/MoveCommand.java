package me.dzikimlecz.discordchess.event.commands.chess;

import me.dzikimlecz.chessapi.GameInfo;
import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.game.ChessGameManager;
import me.dzikimlecz.discordchess.util.CommandContext;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MoveCommand extends ChessCommand {

	public MoveCommand(IConfig<String> config, ILogs logs, ChessGameManager manager) {
		super("move", List.of("mv", "m"), config, logs, manager);
	}

	@Override
	public void handle(CommandContext context) {
		var args = context.getArgs();
		var channel = context.getChannel();
		var author = context.getAuthor();

		if (args.size() < 1) {
			sendUsage(channel);
			return;
		}

		var playerCheckStatus = checkPlayer(channel, author);
		if (playerCheckStatus != null) {
			channel.sendMessage(playerCheckStatus).queue();
			return;
		}

		clearDrawRequests(channel);

		var notation = args.get(0);
		try {
			gamesManager.move(channel, notation);
		} catch(IllegalArgumentException e) {
			channel.sendMessage("This is not a correct move!").queue();
		}
	}

	private void clearDrawRequests(TextChannel channel) {
		var listener = gamesManager.getListener(channel);
		if (listener.drawRequester() != null) listener.replyToDraw(false);
	}

	@Nullable
	private String checkPlayer(TextChannel channel, User author) {
		GameInfo<TextChannel, User> info;
		try {
			info = gamesManager.getInfo(channel);
		} catch(IllegalStateException e) {
			return "There isn't any game ongoing on this channel";
		}
		var turnColor = gamesManager.getTurn(channel);
		var appropriatePlayer = info.getPlayer(turnColor);
		if (!appropriatePlayer.equals(author)) {
			return info.getPlayer(turnColor.opposite()).equals(author) ?
					"That's not your turn!" : "You aren't in any game on this channel!";
		}
		return null;
	}
}
