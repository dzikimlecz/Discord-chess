package me.dzikimlecz.discordchess.util;

import me.duncte123.botcommons.commands.ICommandContext;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class CommandContext implements ICommandContext {
    private final GuildMessageReceivedEvent event;
    private final List<String> args;

    public CommandContext(GuildMessageReceivedEvent event, List<String> args) {
        this.event = event;
        this.args = args;
    }

    @Override
    public GuildMessageReceivedEvent getEvent() {
        return event;
    }

    @Override
    public TextChannel getChannel() {
        return event.getChannel();
    }

    @Override
    public Message getMessage() {
        return event.getMessage();
    }

    @Override
    public User getAuthor() {
        return event.getAuthor();
    }

    @Override
    public Member getMember() {
        return event.getMember();
    }

    @Override
    public JDA getJDA() {
        return event.getJDA();
    }

    @Override
    public User getSelfUser() {
        return getJDA().getSelfUser();
    }

    public List<String> getArgs() {
        return args;
    }
}
