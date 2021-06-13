package me.dzikimlecz.discordchess.event;

import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.event.commands.HelpCommand;
import me.dzikimlecz.discordchess.event.commands.ShutdownCommand;
import me.dzikimlecz.discordchess.event.commands.chess.*;
import me.dzikimlecz.discordchess.event.listeners.MessagesListener;
import me.dzikimlecz.discordchess.event.listeners.OnReadyListener;
import me.dzikimlecz.discordchess.game.ChessGameManager;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class EventListeners extends ListenerAdapter {

    private final OnReadyListener onReadyListener;
    private final MessagesListener messagesListener;
    private final CommandManager commandManager;
    private final ChessGameManager gameManager;

    public EventListeners(IConfig<String> config, ILogs logs) {
        commandManager = new CommandManager(config, logs);
        gameManager = new ChessGameManager(config, logs);
        addCommands(config, logs);
        onReadyListener = new OnReadyListener(config, logs, commandManager);
        messagesListener = new MessagesListener(config, logs, commandManager);
    }

    private void addCommands(IConfig<String> config, ILogs logs) {
        commandManager.addCommands(
                new ShutdownCommand(config, logs),
                new HelpCommand(config, logs, commandManager)
        );
        commandManager.addCommands(
                new GameInitCommand(config, logs, gameManager),
                new MoveCommand(config, logs, gameManager),
                new GameForceStopCommand(config, logs, gameManager),
                new DrawRequestCommand(config, logs, gameManager),
                new DrawResponseCommand(config, logs, gameManager),
                new ResignCommand(config, logs, gameManager),
                new PawnExchangeResponseCommand(config, logs, gameManager)
        );
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        onReadyListener.onReady(event);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        messagesListener.onGuildMessageReceived(event);
    }
}
