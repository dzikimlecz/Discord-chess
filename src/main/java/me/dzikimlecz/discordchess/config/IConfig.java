package me.dzikimlecz.discordchess.config;

public interface IConfig<E> {
    E get(String key);
}
