package me.dzikimlecz.discordchess.event;

import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.event.commands.AbstractCommand;
import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.util.CommandContext;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {
	
	private final List<AbstractCommand> commands;
	private final IConfig<String> config;
	private final ILogs logs;
	
	public CommandManager(IConfig<String> config, ILogs logs) {
		this.config = config;
		this.logs = logs;
		commands = new ArrayList<>();
	}
	
	public void addCommand(AbstractCommand cmd) {
		boolean nameFound =
				commands.stream().anyMatch((it) -> it.name().equalsIgnoreCase(cmd.name()));
		if (nameFound) throw new IllegalArgumentException("Command already present");
		for (String alias : cmd.aliases()) {
			boolean aliasesMatch =
					commands.stream().anyMatch((it) -> it.aliases().contains(alias));
			if (aliasesMatch) throw new IllegalArgumentException(
						String.format("Command with alias %s already present.", alias));
		}
		commands.add(cmd);
	}
	
	public void addCommands(AbstractCommand... commands) {
		for (AbstractCommand command : commands) addCommand(command);
	}
	
	@Nullable
	public AbstractCommand getCommand(String search) {
		String searchLower = search.toLowerCase();
		for (AbstractCommand command : commands)
			if (command.name().equals(searchLower) || command.aliases().contains(searchLower))
				return command;
		return null;
	}
	
	public void handle(GuildMessageReceivedEvent event) {
		String[] split = event.getMessage().getContentRaw().replaceFirst(
						"(?i)" + Pattern.quote(config.get("prefix")), "")
						.split("\\s+");
		String invoke = split[0].toLowerCase();
		AbstractCommand cmd = getCommand(invoke);
		if (cmd == null) return;
		List<String> args = Arrays.asList(split).subList(1, split.length);
		var ctx = new CommandContext(event, args);
		cmd.handle(ctx);
	}
	
	public List<AbstractCommand> commands() {
		return commands;
	}
	
}
