package me.dzikimlecz.discordchess.event.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class ImageSender {

	private final Timer timer;

	public ImageSender() {
		timer = new Timer();
	}

	public void sendImage(BufferedImage image,
	                      TextChannel channel,
	                      String title) throws IOException {
		String fileName =
				MessageFormat.format("{0}{1}.png",
				                     LocalDateTime.now().format(DateTimeFormatter.ISO_TIME)
						                     .replaceAll("[:+]", "_"),
				                     ThreadLocalRandom.current().nextInt(100));
		var temp = new File("temp", fileName);
		var embed = new EmbedBuilder();
		embed.setTitle(title);
		ImageIO.write(image, "png", temp);
		var file = new FileInputStream(temp);
		embed.setImage("attachment://board.png");
		channel.sendFile(file, "board.png").embed(embed.build()).queue();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					file.close();
					Files.delete(temp.toPath());
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}, 1000);
	}
}
