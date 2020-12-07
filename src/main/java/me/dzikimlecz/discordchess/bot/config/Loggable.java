package me.dzikimlecz.discordchess.bot.config;

import me.dzikimlecz.discordchess.bot.event.AbstractEventListener;

public interface Loggable {
	void info(Class<?> source, String msg);
	void info(Class<?> source, String msg, Object arg);
	default void info(String msg) {
		info(this.getClass(), msg);
	}
	
	void debug(Class<?> source, String msg);
	void debug(Class<?> source, String msg, Object arg);
	default void debug(String msg) {
		debug(this.getClass(), msg);
	}
	
	void error(Class<?> source, String msg);
	void error(Class<?> source, String msg, Object arg);
	default void error(String msg) {
		error(this.getClass(), msg);
	}
	
	default void write(String msg, Class<?> source) {
		info(source, msg);
	}
	
	default void write(String msg, Class<?> source, Object arg) {
		info(source, msg, arg);
	}
	
	default void write(Class<AbstractEventListener> source, String msg, Object arg) {
		info(this.getClass(), msg, arg);
	}
}
