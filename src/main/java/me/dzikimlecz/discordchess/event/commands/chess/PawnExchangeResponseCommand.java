package me.dzikimlecz.discordchess.event.commands.chess;

import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.game.ChessGameManager;
import me.dzikimlecz.discordchess.util.CommandContext;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.text.MessageFormat;
import java.util.List;

public class PawnExchangeResponseCommand extends ChessCommand {
    public PawnExchangeResponseCommand(IConfig<String> config, ILogs logs, ChessGameManager manager) {
        super("pawnexchange", List.of("pex"), config, logs, manager);
        help.setUsage(MessageFormat.format("{0}{1} + name of piece", config.get("prefix"), name()));
        help.setCmdInfo("Command used to respond to event of pawn reaching the end of the board " +
                "and exchanging it to another Piece.");
    }

    @Override
    public void handle(CommandContext context) {
        var args = context.getArgs();
        var author = context.getAuthor();
        var channel = context.getChannel();
        if (!checkRequest(channel)) {
            channel.sendMessage("There aren't any pending exchanges!").queue();
            return;
        }
        if (!checkResponder(author, channel)) {
            channel.sendMessage("You aren't supposed to respond " + author.getAsMention()).queue();
            return;
        }
        if (args.isEmpty()) {
            sendUsage(channel);
            return;
        }
        gamesManager.getListener(channel).replyToExchange(args.get(0));
    }

    private boolean checkRequest(TextChannel key) {
        return gamesManager.getListener(key).exchangingPlayer() != null;
    }

    private boolean checkResponder(User author, TextChannel key) {
        var player = gamesManager.getInfo(key).getPlayer(gamesManager.getTurn(key));
        return player.equals(author);
    }
}
