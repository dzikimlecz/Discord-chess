package me.dzikimlecz.discordchess.util;

import me.dzikimlecz.chessapi.game.board.Color;
import me.dzikimlecz.chessapi.game.board.pieces.ChessPiece;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

public class ChessImageProcessor {

    private static final File WHITE_SIDE_BOARD_FILE = new File("temp/whiteSideBoard.png");
    private static final File BLACK_SIDE_BOARD_FILE = new File("temp/blackSideBoard.png");
    private static final int SQUARE_SIDE_LENGTH = 512;
    private static final int BOARD_SIDE_LENGTH = 8 * SQUARE_SIDE_LENGTH;
    final static int DARK_SQUARE_COLOR_RGB = 0x263238;
    final static int LIGHT_SQUARE_COLOR_RGB = 0xFEFEFE;

    static {
        if (!WHITE_SIDE_BOARD_FILE.exists() || !BLACK_SIDE_BOARD_FILE.exists())
            initBackgroundFiles();
    }

    private static void initBackgroundFiles() {
        createFiles();
        var WHITE_SIDE_BOARD = createImage();
        var BLACK_SIDE_BOARD = createImage();
        fillBackgrounds(WHITE_SIDE_BOARD, BLACK_SIDE_BOARD);
        saveBackgrounds(WHITE_SIDE_BOARD, BLACK_SIDE_BOARD);
    }

    private static void createFiles() {
        WHITE_SIDE_BOARD_FILE.mkdirs();
        BLACK_SIDE_BOARD_FILE.mkdirs();
        try {
            WHITE_SIDE_BOARD_FILE.createNewFile();
            BLACK_SIDE_BOARD_FILE.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize", e);
        }
    }

    private static void fillBackgrounds(BufferedImage WHITE_SIDE_BOARD, BufferedImage BLACK_SIDE_BOARD) {
        for (int y = 0; y < BOARD_SIDE_LENGTH; y++) {
            for (int x = 0; x < BOARD_SIDE_LENGTH; x++) {
                WHITE_SIDE_BOARD.setRGB(x, y, getColorForWhite(x, y).getRGB());
                BLACK_SIDE_BOARD.setRGB(x, y, getColorForBlack(x, y).getRGB());
            }
        }
    }

    private static java.awt.Color getColorForWhite(int x, int y) {
        boolean isSquareBlackFromWhiteSide = ((x / SQUARE_SIDE_LENGTH) % 2) == ((y / SQUARE_SIDE_LENGTH) % 2);
        final int rgbForWhite = isSquareBlackFromWhiteSide ? DARK_SQUARE_COLOR_RGB : LIGHT_SQUARE_COLOR_RGB;
        return new java.awt.Color(rgbForWhite);
    }

    private static java.awt.Color getColorForBlack(int x, int y) {
        boolean isSquareBlackFromWhiteSide = ((x / SQUARE_SIDE_LENGTH) % 2) == ((y / SQUARE_SIDE_LENGTH) % 2);
        final int rgbForBlack = isSquareBlackFromWhiteSide ? LIGHT_SQUARE_COLOR_RGB : DARK_SQUARE_COLOR_RGB;
        return new java.awt.Color(rgbForBlack);
    }

    private static void saveBackgrounds(BufferedImage WHITE_SIDE_BOARD, BufferedImage BLACK_SIDE_BOARD) {
        try {
            ImageIO.write(WHITE_SIDE_BOARD, "png", WHITE_SIDE_BOARD_FILE);
            ImageIO.write(BLACK_SIDE_BOARD, "png", BLACK_SIDE_BOARD_FILE);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize", e);
        }
    }

    public BufferedImage generateImageOfBoard(List<List<ChessPiece>> pieces,
                                              @NotNull Color colorOnTheBottom) {
        if (checkIfPiecesInvalid(pieces))
            throw new IllegalArgumentException("Illegal board format");
        var boardImage = getBoard(colorOnTheBottom);
        fillBoardImage(pieces, colorOnTheBottom, boardImage);
        return boardImage;
    }

    private void fillBoardImage(List<List<ChessPiece>> pieces, @NotNull Color colorOnTheBottom, BufferedImage boardImage) {
        int firstGeneratedRow = (colorOnTheBottom == Color.BLACK) ? 0 : 7;
        int limit = (colorOnTheBottom == Color.BLACK) ? 8 : -1;
        int delta = (colorOnTheBottom == Color.BLACK) ? 1 : -1;
        for (int row = firstGeneratedRow, boardRow = 0;
             row != limit && boardRow < 8; row += delta, boardRow++) {
            for (int line = 0; line < 8; line++) {
                var piece = pieces.get(row).get(line);
                if (piece == null) continue;
                var pieceImage = getPieceImage(piece);
                overlay(boardImage, pieceImage, boardRow, line);
            }
        }
    }

    private void overlay(BufferedImage boardImage, BufferedImage pieceImage, int boardRow, int line) {
        for (int y = 0; y < SQUARE_SIDE_LENGTH; y++) {
            int yInImage = boardRow * SQUARE_SIDE_LENGTH + y;
            for (int x = 0; x < SQUARE_SIDE_LENGTH; x++) {
                int xInImage = line * SQUARE_SIDE_LENGTH + x;
                if (getPixelAlpha(pieceImage, y, x) != 0)
                    boardImage.setRGB(xInImage, yInImage, pieceImage.getRGB(x, y));
            }
        }
    }

    private double getPixelAlpha(BufferedImage pieceImage, int y, int x) {
        return pieceImage.getRGB(x, y) / 1E6;
    }

    @NotNull
    private static BufferedImage createImage() {
        return new BufferedImage(BOARD_SIDE_LENGTH,
                BOARD_SIDE_LENGTH,
                BufferedImage.TYPE_4BYTE_ABGR);
    }

    private boolean checkIfPiecesInvalid(List<List<ChessPiece>> pieces) {
        return pieces == null || pieces.size() != 8 || pieces.get(0).size() != 8;
    }

    private BufferedImage getBoard(Color colorOnTheBottom) {
        try {
            return readBoard(colorOnTheBottom);
        } catch (IOException e) {
            initBackgroundFiles();
            try {
                return readBoard(colorOnTheBottom);
            } catch (IOException ex) {
                throw new RuntimeException("Could not read board", e);
            }
        }
    }

    private BufferedImage readBoard(Color colorOnTheBottom) throws IOException {
        return ImageIO.read(colorOnTheBottom == Color.WHITE ? WHITE_SIDE_BOARD_FILE: BLACK_SIDE_BOARD_FILE);
    }

    @NotNull
    private BufferedImage getPieceImage(@NotNull ChessPiece piece) {
        try {
            return ImageIO.read(getFromResources(getPath(piece)));
        } catch (Exception e) {
            throw new IllegalArgumentException("Piece without matching image", e);
        }
    }

    @NotNull
    private String getPath(@NotNull ChessPiece piece) {
        final String pathTemplate = "pieces/pngs/{0}/{1}.png";
        var color = piece.color().name().toLowerCase();
        var name = switch (piece.toString()) {
            case "P" -> "pawn";
            case "N" -> "knight";
            case "B" -> "bishop";
            case "R" -> "rook";
            case "Q" -> "queen";
            case "K" -> "king";
            default -> throw new IllegalStateException("Unexpected value: " + piece);
        };
        return MessageFormat.format(pathTemplate, color, name);
    }

    @NotNull
    private URL getFromResources(@NotNull String path) {
        if (path.startsWith("/")) path = path.substring(1);
        try {
            return Objects.requireNonNull(this.getClass().getResource(path));
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Resource not found", e);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Resource was not a file", e);
        }
    }
}
