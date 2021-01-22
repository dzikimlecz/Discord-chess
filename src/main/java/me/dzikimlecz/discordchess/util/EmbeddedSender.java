package me.dzikimlecz.discordchess.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class EmbeddedSender {

	private final Timer timer;

	public EmbeddedSender() {
		timer = new Timer();
	}


	public void sendFileAsThumbnail(File file,
	                                TextChannel channel,
	                                String title,
	                                String description) throws IOException {
		var embed = new EmbedBuilder();
		embed.setTitle(title);
		if (description != null)
			embed.appendDescription(description);
		var stream = new FileInputStream(file);
		embed.setThumbnail("attachment://img.png");
		channel.sendFile(stream, "img.png").embed(embed.build()).queue();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					stream.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}, 2000);
	}

	public void sendFile(File file,
	                     TextChannel channel,
	                     String title,
	                     String description) throws IOException {
		var embed = new EmbedBuilder();
		embed.setTitle(title);
		if (description != null)
			embed.appendDescription(description);
		var stream = new FileInputStream(file);
		embed.setImage("attachment://img.png");
		channel.sendFile(stream, "img.png").embed(embed.build()).queue();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					stream.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}, 2000);
	}


	public void sendImage(BufferedImage image,
	                      TextChannel channel,
	                      String title) throws IOException {
		sendImage(image, channel, title, null);
	}

	public void sendImage(BufferedImage image,
	                      TextChannel channel,
	                      String title,
	                      @Nullable String description) throws IOException {
		String fileName =
				MessageFormat.format("{0}{1}.png",
				                     LocalDateTime.now().format(DateTimeFormatter.ISO_TIME)
						                     .replaceAll("[:+]", "_"),
				                     ThreadLocalRandom.current().nextInt(100));
		var temp = new File("temp", fileName);
		ImageIO.write(image, "png", temp);
		sendFile(temp, channel, title, description);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					Files.delete(temp.toPath());
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}, 2200);
	}
}
