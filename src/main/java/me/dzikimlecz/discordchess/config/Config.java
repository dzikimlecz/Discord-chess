package me.dzikimlecz.discordchess.config;


import io.github.cdimascio.dotenv.Dotenv;

public class Config implements IConfig<String> {
	private final Dotenv dotenv;
	
	public Config() {
		dotenv = Dotenv.load();
	}

	public String get(String key) {
		key = key.trim().toUpperCase().replaceAll("\\s+", "_");
		if (key.equals("PREFIX")) {
			return dotenv.get("DEFAULT_PREFIX");
		}
		return dotenv.get(key);
	}
}
