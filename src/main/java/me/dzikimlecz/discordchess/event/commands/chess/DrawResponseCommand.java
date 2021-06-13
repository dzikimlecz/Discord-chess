package me.dzikimlecz.discordchess.event.commands.chess;

import me.dzikimlecz.chessapi.game.board.Color;
import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.game.ChessGameManager;
import me.dzikimlecz.discordchess.util.CommandContext;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.List;

public class DrawResponseCommand extends ChessCommand {
    public DrawResponseCommand(IConfig<String> config, ILogs logs, ChessGameManager manager) {
        super("draw", List.of("drs, rsd"), config, logs, manager);
        help.setCmdInfo("Command used for responding to a draw request");
        help.setUsage(MessageFormat.format("{0}{1} + \"accept\", or\"deny\"",
                config.get("prefix"),
                name()));

    }

    @Override
    public void handle(CommandContext context) {
        var args = context.getArgs();
        var channel = context.getChannel();
        var author = context.getAuthor();
        var listener = gamesManager.getListener(channel);
        var requester = listener.drawRequester();
        if (args.size() < 1) {
            sendUsage(channel);
            return;
        }

        var checkRequestStatus = checkRequest(channel, author, requester);
        if (checkRequestStatus != null) {
            channel.sendMessage(checkRequestStatus).queue();
            return;
        }

        var response = args.get(0);
        switch (response) {
            case "accept", "a", "ok", "yes", "si" -> listener.replyToDraw(true);
            case "deny", "d", "nah", "nope", "no" -> listener.replyToDraw(false);
            default -> channel.sendMessage("Usage: " + help.usage()).queue();
        }
    }

    @Nullable
    private String checkRequest(TextChannel channel, User author, Color requester) {
        if (requester == null)
            return "There are no pending draw requests on this channel";
        var properResponder = requester.opposite();
        return (gamesManager.getInfo(channel).getPlayer(properResponder).equals(author)) ?
                null : "You are not allowed to respond to the pending request on this channel";
    }
}
