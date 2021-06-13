package me.dzikimlecz.discordchess.game;

import me.dzikimlecz.chessapi.ChessEventListener;
import me.dzikimlecz.chessapi.GameInfo;
import me.dzikimlecz.chessapi.game.ChessGame;
import me.dzikimlecz.chessapi.game.board.Color;
import me.dzikimlecz.chessapi.game.board.pieces.ChessPiece;
import me.dzikimlecz.chessapi.manager.GamesManager;
import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.util.EmbeddedSender;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public class ChessGameManager implements GamesManager<TextChannel> {
    private final IConfig<String> config;
    private final ILogs logs;
    private final EmbeddedSender embeddedSender;
    private final GamesManager<TextChannel> manager;

    public ChessGameManager(IConfig<String> config, ILogs logs) {
        super();
        this.manager = GamesManager.newManager();
        this.config = config;
        this.logs = logs;
        embeddedSender = new EmbeddedSender();
    }

    private static String getLogsName(TextChannel channel) {
        return "%s::%s".formatted(channel.getGuild().getName(), channel.getName());
    }

    public void registerGame(TextChannel channel, User whitePlayer, User blackPlayer) {
        var info = new GameInfo<>(channel, whitePlayer, blackPlayer);
        var gameEventHandler = new GameEventHandler(info, this, config, logs, embeddedSender);
        newGame(channel, gameEventHandler);
        manager.attachInfo(info);
    }

    @Override
    public ChessGame newGame(TextChannel gameKey, ChessEventListener listener) {
        var result = manager.newGame(gameKey, listener);
        logs.write("New game created on {}", this.getClass(), getLogsName(gameKey));
        return result;
    }

    @Override
    public void forceClose(TextChannel gameKey) {
        manager.forceClose(gameKey);
        logs.write("Game closed on {}", this.getClass(), getLogsName(gameKey));
    }

    @Override
    public boolean close(TextChannel textChannel) {
        return manager.close(textChannel);
    }

    @Override
    public void move(TextChannel textChannel, String s) {
        manager.move(textChannel, s);
    }

    @Override
    public void attachInfo(GameInfo<TextChannel, ?> gameInfo) {
        manager.attachInfo(gameInfo);
    }

    @Override
    @SuppressWarnings("unchecked")
    public GameInfo<TextChannel, User> getInfo(TextChannel gameKey) {
        return (GameInfo<TextChannel, User>) manager.getInfo(gameKey);
    }

    @Override
    public List<List<ChessPiece>> read(TextChannel textChannel) {
        return manager.read(textChannel);
    }

    @Override
    public void requestDraw(TextChannel textChannel, Color color) {
        manager.requestDraw(textChannel, color);
    }

    @Override
    public void shutdown() {
        manager.shutdown();
    }

    public GameEventHandler getListener(TextChannel gameKey) {
        return (GameEventHandler) manager.getListener(gameKey);
    }

    public Color getTurn(TextChannel gameKey) {
        return manager.getTurn(gameKey);
    }
}
