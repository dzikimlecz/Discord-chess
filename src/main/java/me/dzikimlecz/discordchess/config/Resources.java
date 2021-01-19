package me.dzikimlecz.discordchess.config;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;

public class Resources implements IConfig<File> {
	public File get(String path) {
		try {
			return Path.of(Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
					                                      .getResource(path)).toURI()).toFile();
		} catch(URISyntaxException | NullPointerException e) {
			throw new IllegalArgumentException("Resource not found", e);
		}
	}
}
