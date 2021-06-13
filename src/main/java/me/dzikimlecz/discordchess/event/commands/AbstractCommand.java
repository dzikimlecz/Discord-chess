package me.dzikimlecz.discordchess.event.commands;

import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.util.CommandContext;
import me.dzikimlecz.discordchess.util.CommandHelpData;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public abstract class AbstractCommand {

    protected final IConfig<String> config;
    protected final ILogs logs;
    protected final CommandHelpData help;
    private final String name;
    private final List<String> aliases;

    public AbstractCommand(String name, List<String> aliases, IConfig<String> config, ILogs logs) {
        this.name = name;
        this.aliases = aliases;
        this.config = config;
        this.logs = logs;
        help = new CommandHelpData(name(), aliases(), "");
    }

    public abstract void handle(CommandContext context);

    public String name() {
        return name;
    }

    public CommandHelpData help() {
        return help;
    }

    public List<String> aliases() {
        return aliases;
    }

    protected void sendUsage(TextChannel channel) {
        channel.sendMessage("Usage: " + help.usage()).queue();
    }
}
