package me.dzikimlecz.discordchess.config;

import io.github.cdimascio.dotenv.Dotenv;

public class Config implements Gettable<String> {
	private static final Dotenv dotenv;
	
	static {
		dotenv = Dotenv.load();
	}
	public String get(String key) {
		return dotenv.get(key.toUpperCase());
	}
}
