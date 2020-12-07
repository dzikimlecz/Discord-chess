package me.dzikimlecz.discordchess.bot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logs implements Loggable {
	
	private Logger logger;
	
	@Override
	public void info(Class<?> source, String msg) {
		setSource(source);
		logger.info(msg);
	}
	
	@Override
	public void info(Class<?> source, String msg, Object arg) {
		setSource(source);
		logger.info(msg, arg);
	}
	
	@Override
	public void debug(Class<?> source, String msg) {
		setSource(source);
		logger.debug(msg);
	}
	
	@Override
	public void debug(Class<?> source, String msg, Object arg) {
		setSource(source);
		logger.debug(msg, arg);
	}
	
	@Override
	public void error(Class<?> source, String msg) {
		setSource(source);
		logger.error(msg);
	}
	
	@Override
	public void error(Class<?> source, String msg, Object arg) {
		setSource(source);
		logger.error(msg, arg);
	}
	
	private void setSource(Class<?> source) {
		logger = LoggerFactory.getLogger(source);
	}
}
