package me.dzikimlecz.discordchess.event.commands.chess;

import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.event.commands.ImageSender;
import me.dzikimlecz.discordchess.game.ChessGameManager;
import me.dzikimlecz.discordchess.util.ChessImageProcessor;
import me.dzikimlecz.discordchess.util.CommandContext;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class GameInitCommand extends ChessCommand {

	private final ChessImageProcessor imageProcessor;
	private final ImageSender imageSender;

	public GameInitCommand(IConfig<String> config, ILogs logs, ChessGameManager manager) {
		super("chess", List.of("gameinit"), config, logs, manager);
		help.setCmdInfo("Creates new chess game on this channel.");
		help.setUsage(MessageFormat.format(
				"""
						{0}{1} @OpponentMention
						Optional settings:
						chosen color: -black(-b), -white(-w), -random(-rand, -r)(default: -rand)]"""
				, config.get("prefix"), name()));
		imageProcessor = new ChessImageProcessor();
		imageSender = new ImageSender();
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
		var opponent = getOpponent(channel, opponentTag);
		if (opponent.isEmpty()) {
			channel.sendMessage(MessageFormat.format(
					"Cannot find member {0}, with which I can play, on this channel",
					opponentTag)).queue();
			return;
		}

		String colorOption = (args.size() > 1) ? args.get(1) : null;
		User[] players = assignPlayers(colorOption, gameAuthor, opponent.get());
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

		String msg = MessageFormat.format(
				"""
						Game created!
						White: {0}
						Black: {1}""",
				whitePlayer.getAsMention(),
				blackPlayer.getAsMention()
		);
		channel.sendMessage(msg).queue();
		new Thread(() -> sendBoard(channel)).start();
	}

	private void sendBoard(TextChannel channel) {
		var image = imageProcessor.generateImageOfBoard(gamesManager.read(channel));
		try {
			imageSender.sendImage(image, channel, "Game Started!");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private Optional<User> getOpponent(TextChannel channel, String opponentTag) {
		var tag = opponentTag.replaceAll("\\D", "");
		return channel.getMembers().stream()
				.filter(this::memberValidForPlaying)
				.map(Member::getUser)
				.filter(user -> user.getAsMention().replaceAll("\\D", "")
						.equals(tag))
				.findAny();
	}

	/**
	 * Checks, if it's possible to play, with that member
	 * @param member member to be validated for a game.
	 * @return {@code false} if member is a bot or have a proper role to play chess (currently
	 * just a role that is named <i>chess player</i>),<br>{@code true} otherwise.
	 */
	private boolean memberValidForPlaying(Member member) {
		if (!member.getUser().isBot()) return true;
		return member.getRoles().stream()
				.anyMatch(role -> role.getName().replaceAll("\\s", "")
						.equalsIgnoreCase("chessplayer"));
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
