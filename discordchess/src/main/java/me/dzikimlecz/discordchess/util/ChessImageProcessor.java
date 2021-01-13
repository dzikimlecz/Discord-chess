package me.dzikimlecz.discordchess.util;

import me.dzikimlecz.chessapi.game.board.Board;
import me.dzikimlecz.chessapi.game.board.Color;
import me.dzikimlecz.chessapi.game.board.pieces.ChessPiece;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

public class ChessImageProcessor {

	private static final int SQUARE_SIDE_LENGTH = 512;
	private static final int BOARD_SIDE_LENGTH = 8 * SQUARE_SIDE_LENGTH;
	private static final BufferedImage WHITE_TILE;
	private static final BufferedImage BLACK_TILE;

	
	static {
		WHITE_TILE = new BufferedImage(SQUARE_SIDE_LENGTH,
		                                      SQUARE_SIDE_LENGTH,
		                                      BufferedImage.TYPE_4BYTE_ABGR);
		for (int y = 0; y < SQUARE_SIDE_LENGTH; y++)
			for (int x = 0; x < SQUARE_SIDE_LENGTH; x++) 
				WHITE_TILE.setRGB(x, y, new java.awt.Color(0xFEFEFE).getRGB());

		BLACK_TILE = new BufferedImage(SQUARE_SIDE_LENGTH,
		                                  SQUARE_SIDE_LENGTH,
		                                  BufferedImage.TYPE_4BYTE_ABGR);
		for (int y = 0; y < SQUARE_SIDE_LENGTH; y++)
			for (int x = 0; x < SQUARE_SIDE_LENGTH; x++)
				BLACK_TILE.setRGB(x, y, new java.awt.Color(0x263238).getRGB());
	}

	public BufferedImage generateImageOfBoard(ChessPiece[][] pieces) {
		if (pieces.length != 8 || pieces[0].length != 8)
			throw new IllegalArgumentException("Illegal board format");
		var boardImage = new BufferedImage(BOARD_SIDE_LENGTH,
		                                   BOARD_SIDE_LENGTH,
		                                   BufferedImage.TYPE_4BYTE_ABGR);
		for (int y = 0, piecesLength = pieces.length; y < piecesLength; y++) {
			ChessPiece[] chessPieces = pieces[y];
			for (int x = 0, chessPiecesLength = chessPieces.length; x < chessPiecesLength; x++) {
				ChessPiece piece = chessPieces[x];
				var color = ((y % 2) == (x % 2)) ? Color.BLACK : Color.WHITE;
				var squareImage = generateSquareImage(piece, color);
				var rgb = new int[SQUARE_SIDE_LENGTH * SQUARE_SIDE_LENGTH];
				squareImage.getRGB(0, 0, SQUARE_SIDE_LENGTH, SQUARE_SIDE_LENGTH,
				                   rgb, 0, 1);
				boardImage.setRGB(x * SQUARE_SIDE_LENGTH,
				                  y * SQUARE_SIDE_LENGTH,
				                  SQUARE_SIDE_LENGTH, SQUARE_SIDE_LENGTH,
				                  rgb, 0, 1);
			}
		}
		return boardImage;
	}

	private BufferedImage generateSquareImage(@Nullable ChessPiece piece, Color squareColor) {
		var squareBackground = (squareColor == Color.WHITE) ? WHITE_TILE : BLACK_TILE;
		if (piece == null) return squareBackground;
		var pieceImage = getPieceImage(piece);

		var bufferedImage = new BufferedImage(SQUARE_SIDE_LENGTH,
		                                      SQUARE_SIDE_LENGTH,
		                                      BufferedImage.TYPE_4BYTE_ABGR);
		for (int y = 0; y < SQUARE_SIDE_LENGTH; y++) {
			for (int x = 0; x < SQUARE_SIDE_LENGTH; x++) {
				int rgb = pieceImage.getRGB(x, y);
				var alpha = rgb / 10E6;
				int rgbToInsert = (alpha != 0) ? rgb : squareBackground.getRGB(x, y);
				bufferedImage.setRGB(x, y, rgbToInsert);
			}
		}
		return bufferedImage;
	}

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
		var path = MessageFormat.format(pathNotFilled, color, name);
		try {
			var input = new File("pieces\\pngs\\white\\rook.png");
			System.out.println(input.exists());
			return ImageIO.read(input);
		} catch(Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Piece without matching image");
		}
	}

}
