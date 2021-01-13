package me.dzikimlecz.discordchess.game;

import me.dzikimlecz.chessapi.ChessEventListener;
import me.dzikimlecz.chessapi.DrawReason;
import me.dzikimlecz.chessapi.GameInfo;
import me.dzikimlecz.chessapi.GamesManager;
import me.dzikimlecz.chessapi.game.board.Color;
import me.dzikimlecz.chessapi.game.board.pieces.ChessPiece;
import me.dzikimlecz.discordchess.config.IConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class GameEventHandler implements ChessEventListener {
	private final GameInfo<TextChannel, User> gameInfo;
	private final TextChannel channel;
	private final GamesManager<TextChannel> manager;
	private final IConfig<String> config;
	private final BlockingQueue<Boolean> responseContainer;
	private Color drawRequester;

	public GameEventHandler(GameInfo<TextChannel, User> gameInfo,
	                        GamesManager<TextChannel> manager,
	                        IConfig<String> config) {
		this.gameInfo = gameInfo;
		channel = gameInfo.getKey();
		this.manager = manager;
		this.config = config;
		this.responseContainer = new ArrayBlockingQueue<>(1);
	}

	@Override
	public void onMoveHandled() {
		var pieces = manager.read(channel);
		var builder = new EmbedBuilder();
		builder.setTitle("Game");
		for (ChessPiece[] piecesRow : pieces) {
			for (ChessPiece piece : piecesRow) {
				if (piece == null) builder.appendDescription(" ".repeat(3));
				else builder.appendDescription(String.valueOf(piece.color().name().charAt(0)))
						.appendDescription(piece.toString()).appendDescription(" ");
			}
			builder.appendDescription("\n");
		}
		channel.sendMessage(builder.build()).queue();
	}

	@Override
	public void onIllegalMove() {
		channel.sendMessage("That isn't an appropriate move!").queue();
	}

	@Override
	public boolean onDrawRequest(Color requester) {
		drawRequester = requester;
		User requestingPlayer = gameInfo.getPlayer(requester);
		channel.sendMessage(requestingPlayer.getAsTag() + " requests a draw!").queue();
		channel.sendMessage(MessageFormat.format(
				"Send \"{0}draw accept\", or \"{0}draw deny\"",
				config.get("prefix")
		)).queue();
		try {
			return responseContainer.take();
		} catch(InterruptedException e) {
			return false;
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
			responseContainer.put(accept);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		drawRequester = null;
	}

	@Nullable
	public Color drawRequester() {
		return drawRequester;
	}
}
