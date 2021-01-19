package me.dzikimlecz.discordchess.util;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class CommandHelpData {
	private final String name;
	private final List<String> aliases;
	private String cmdInfo;
	private String usage;

	public CommandHelpData(@NotNull String name, @NotNull List<String> aliases, @NotNull String cmdInfo) {
		this.name = name;
		this.aliases = aliases;
		this.cmdInfo = cmdInfo;
	}

	public String name() {
		return name;
	}

	public List<String> aliases() {
		return aliases;
	}

	public String cmdInfo() {
		return cmdInfo;
	}

	public void setCmdInfo(String cmdInfo) {
		this.cmdInfo = cmdInfo;
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
		for (int i = 0, aliasesSize = aliases.size(); i < aliasesSize; i++) {
			String alias = aliases.get(i);
			builder.append("\t\t\"").append(alias).append("\"");
			if (i != aliasesSize - 1) builder.append(',');
			builder.append("\n");
		}
		builder.append("\t],\n")
				.append("\t\"info\": \"").append(cmdInfo).append("\"\n")
				.append("\t\"usage\": \"").append(usage).append("\"\n")
				.append("}\n");
		return builder.toString();
	}

	@Override
	public String toString() {
		var builder = new StringBuilder();
		builder.append("`Command:` ").append(name).append('\n')
				.append("`Aliases:` ").append('\n');
		aliases.forEach(alias -> builder.append('\t').append(alias).append('\n'));
		builder.append("`Info:` ").append(cmdInfo).append('\n')
				.append("`Usage:` ").append(usage).append('\n');
		return builder.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CommandHelpData)) return false;
		CommandHelpData that = (CommandHelpData) o;
		return name.equals(that.name) && aliases.equals(that.aliases) && cmdInfo.equals(that.cmdInfo);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, aliases, cmdInfo);
	}


}
