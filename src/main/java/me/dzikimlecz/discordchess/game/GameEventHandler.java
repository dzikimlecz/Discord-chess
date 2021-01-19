package me.dzikimlecz.discordchess.game;

import me.dzikimlecz.chessapi.ChessEventListener;
import me.dzikimlecz.chessapi.DrawReason;
import me.dzikimlecz.chessapi.GameInfo;
import me.dzikimlecz.chessapi.game.board.Color;
import me.dzikimlecz.chessapi.game.board.pieces.*;
import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.config.Resources;
import me.dzikimlecz.discordchess.util.ChessImageProcessor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class GameEventHandler implements ChessEventListener {
	private final GameInfo<TextChannel, User> gameInfo;
	private final TextChannel channel;
	private final ChessGameManager manager;
	private final IConfig<String> config;
	private final ILogs logs;
	private final BlockingQueue<Boolean> drawResponseContainer;
	private final BlockingQueue<String> exchangeResponseContainer;
	private final ChessImageProcessor imageProcessor;
	private Color drawRequester;
	private Color exchangingPlayer;

	public GameEventHandler(GameInfo<TextChannel, User> gameInfo,
	                        ChessGameManager manager,
	                        IConfig<String> config,
	                        ILogs logs) {
		this.gameInfo = gameInfo;
		channel = gameInfo.getKey();
		this.manager = manager;
		this.config = config;
		this.logs = logs;
		this.drawResponseContainer = new ArrayBlockingQueue<>(1);
		this.exchangeResponseContainer = new ArrayBlockingQueue<>(1);
		imageProcessor = new ChessImageProcessor(new Resources());
	}

	@Override
	public void onMoveHandled() {
		var image = imageProcessor.generateImageOfBoard(manager.read(channel));
		try {
			sendImage(image, channel);
		} catch(IOException e) {
			logs.error(e.getClass(), e.getMessage() + "in {}", this.getClass());
		}
	}

	private static void sendImage(BufferedImage image, TextChannel channel) throws IOException {
		String fileName = LocalDateTime.now().format(DateTimeFormatter.ISO_TIME)
				.replaceAll("[:+]", "_")  + ".png";
		var temp = new File("temp", fileName);
		var embed = new EmbedBuilder();
		embed.setTitle("Moved!");
		ImageIO.write(image, "png", temp);
		var file = new FileInputStream(temp);
		embed.setImage("attachment://board.png");
		channel.sendFile(file, "board.png").embed(embed.build()).queue();
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				temp.delete();
			}
		}, 500);
	}

	@Override
	public void onIllegalMove() {
		MessageBuilder msg = new MessageBuilder();
		msg.append("Sorry this move isn't clear for me ")
				.append(new Activity.Emoji(":pensive:"));
		channel.sendMessage(msg.build()).queue();
	}

	@Override
	public boolean onDrawRequest(Color requester) {
		drawResponseContainer.clear();
		drawRequester = requester;
		User requestingPlayer = gameInfo.getPlayer(requester);
		channel.sendMessage(requestingPlayer.getAsTag() + " requests a draw!").queue();
		channel.sendMessage(MessageFormat.format(
				"Send \"{0}draw accept\", or \"{0}draw deny\"",
				config.get("prefix")
		)).queue();
		try {
			return drawResponseContainer.take();
		} catch(InterruptedException e) {
			return false;
		}
	}

	@Nullable
	@Override
	public Class<? extends Piece> onPawnExchange() {
		drawResponseContainer.clear();
		var color = manager.getTurn(channel);
		exchangingPlayer = color;
		var player = gameInfo.getPlayer(color);
		var msg = new MessageBuilder();
		msg.append(player.getAsMention()).append(" has a pawn to exchange!\n")
				.append(MessageFormat.format("Send \"{0}pex\" + name of piece, or its notation",
			                             config.get("prefix")));
		channel.sendMessage(msg.build()).queue();
		String response;
		while (true) {
			try {
				response = exchangeResponseContainer.take();
			} catch(InterruptedException e) {
				return null;
			}
			switch (response.toLowerCase()) {
				case "p", "pawn", "pionek" -> {
					return Pawn.class;
				}
				case "s", "n", "knight", "skoczek" -> {
					return Knight.class;
				}
				case "g", "b", "goniec", "bishop" -> {
					return Bishop.class;
				}
				case "w", "r", "wieża", "wieza", "rook" -> {
					return Rook.class;
				}
				case "h", "q", "hetman", "dama", "krolowa", "królowa", "queen" -> {
					return Queen.class;
				}
				case "k", "król", "krol", "king" -> {
					return King.class;
				}
				default -> channel.sendMessage("There is no piece of name " + response).queue();
			}
		}
	}

	@Override
	public void onMate(Color winner) {
		gameInfo.setWinner(winner);
		channel.sendMessage(MessageFormat.format(
				"{0} has defeated {1}! GG!",
				Objects.requireNonNull(gameInfo.getWinner()).getAsTag(),
				Objects.requireNonNull(gameInfo.getLoser()).getAsTag()
		)).queue();
		manager.close(channel);
	}

	@Override
	public void onDraw(DrawReason drawReason) {
		channel.sendMessage("That is a draw!").queue();
		manager.close(channel);
	}

	public void replyToDraw(boolean accept) {
		try {
			drawResponseContainer.put(accept);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		drawRequester = null;
	}

	public void replyToExchange(String piece) {
		try {
			exchangeResponseContainer.put(piece);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		exchangingPlayer = null;
	}

	@Nullable
	public Color drawRequester() {
		return drawRequester;
	}

	public Color exchangingPlayer() {
		return exchangingPlayer;
	}
}
