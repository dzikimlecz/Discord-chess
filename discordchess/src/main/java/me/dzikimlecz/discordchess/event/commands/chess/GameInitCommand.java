package me.dzikimlecz.discordchess.event.commands.chess;

import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.game.ChessGameManager;
import me.dzikimlecz.discordchess.util.CommandContext;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class GameInitCommand extends ChessCommand {

	public GameInitCommand(IConfig<String> config, ILogs logs, ChessGameManager manager) {
		super("play", List.of("chess", "gameinit"), config, logs, manager);
		help.setCmdInfo("Creates new chess game on this channel.");
		help.setUsage(MessageFormat.format(
				"{0}{1} @OpponentTag [optional chosen color: -black(-b), -white(-w), -random" +
						"(-rand, -r) (default: -rand)]", config.get("prefix"), name()));
	}

	@Override
	public void handle(CommandContext context) {
		var channel = context.getChannel();
		var args = context.getArgs();
		var gameAuthor = context.getAuthor();

		if (args.isEmpty()) {
			sendUsage(channel);
			return;
		}

		String opponentTag = args.get(0);
		User opponent = getOpponent(channel, opponentTag);
		if (opponent == null) {
			channel.sendMessage(MessageFormat.format(
					"Cannot find non-bot member {0} on this channel", opponentTag)).queue();
			return;
		}

		String colorOption = (args.size() > 1) ? args.get(1) : null;
		User[] players = assignPlayers(colorOption, gameAuthor, opponent);
		if (players == null) {
			sendUsage(channel);
			return;
		}

		try {
			var whitePlayer = players[0];
			var blackPlayer = players[1];
			gamesManager.registerGame(channel, whitePlayer, blackPlayer);
			String msg = MessageFormat.format(
					"""
							Game created!
							White: {0}
							Black: {1}""",
					whitePlayer.getAsMention(),
					blackPlayer.getAsMention()
			);
			channel.sendMessage(msg).queue();
		} catch(IllegalStateException e) {
			channel.sendMessage("There already is a game ongoing on this channel.").queue();
		}
	}

	@Nullable
	private User getOpponent(TextChannel channel, String opponentTag) {
		final var tag = opponentTag.replaceAll("\\D", "");
		var opponentOptional = channel.getMembers().stream()
				.map(Member::getUser)
				.dropWhile(User::isBot)
				.filter(member -> member.getAsMention().replaceAll("\\D", "")
						.equals(tag))
				.findAny();
		return opponentOptional.isEmpty() ? null : opponentOptional.get();
	}

	@Nullable
	private User[] assignPlayers(@Nullable String colorOption,
	                             @NotNull User author,
	                             @NotNull User opponent) {
		User whitePlayer, blackPlayer;
		if (colorOption == null) {
			var nextBool = ThreadLocalRandom.current().nextBoolean();
			blackPlayer = (nextBool) ? author : opponent;
			whitePlayer = (nextBool) ? opponent : author;
		} else switch (colorOption) {
			case "-black" -> {
				blackPlayer = author;
				whitePlayer = opponent;
			}
			case "-white" -> {
				blackPlayer = opponent;
				whitePlayer = author;
			}
			case "-r", "-rand", "-random" -> {
				var nextBool = ThreadLocalRandom.current().nextBoolean();
				blackPlayer = (nextBool) ? author : opponent;
				whitePlayer = (nextBool) ? opponent : author;
			}
			default -> {
				return null;
			}
		}
		return new User[] {whitePlayer, blackPlayer};
	}


}
