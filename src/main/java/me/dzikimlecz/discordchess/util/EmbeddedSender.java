package me.dzikimlecz.discordchess.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class EmbeddedSender {

    private final Timer timer = new Timer();

    public void sendFileAsThumbnail(InputStream file,
                                    TextChannel channel,
                                    String title,
                                    String description) {
        sendFileAsThumbnail(file, channel, title, description, null);
    }

    public void sendFileAsThumbnail(InputStream file,
                                    TextChannel channel,
                                    String title,
                                    String description,
                                    @Nullable Color color) {
        var embed = initEmbed(title, description, color);
        var attachmentName = "img.png";
        embed.setThumbnail("attachment://" + attachmentName);
        send(channel, file, embed, attachmentName);
    }

    public void sendFile(InputStream file,
                         TextChannel channel,
                         String title) {
        sendFile(file, channel, title, null);
    }

    public void sendFile(InputStream file,
                         TextChannel channel,
                         String title,
                         String description) {
        sendFile(file, channel, title, description, null);
    }


    public void sendFile(InputStream file,
                         TextChannel channel,
                         String title,
                         String description,
                         @Nullable Color color) {
        var embed = initEmbed(title, description, color);
        var attachmentName = "img.png";
        embed.setImage("attachment://" + attachmentName);
        send(channel, file, embed, attachmentName);
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
        sendFile(new FileInputStream(temp), channel, title, description);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Files.delete(temp.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 2200);
    }

    private void send(TextChannel channel,
                      InputStream file,
                      EmbedBuilder embed,
                      String attachmentName) {
        channel.sendFile(file, attachmentName).embed(embed.build()).queue();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 2000);
    }

    private EmbedBuilder initEmbed(String title, String description, Color color) {
        var embed = new EmbedBuilder();
        embed.setTitle(title);
        if (description != null)
            embed.appendDescription(description);
        if (color != null)
            embed.setColor(color);
        return embed;
    }
}
