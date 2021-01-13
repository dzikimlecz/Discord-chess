package me.dzikimlecz.discordchess.event.commands;

import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.event.CommandManager;
import me.dzikimlecz.discordchess.util.CommandContext;
import me.dzikimlecz.discordchess.util.CommandHelpData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.List;
import java.util.Scanner;

public class HelpCommand extends AbstractCommand {
	private final CommandManager manager;

	public HelpCommand(IConfig<String> config, ILogs logs, CommandManager manager) {
		super("help", List.of(), config, logs);
		this.manager = manager;
		help.setCmdInfo("Displays help for ordered command or list of them.");
		help.setUsage(MessageFormat.format(
				"{0}{1} ordered-command-name or \"list\"",
				config.get("prefix"), name()
		));
	}

	@Override
	public void handle(CommandContext context) {
		var channel = context.getChannel();
		var args = context.getArgs();
		if (args.isEmpty()) {
			sendUsage(channel);
			return;
		}
		if (args.get(0).equals("list")) {
			var messageBuilder = new MessageBuilder();
			manager.commands().forEach(command -> {
				messageBuilder.append(command.name()).append(" (");
				command.aliases().forEach(alias -> messageBuilder.append(alias).append(", "));
				messageBuilder.replaceLast(", ", ")\n");
			});
			channel.sendMessage(messageBuilder.build()).queue();
		}
		CommandHelpData commandHelpData;
		try {
			commandHelpData = getHelpData(args.get(0));
		} catch(Exception e) {
			channel.sendMessage(e.getMessage()).queue();
			return;
		}
		sendHelp(channel, commandHelpData);
	}

	private void sendHelp(TextChannel channel, CommandHelpData data) {
		var reader = new Scanner(data.toString());
		var embedBuilder = new EmbedBuilder();
		embedBuilder.setColor(Color.YELLOW);
		embedBuilder.setTitle("Help: " + data.name());
		while (reader.hasNextLine())
			embedBuilder.appendDescription(reader.nextLine()).appendDescription("\n");
		channel.sendMessage(embedBuilder.build()).queue();
	}

	private CommandHelpData getHelpData(String commandName) {
		var commandOptional = manager.commands().stream()
				.filter(command -> command.name().equals(commandName)).findAny();
		if (commandOptional.isPresent()) return commandOptional.get().help();
		commandOptional = manager.commands().stream()
				.filter(command -> command.aliases().contains(commandName)).findAny();
		if (commandOptional.isPresent()) return commandOptional.get().help();
		throw new IllegalArgumentException(
				"There isn't any command matching query: " + commandName
		);
	}
}
