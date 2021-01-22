package me.dzikimlecz.discordchess.event.commands.chess;

import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.game.ChessGameManager;
import me.dzikimlecz.discordchess.util.CommandContext;
import me.dzikimlecz.discordchess.util.EmbeddedSender;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GameInitCommand extends ChessCommand {

	private final EmbeddedSender embeddedSender;

	public GameInitCommand(IConfig<String> config, ILogs logs, ChessGameManager manager) {
		super("chess", List.of("gameinit"), config, logs, manager);
		help.setCmdInfo("Creates new chess game on this channel.");
		help.setUsage(MessageFormat.format(
				"""
						{0}{1} @OpponentMention
						Optional settings:
						chosen color: -black(-b), -white(-w), -random(-rand, -r)(default: -rand)]"""
				, config.get("prefix"), name()));
		embeddedSender = new EmbeddedSender();
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

		var opponents = context.getMessage().getMentionedMembers();
		if (opponents.isEmpty()) {
			sendUsage(channel);
			return;
		}
		var opponentOptional = opponents.stream()
				.filter(this::memberValidForPlaying).findAny();

		if (opponentOptional.isEmpty()) {
			channel.sendMessage(MessageFormat.format(
					"Cannot find member {0}, with which I can play, on this channel",
					opponents.get(0).getAsMention())).queue();
			return;
		}
		var opponent = opponentOptional.get().getUser();

		String colorOption = (args.size() > 1) ? args.get(1) : null;
		User[] players = assignPlayers(colorOption, gameAuthor, opponent);
		if (players == null) {
			sendUsage(channel);
			return;
		}

		var whitePlayer = players[0];
		var blackPlayer = players[1];
		try {
			gamesManager.registerGame(channel, whitePlayer, blackPlayer);
		} catch(IllegalStateException e) {
			channel.sendMessage("There already is a game ongoing on this channel.").queue();
			return;
		}

		sendGameStartMessage(channel, whitePlayer, blackPlayer);
		sendBoard(channel);
	}

	private void sendGameStartMessage(TextChannel channel, User whitePlayer, User blackPlayer) {
		String description = MessageFormat.format(
				"""
						White: {0}
						Black: {1}""",
				whitePlayer.getAsMention(),
				blackPlayer.getAsMention()
		);
		try {
			var image = getMatchStartImage();
			embeddedSender.sendFileAsThumbnail(image,
			                                   channel,
			                                   "Game Created!",
			                                   description,
			                                   new java.awt.Color(0x60B9FE, false));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private InputStream getMatchStartImage() throws IOException {
		var filename = "match/match-%d.png".formatted(
		                                    ThreadLocalRandom.current().nextInt(5));
		try {
			return GameInitCommand.class.getResourceAsStream(filename);
		} catch(NullPointerException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void sendBoard(TextChannel channel) {
		var resource = GameInitCommand.class.getResourceAsStream("start-board.png");
		try {
			embeddedSender.sendFile(resource, channel, "Game Started!");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks, if it's possible to play, with that member
	 * @param member member to be validated for a game.
	 * @return {@code false} if member is a bot or have a proper role to play chess (currently
	 * just a role that is named <i>chess player</i>),<br>{@code true} otherwise.
	 */
	private boolean memberValidForPlaying(Member member) {
		return (!member.getUser().isBot()) || (member.getRoles().stream()
				.anyMatch(role -> role.getName().replaceAll("\\s", "")
						.equalsIgnoreCase("chessplayer")));
	}

	@Nullable
	private User[] assignPlayers(@Nullable String colorOption,
	                             @NotNull User author,
	                             @NotNull User opponent) {
		User whitePlayer, blackPlayer;
		colorOption = (colorOption == null) ? "-r" : colorOption;
		switch (colorOption) {
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
