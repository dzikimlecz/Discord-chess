package me.dzikimlecz.discordchess.game;

import me.dzikimlecz.chessapi.ChessEventListener;
import me.dzikimlecz.chessapi.GameInfo;
import me.dzikimlecz.chessapi.GamesManager;
import me.dzikimlecz.chessapi.game.board.Color;
import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class ChessGameManager extends GamesManager<TextChannel> {
	private final IConfig<String> config;
	private final ILogs logs;

	public ChessGameManager(IConfig<String> config, ILogs logs) {
		super();
		this.config = config;
		this.logs = logs;
	}

	public void registerGame(TextChannel channel, User whitePlayer, User blackPlayer) {
		var info = new GameInfo<>(channel, whitePlayer, blackPlayer);
		var gameEventHandler = new GameEventHandler(info, this, config);
		newGame(channel, gameEventHandler);
		attachInfo(channel, info);
	}

	private static String getLogsName(TextChannel channel) {
		return "%s::%s".formatted(channel.getGuild().getName(), channel.getName());
	}

	@Override
	public void newGame(TextChannel gameKey, ChessEventListener listener) {
		super.newGame(gameKey, listener);
		logs.write("New game created on {}", this.getClass(), getLogsName(gameKey));
	}

	@Override
	public void forceClose(TextChannel gameKey) {
		super.forceClose(gameKey);
		logs.write("Game closed on {}", this.getClass(), getLogsName(gameKey));
	}

	@Override
	@SuppressWarnings("unchecked")
	public GameInfo<TextChannel, User> getInfo(TextChannel gameKey) {
		return (GameInfo<TextChannel, User>) super.getInfo(gameKey);
	}

	public GameEventHandler getListener(TextChannel gameKey) {
		return (GameEventHandler) getGame(gameKey).listener();
	}

	public Color getTurn(TextChannel gameKey) {
		return getGame(gameKey).color();
	}
}
