package me.dzikimlecz.discordchess.event.commands.chess;

import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.event.commands.AbstractCommand;
import me.dzikimlecz.discordchess.game.ChessGameManager;

import java.util.List;

public abstract class ChessCommand extends AbstractCommand {
	protected final ChessGameManager gamesManager;

	public ChessCommand(String name, List<String> aliases, IConfig<String> config, ILogs logs,
	                    ChessGameManager manager) {
		super(name, aliases, config, logs);
		gamesManager = manager;
	}
}
