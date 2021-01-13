package me.dzikimlecz.discordchess.util;

import me.dzikimlecz.chessapi.game.board.Color;
import me.dzikimlecz.chessapi.game.board.pieces.ChessPiece;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.MessageFormat;

public class ChessImageProcessor {

	private static final int SQUARE_SIDE_LENGTH = 512;
	private static final int BOARD_SIDE_LENGTH = 8 * SQUARE_SIDE_LENGTH;
	public static final BufferedImage BOARD;

	
	static {
		BOARD = new BufferedImage(BOARD_SIDE_LENGTH,
		                          BOARD_SIDE_LENGTH,
		                          BufferedImage.TYPE_4BYTE_ABGR);
		for (int y = 0; y < BOARD_SIDE_LENGTH; y++) {
			for (int x = 0; x < BOARD_SIDE_LENGTH; x++) {
				boolean isSquareBlack =
						((x / SQUARE_SIDE_LENGTH) % 2) == ((y / SQUARE_SIDE_LENGTH) % 2);
				var color = (isSquareBlack) ?
						new java.awt.Color(0x263238) : new java.awt.Color(0xFEFEFE);
				BOARD.setRGB(x, y, color.getRGB());
			}
		}

	}

	public BufferedImage generateImageOfBoard(ChessPiece[][] pieces) {
		if (pieces.length != 8 || pieces[0].length != 8)
			throw new IllegalArgumentException("Illegal board format");
		var boardImage = new BufferedImage(BOARD_SIDE_LENGTH,
		                                   BOARD_SIDE_LENGTH,
		                                   BufferedImage.TYPE_4BYTE_ABGR);
		for (int row = 0; row < 8; row++) {
			for (int line = 0; line < 8; line++) {
				var piece = pieces[row][line];
				boolean noPiece = (piece == null);
				var pieceImage = (noPiece) ? null : getPieceImage(piece);
				for (int rawY = 0; rawY < SQUARE_SIDE_LENGTH; rawY++) {
					int y = row * SQUARE_SIDE_LENGTH + rawY;
					for (int rawX = 0; rawX < SQUARE_SIDE_LENGTH; rawX++) {
						int x = line * SQUARE_SIDE_LENGTH + rawX;
						int pieceImageRGB = (noPiece) ? 0 : pieceImage.getRGB(rawX, rawY);
						double pieceImageAlpha = pieceImageRGB / 1E6;
						int rgb = (pieceImageAlpha == 0) ? BOARD.getRGB(x, y) : pieceImageRGB;
						boardImage.setRGB(x, y, rgb);
					}
				}
			}
		}
		return boardImage;
	}

	private BufferedImage getPieceImage(@NotNull ChessPiece piece) {
		final String pathNotFilled = "src/main/resources/pieces/pngs/{0}/{1}.png";
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
		var path = MessageFormat.format(pathNotFilled, color, name);
		try {
			var input = new File(path);
			return ImageIO.read(input);
		} catch(Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Piece without matching image");
		}
	}

}
