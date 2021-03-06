package me.dzikimlecz.discordchess.game;

import me.dzikimlecz.chessapi.ChessEventListener;
import me.dzikimlecz.chessapi.DrawReason;
import me.dzikimlecz.chessapi.GameInfo;
import me.dzikimlecz.chessapi.game.board.Color;
import me.dzikimlecz.chessapi.game.board.pieces.*;
import me.dzikimlecz.discordchess.config.IConfig;
import me.dzikimlecz.discordchess.config.ILogs;
import me.dzikimlecz.discordchess.util.ChessImageProcessor;
import me.dzikimlecz.discordchess.util.EmbeddedSender;
import me.dzikimlecz.discordchess.util.concurrent.BlockingContainer;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

// TODO: 13.06.2021 replace this absolute mess of 2137 fields with something neater
public class GameEventHandler implements ChessEventListener {
    private final GameInfo<TextChannel, User> gameInfo;
    private final TextChannel channel;
    private final ChessGameManager manager;
    private final IConfig<String> config;
    private final ILogs logs;
    private final EmbeddedSender sender;
    private final ChessImageProcessor imageProcessor;
    private final BlockingContainer<Boolean> drawResponseContainer;
    private final BlockingContainer<String> exchangeResponseContainer;
    private final AtomicReference<Color> drawRequester;
    private final AtomicReference<Color> exchangingPlayer;

    public GameEventHandler(
            GameInfo<TextChannel, User> gameInfo,
            ChessGameManager manager,
            IConfig<String> config,
            ILogs logs,
            EmbeddedSender sender
    ) {
        this.gameInfo = gameInfo;
        channel = gameInfo.getKey();
        this.manager = manager;
        this.config = config;
        this.logs = logs;
        this.sender = sender;
        this.drawResponseContainer = BlockingContainer.create();
        this.exchangeResponseContainer = BlockingContainer.create();
        imageProcessor = new ChessImageProcessor();
        drawRequester = new AtomicReference<>();
        exchangingPlayer = new AtomicReference<>();
    }

    @Override
    public void onMoveHandled() {
        var color = manager.getTurn(channel);
        var image = imageProcessor.generateImageOfBoard(manager.read(channel), color);
        try {
            sender.sendImage(image, channel, "Moved");
        } catch (IOException e) {
            logs.error(e.getClass(), e.getMessage() + "in {}", this.getClass());
        }
    }

    @Override
    public void onIllegalMove() {
        MessageBuilder msg = new MessageBuilder();
        msg.append("Sorry this move isn't clear for me ")
                .append(new Activity.Emoji(":pensive:"));
        channel.sendMessage(msg.build()).queue();
    }

    @Override
    public boolean onDrawRequest(Color requester) {
        drawResponseContainer.clear();
        drawRequester.set(requester);
        User requestingPlayer = gameInfo.getPlayer(requester);
        channel.sendMessage(requestingPlayer.getAsMention() + " requests a draw!").queue();
        channel.sendMessage(MessageFormat.format(
                "Send \"{0}draw accept\", or \"{0}draw deny\"",
                config.get("prefix")
        )).queue();
        try {
            return drawResponseContainer.take();
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public Class<? extends Piece> onPawnExchange() {
        drawResponseContainer.clear();
        var color = manager.getTurn(channel);
        exchangingPlayer.set(color);
        var player = gameInfo.getPlayer(color);
        sendExchangeMessage(player);
        String response;
        while (true) {
            try {
                response = exchangeResponseContainer.take();
            } catch (InterruptedException e) {
                return null;
            }
            Class<? extends Piece> result = switch (response.toLowerCase()) {
                case "p", "pawn", "pionek" -> Pawn.class;
                case "s", "n", "knight", "skoczek" -> Knight.class;
                case "g", "b", "goniec", "bishop" -> Bishop.class;
                case "w", "r", "wieża", "wieza", "rook" -> Rook.class;
                case "h", "q", "hetman", "dama", "krolowa", "królowa", "queen" -> Queen.class;
                case "k", "król", "krol", "king" -> King.class;
                default -> null;
            };
            if (result != null) return result;
            channel.sendMessage("There is no piece of name " + response).queue();
        }
    }

    private void sendExchangeMessage(User player) {
        var instruction =
                "Send \"%s%s\" + name of piece, or its notation".formatted(
                        config.get("prefix"), "pex");
        var title = player.getAsMention() + "has a pawn to promote!\n";
        var filename = "promotion.png";
        var file = GameEventHandler.class.getResourceAsStream(filename);
        sender.sendFileAsThumbnail(file, channel, title, instruction);
    }

    @Override
    public void onMate(Color winnerColour) {
        gameInfo.setWinner(winnerColour);
        var winner = gameInfo.getWinner();
        var loser = gameInfo.getLoser();
        sendMateImage(winner, loser);
    }

    private void sendMateImage(User winner, User loser) {
        var description = MessageFormat.format("{0} has deafeted {1}!",
                winner.getAsMention(),
                loser.getAsMention());
        var image = getMateImage();
        sender.sendFileAsThumbnail(image,
                channel,
                "Mate!",
                description,
                new java.awt.Color(0xFD2A39, false));

    }

    private InputStream getMateImage() {
        var filename = "win/win-%d.png"
                .formatted(ThreadLocalRandom.current().nextInt(4));
        return GameEventHandler.class.getResourceAsStream(filename);
    }

    @Override
    public void onDraw(DrawReason drawReason) {
        var msg = new MessageBuilder()
                .append("That's a draw!\n").append(
                        switch (drawReason) {
                            case STALE_MATE -> "You've got stale-mated!";
                            case TRIPLE_POSITION_REPEAT -> "Position was repeated!";
                            case FIFTY_MOVES_WITHOUT_PAWN -> "Pawns weren't used for so long! (50 moves)";
                            case LACK_OF_PIECES -> "It's not possible to mate for you!";
                            case PLAYERS_DECISION -> "Your decision.";
                        });
        channel.sendMessage(msg.build()).queue();
    }

    public void replyToDraw(boolean accept) {
        try {
            drawResponseContainer.put(accept);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        drawRequester.set(null);
    }

    public void replyToExchange(String piece) {
        try {
            exchangeResponseContainer.put(piece);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        exchangingPlayer.set(null);
    }

    @Nullable
    public Color drawRequester() {
        return drawRequester.get();
    }

    public Color exchangingPlayer() {
        return exchangingPlayer.get();
    }

}
