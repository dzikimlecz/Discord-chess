package me.dzikimlecz.discordchess.event;

import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.event.commands.AbstractCommand;
import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.util.CommandContext;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
		boolean nameFound = commands.stream()
				.anyMatch((it) -> it.name().equalsIgnoreCase(cmd.name()));
		if (nameFound) throw new IllegalArgumentException("Command already present");
		cmd.aliases().forEach(alias -> {
			boolean aliasesMatch = commands.stream()
					.anyMatch((it) -> it.aliases().contains(alias));
			if (aliasesMatch) throw new IllegalArgumentException(
					String.format("Command with alias: %s already present.", alias));
		});
		commands.add(cmd);
	}
	
	public void addCommands(AbstractCommand... commands) {
		Arrays.stream(commands).forEachOrdered(this::addCommand);
	}
	
	@Nullable
	public AbstractCommand getCommand(String search) {
		for (AbstractCommand command : commands)
			if (command.name().equalsIgnoreCase(search) ||
			    command.aliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(search)))
				return command;
		return null;
	}
	
	public void handle(GuildMessageReceivedEvent event) {
		String[] split = event.getMessage().getContentRaw().replaceFirst(
						"(?i)" + Pattern.quote(config.get("prefix")), "")
				.trim().split("\\s+");
		String commandName = split[0].toLowerCase();
		var cmd = getCommand(commandName);
		if (cmd == null) return;
		List<String> args = Arrays.asList(split).subList(1, split.length);
		cmd.handle(new CommandContext(event, args));
	}
	
	public List<AbstractCommand> commands() {
		return commands;
	}
}
