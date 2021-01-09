package me.dzikimlecz.discordchess.game;

import me.dzikimlecz.chessapi.ChessEventListener;
import me.dzikimlecz.chessapi.DrawReason;
import me.dzikimlecz.chessapi.GameInfo;
import me.dzikimlecz.chessapi.GamesManager;
import me.dzikimlecz.chessapi.game.board.Color;
import me.dzikimlecz.chessapi.game.board.pieces.ChessPiece;
import me.dzikimlecz.discordchess.config.IConfig;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.text.MessageFormat;
import java.util.Objects;

public class GameEventHandler implements ChessEventListener {
	private final GameInfo<TextChannel, User> gameInfo;
	private final TextChannel channel;
	private final GamesManager<TextChannel> manager;
	private final IConfig<String> config;

	public GameEventHandler(GameInfo<TextChannel, User> gameInfo,
	                        GamesManager<TextChannel> manager,
	                        IConfig<String> config) {
		this.gameInfo = gameInfo;
		channel = gameInfo.getKey();
		this.manager = manager;
		this.config = config;
	}

	@Override
	public void onMoveHandled() {
		var pieces = manager.read(channel);
		var builder = new StringBuilder();
		for (ChessPiece[] piecesRow : pieces) {
			for (ChessPiece piece : piecesRow) {
				if (piece == null) builder.append(" ".repeat(3));
				else builder.append(piece.color().name().charAt(0))
						.append(piece.toString()).append(' ');
			}
			channel.sendMessage(builder.toString()).queue();
			builder.setLength(0);
		}
	}

	@Override
	public void onIllegalMove() {
		channel.sendMessage("That isn't an appropriate move!").queue();
	}

	@Override
	public boolean onDrawRequest(Color requestor) {
		long lastRequestTime = System.currentTimeMillis();
		User requestingPlayer = gameInfo.getPlayer(requestor);
		channel.sendMessage(requestingPlayer.getAsTag() + " requests a draw!").queue();
		channel.sendMessage(MessageFormat.format(
				"Send \"{0}draw accept\", or \"{0}draw deny\"",
				config.get("prefix")
		)).queue();
		return false;
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
}
