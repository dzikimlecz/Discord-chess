package me.dzikimlecz.discordchess.config;

public interface ILogs {
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

    default void write(String msg) {
        info(msg);
    }

    default void write(String msg, Class<?> source) {
        info(source, msg);
    }

    default void write(String msg, Class<?> source, Object arg) {
        info(source, msg, arg);
    }

    default void write(String msg, Object arg) {
        info(this.getClass(), msg, arg);
    }
}
