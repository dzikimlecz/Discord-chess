package me.dzikimlecz.discordchess.chess;

import me.dzikimlecz.discordchess.chess.board.Board;
import me.dzikimlecz.discordchess.chess.board.Square;
import me.dzikimlecz.discordchess.chess.pieces.*;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class MoveVariations {

	private final Map<Piece, Square> semiPossibleMoves;
	private String notation;
	private final Board board;
	private final Color color;
	private final IllegalArgumentException illegalMove;


	public MoveVariations(String notation, Board board, Color color) throws Exception {
		this.notation = notation;
		this.board = board;
		this.color = color;
		illegalMove = new IllegalArgumentException(MessageFormat.format(
				"{0} is not valid move notation for {1}",
				notation, color.toString().toLowerCase())
		);

		if (notation.startsWith("O-O") || notation.startsWith("0-0")) {
			semiPossibleMoves = parseCastling(notation);
			return;
		}

		semiPossibleMoves = switch (notation.length()) {
			case 2 -> parseSimplePawnMove(notation);
			case 3 -> parseSimplePieceMove(notation);
			case 4 -> parseMove(notation);
			case 5 -> parseSpecifiedMove(notation);
			default -> throw illegalMove;
		};
	}

	private Map<Piece, Square> parseSimplePieceMove(String notation) throws IllegalAccessException {
		boolean pieceMoves = Pattern.matches("[BGKNSPQRHW][a-h][1-8]", notation);
		boolean pawnChangesLine = Pattern.matches("[a-h][a-h][1-8]", notation);
		if (pieceMoves) {
			final char piece = notation.charAt(0);
			final char line = notation.charAt(1);
			final int row = notation.charAt(2) - '0';
			final var square = board.square(line, row);
			switch (piece) {
				case 'P' -> {
					return parseSimplePawnMove(notation.substring(1));
				}
				case 'B', 'G' -> {
					List<Bishop> bishops = board.getBishopsOnDiagonals(line, row, color);
					Map<Bishop, Square> map = new HashMap<>();
					bishops.forEach(e -> map.put(e, square));
					return Map.copyOf(map);
				}
				case 'N', 'S' -> {
					List<Knight> knights = board.getKnightsAttacking(line, row, color);
					Map<Knight, Square> map = new HashMap<>();
					knights.forEach(e -> map.put(e, square));
					return Map.copyOf(map);
				}
				case 'R', 'W' -> {
					List<Rook> rooks = board.getRooksOnLines(line, row, color);
					Map<Rook, Square> map = new HashMap<>();
					rooks.forEach(e -> map.put(e, square));
					return Map.copyOf(map);
				}
				case 'Q', 'H' -> {
					List<Queen> queens = board.getQueenFromLineOrDiagonal(line, row, color);
					Map<Queen, Square> map = new HashMap<>();
					queens.forEach(e -> map.put(e, square));
					return Map.copyOf(map);
				}
				case 'K' -> {
					Optional<King> king = board.getKingNearby(line, row);
					if (king.isEmpty()) return Map.of();
					return Map.of(king.get(), square);
				}
			}
		}
		if (pawnChangesLine) {

		}
	}

	private Map<Piece, Square> parseSimplePawnMove(String notation) throws IllegalAccessException {
		if (!Pattern.matches("[a-h][1-8]", notation)) throw illegalMove;
		char line = notation.charAt(0);
		int row = notation.charAt(1) - '0';
		var square = board.square(line, row);
		var pawns = board.getPawnsFromLine(line, color);
		Map<Pawn, Square> map = new HashMap<>();
		pawns.forEach(e -> map.put(e, square));
		return Map.copyOf(map);
	}

	private Map<Piece, Square> parseMove(String notation) {


	}

	private Map<Piece, Square> parseSpecifiedMove(String notation) {
	}

	private Map<Piece, Square> parseCastling(String notation) {
		boolean isCastlingShort;
		if (notation.equals("O-O-O") || notation.equals("0-0-0"))
			isCastlingShort = false;
		else if (notation.equals("O-O") || notation.equals("0-0"))
			isCastlingShort = true;
		else throw illegalMove;

		final int row = (color == Color.WHITE) ? 1 : 8;
		Piece piece = board.square('e', row).getPiece();
		if (!(piece instanceof King)) throw illegalMove;
		var king = (King) piece;

		char rookLine = (isCastlingShort) ? 'h' : 'a';
		piece = board.square(rookLine, row).getPiece();
		if (!(piece instanceof Rook)) throw illegalMove;
		var rook = (Rook) piece;

		char newKingLine = (isCastlingShort) ? 'g' : 'c';
		char newRookLine = (isCastlingShort) ? 'f' : 'd';
		return Map.of(
				king, board.square(newKingLine, row),
				rook, board.square(newRookLine, row)
		);
	}

}
