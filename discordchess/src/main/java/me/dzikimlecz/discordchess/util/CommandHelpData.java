package me.dzikimlecz.discordchess.util;

import me.dzikimlecz.discordchess.event.commands.AbstractCommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class CommandHelpData {
	private final String name;
	private final List<String> aliases;
	private String usage;

	public CommandHelpData(@NotNull String name, @NotNull List<String> aliases, @NotNull String usage) {
		this.name = name;
		this.aliases = aliases;
		this.usage = usage;
	}

	public String name() {
		return name;
	}

	public List<String> aliases() {
		return aliases;
	}

	public String usage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String toJSON() {
		var builder = new StringBuilder("{\n");
		builder.append("\t\"name\": \"").append(name).append("\",\n")
				.append("\t\"aliases\": [").append('\n');
		aliases.forEach(alias -> builder.append("\t\t\"").append(alias).append("\",\n"));
		builder.append("\t],\n")
				.append("\t\"usage\": \"").append(usage).append("\"\n")
				.append("}\n");
		return builder.toString();
	}

	@Override
	public String toString() {
		var builder = new StringBuilder();
		builder.append("Command: ").append(name).append('\n')
				.append("Aliases: ").append('\n');
		aliases.forEach(alias -> builder.append('\t').append(alias).append('\n'));
		builder.append("Usage: ").append(usage);
		return builder.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CommandHelpData)) return false;
		CommandHelpData that = (CommandHelpData) o;
		return name.equals(that.name) && aliases.equals(that.aliases) && usage.equals(that.usage);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, aliases, usage);
	}
}
