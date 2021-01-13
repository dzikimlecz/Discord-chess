package me.dzikimlecz.discordchess.event.commands.chess;

import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.game.ChessGameManager;
import me.dzikimlecz.discordchess.util.CommandContext;

import java.util.List;

public class ResignCommand extends ChessCommand {
	public ResignCommand(IConfig<String> config, ILogs logs, ChessGameManager manager) {
		super("resign", List.of(), config, logs, manager);
	}

	@Override
	public void handle(CommandContext context) {

	}
}
