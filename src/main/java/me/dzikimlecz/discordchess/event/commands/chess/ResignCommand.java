package me.dzikimlecz.discordchess.event.commands.chess;

import me.dzikimlecz.chessapi.game.board.Color;
import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.game.ChessGameManager;
import me.dzikimlecz.discordchess.util.CommandContext;
import me.dzikimlecz.discordchess.util.ImageSender;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ResignCommand extends ChessCommand {

	private final ImageSender imageSender;

	public ResignCommand(IConfig<String> config, ILogs logs, ChessGameManager manager) {
		super("resign", List.of(), config, logs, manager);
		imageSender = new ImageSender();
	}

	@Override
	public void handle(CommandContext context) {
		var channel = context.getChannel();
		var author = context.getAuthor();
		var info = gamesManager.getInfo(channel);
		Optional<User> userOptional = Optional.empty();
		//checks if user is in game
		for (Color value : Color.values()) {
			var player = info.getPlayer(value);
			if (player.equals(author)) {
				userOptional = Optional.of(player);
				break;
			}
		}
		if (userOptional.isEmpty()) {
			channel.sendMessage("You aren't in any game on this channel").queue();
			return;
		}
		gamesManager.forceClose(channel);
		var loser = userOptional.get();
		var winnerColor = info.getPlayer(Color.WHITE).equals(loser) ? Color.BLACK :Color.WHITE;
		info.setWinner(winnerColor);
		var winner = info.getWinner();
		assert winner != null;
		sendResignImage(channel, loser, winner);
	}

	private void sendResignImage(TextChannel channel, User loser, User winner) {
		var title = loser.getAsMention() + " resigned!";
		var description = "Winner: " + winner.getAsMention();
		try {
			var image = ImageIO.read(getClass().getResource("resign.png"));
			imageSender.sendImage(image, channel, title, description);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
