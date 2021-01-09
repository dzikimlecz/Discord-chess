package me.dzikimlecz.discordchess;


import me.dzikimlecz.discordchess.config.Config;
import me.dzikimlecz.discordchess.config.Logs;
import me.dzikimlecz.discordchess.event.EventListeners;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class Bot {
	public static void main(String[] args) throws Exception {
		var config = new Config();
		var logs = new Logs();

		JDABuilder.createDefault(config.get("token"))
				.addEventListeners(new EventListeners(config, logs))
				.setActivity(Activity.playing("chess"))
				.build();
	}
}
