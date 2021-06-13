package me.dzikimlecz.discordchess;


import me.dzikimlecz.discordchess.config.Config;
import me.dzikimlecz.discordchess.config.Logs;
import me.dzikimlecz.discordchess.event.EventListeners;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MEMBERS;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_PRESENCES;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.*;

public class Bot {
    public static void main(String[] args) throws Exception {
        var config = new Config();

		JDABuilder.createDefault(config.get("token"))
				.addEventListeners(new EventListeners(config, new Logs()))
				.enableIntents(
				        GUILD_MEMBERS,
                        GUILD_PRESENCES
                ).enableCache(
				        ACTIVITY,
                        CLIENT_STATUS,
                        MEMBER_OVERRIDES,
                        ROLE_TAGS
                ).setActivity(Activity.playing("chess"))
                .build();
	}
}
