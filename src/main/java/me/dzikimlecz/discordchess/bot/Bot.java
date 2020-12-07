package me.dzikimlecz.discordchess.bot;

import me.dzikimlecz.discordchess.bot.config.Config;
import me.dzikimlecz.discordchess.bot.config.Logs;
import me.dzikimlecz.discordchess.bot.event.EventListeners;
import me.dzikimlecz.discordchess.chess.board.Board;
import net.dv8tion.jda.api.JDABuilder;

public class Bot {
	public static void main(String[] args) throws Exception {
		System.out.println(new Board().toString());
	}
}
