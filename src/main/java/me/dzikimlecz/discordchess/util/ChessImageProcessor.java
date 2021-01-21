package me.dzikimlecz.discordchess.util;

import me.dzikimlecz.chessapi.game.board.pieces.ChessPiece;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Objects;

public class ChessImageProcessor {

	private static final int SQUARE_SIDE_LENGTH = 512;
	private static final int BOARD_SIDE_LENGTH = 8 * SQUARE_SIDE_LENGTH;
	public static final BufferedImage EMPTY_BOARD;

	static {
		EMPTY_BOARD = new BufferedImage(BOARD_SIDE_LENGTH,
		                                BOARD_SIDE_LENGTH,
		                                BufferedImage.TYPE_4BYTE_ABGR);
		for (int y = 0; y < BOARD_SIDE_LENGTH; y++) {
			for (int x = 0; x < BOARD_SIDE_LENGTH; x++) {
				boolean isSquareBlack =
						((x / SQUARE_SIDE_LENGTH) % 2) == ((y / SQUARE_SIDE_LENGTH) % 2);
				var color = (isSquareBlack) ?
						new java.awt.Color(0x263238) : new java.awt.Color(0xFEFEFE);
				EMPTY_BOARD.setRGB(x, y, color.getRGB());
			}
		}

	}

	public BufferedImage generateImageOfBoard(ChessPiece[][] pieces) {
		if (pieces.length != 8 || pieces[0].length != 8)
			throw new IllegalArgumentException("Illegal board format");
		var boardImage = new BufferedImage(BOARD_SIDE_LENGTH,
		                                   BOARD_SIDE_LENGTH,
		                                   BufferedImage.TYPE_4BYTE_ABGR);
		boardImage.setData(EMPTY_BOARD.getRaster());

		for (int row = 0; row < 8; row++) {
			for (int line = 0; line < 8; line++) {
				var piece = pieces[row][line];
				if (piece == null) continue;
				var pieceImage = getPieceImage(piece);
				for (int rawY = 0; rawY < SQUARE_SIDE_LENGTH; rawY++) {
					int y = row * SQUARE_SIDE_LENGTH + rawY;
					for (int rawX = 0; rawX < SQUARE_SIDE_LENGTH; rawX++) {
						int x = line * SQUARE_SIDE_LENGTH + rawX;
						int pieceImageRGB = pieceImage.getRGB(rawX, rawY);
						double pieceImageAlpha = pieceImageRGB / 1E6;
						if (pieceImageAlpha != 0) boardImage.setRGB(x, y, pieceImageRGB);
					}
				}
			}
		}
		return boardImage;
	}

	@NotNull
	private BufferedImage getPieceImage(@NotNull ChessPiece piece) {
		final String pathNotFilled = "pieces/pngs/{0}/{1}.png";
		var color = piece.color().name().toLowerCase();
		var name  = switch (piece.toString()) {
			case "P" -> "pawn";
			case "N" -> "knight";
			case "B" -> "bishop";
			case "R" -> "rook";
			case "Q" -> "queen";
			case "K" -> "king";
			default -> throw new IllegalStateException("Unexpected value: " + piece.toString());
		};
		String path = MessageFormat.format(pathNotFilled, color, name);
		try {
			return ImageIO.read(getFromResources(path));
		} catch(Exception e) {
			throw new IllegalArgumentException("Piece without matching image", e);
		}
	}

	@NotNull
	private URL getFromResources(@NotNull String path) {
		if (path.startsWith("/")) path = path.substring(1);
		try {
			return Objects.requireNonNull(this.getClass().getResource(path));
		}  catch (NullPointerException e) {
			throw new IllegalArgumentException("Resource not found", e);
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("Resource was not a file", e);
		}
	}
}
