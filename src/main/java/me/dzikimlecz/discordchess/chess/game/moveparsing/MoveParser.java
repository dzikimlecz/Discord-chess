package me.dzikimlecz.discordchess.chess.game.moveparsing;

import me.dzikimlecz.discordchess.chess.game.board.Color;
import me.dzikimlecz.discordchess.chess.game.board.Board;
import me.dzikimlecz.discordchess.chess.game.board.Square;
import me.dzikimlecz.discordchess.chess.game.board.pieces.*;
import me.dzikimlecz.discordchess.chess.game.movestoring.GamesData;
import me.dzikimlecz.discordchess.chess.game.movestoring.MoveData;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MoveParser implements me.dzikimlecz.discordchess.chess.game.moveparsing.IMoveParser {

	private final GamesData data;

	private Board board;
	private Color color;
	private IllegalArgumentException illegalMove;

	public MoveParser(GamesData data) {
		this.data = data;
	}

	public MoveData parse(String notation) {
		board = data.getBoard();
		color = data.getColor();
		notation = notation.replaceAll("\\s", "");
		Map<Piece, Square> variations = parseToMap(notation, color);
		return new MoveData(notation, variations, color);
	}


	private Map<Piece, Square> parseToMap(String notation, Color color) {
		illegalMove = new IllegalArgumentException(MessageFormat.format(
				"{0} is not valid move notation for {1}",
				notation, color.toString().toLowerCase())
		);
		if (simplePawnMove.matcher(notation).matches())
			return parseSimplePawnMove(notation, color);
		if (simplePieceMove.matcher(notation).matches())
			return parseSimplePieceMove(notation, color);
		if (pawnMove.matcher(notation).matches())
			return parsePawnMove(notation, color);
		if (pieceMove.matcher(notation).matches())
			return parsePieceMove(notation, color);
		if (specifiedPawnMove.matcher(notation).matches())
			return parseSpecifiedPawnMove(notation, color);
		if (specifiedPieceMove.matcher(notation).matches())
			return parseSpecifiedPieceMove(notation, color);
		if (castling.matcher(notation).matches())
			return parseCastling(notation, color);
		throw illegalMove;
	}

	private Map<Piece, Square> parseCastling(String notation, Color color) {
		boolean isCastlingShort = notation.length() == 3;
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
		return new LinkedHashMap<>(Map.of(
				king, board.square(newKingLine, row),
				rook, board.square(newRookLine, row)
		));
	}

	private Map<Piece, Square> parseSimplePawnMove(String notation, Color color) {
		return parseSimplePieceMove('P' + notation, color);
	}

	private Map<Piece, Square> parseSimplePieceMove(String notation, Color color) {
		Map<Piece, Square> moves = new HashMap<>();
		final char line = notation.charAt(1);
		final int row = notation.charAt(2) - '0';
		final var square = board.square(line, row);
		final var pieceType = getPieceType(notation);
		board.getPiecesMovingTo(line, row, pieceType, color).forEach(e -> moves.put(e, square));
		return moves;
	}

	private Map<Piece, Square> parsePawnMove(String notation, Color color) {
		return parsePieceMove("P" + notation, color);
	}

	private Map<Piece, Square> parsePieceMove(String notation, Color color) {
		char endLine = notation.charAt(1);
		int endRow = notation.charAt(2) - '0';
		char startSpecifier = notation.charAt(0);
		boolean lookingForRow = Character.isDigit(startSpecifier);
		char startLine = (lookingForRow) ? endLine : startSpecifier;
		int startRow = (lookingForRow) ? startSpecifier - '0' : endRow;
		var pieceType = getPieceType(notation);
		List<? extends Piece> pieces = board.getPiecesMovingTo(endLine, endRow, pieceType, color);
		Square destination = board.square(endLine, endRow);
		return pieces.stream().filter(e -> {
			char[] location = e.getLocation();
			return location[0] == startLine && location[1] == startRow;
		}).collect(Collectors.toMap(e -> e, e -> destination, (a, b) -> b));
	}

	private Map<Piece, Square> parseSpecifiedPawnMove(String notation, Color color) {
		return parseSpecifiedPieceMove('P' + notation, color);
	}

	private Map<Piece, Square> parseSpecifiedPieceMove(String notation, Color color) {
		final var pieceType = getPieceType(notation);
		final char startLine = notation.charAt(1);
		final int startRow = notation.charAt(2) - '0';
		final char endLine = notation.charAt(3);
		final int endRow = notation.charAt(4) - '0';
		final var startSquare = board.square(startLine, startRow);
		final var endSquare = board.square(endLine, endRow);
		var piece = startSquare.getPiece();
		if (piece == null || piece.getClass() != pieceType || piece.getColor() != color)
			return Map.of();
		return new HashMap<>(Map.of(piece, endSquare));
	}

	@NotNull
	private Class<? extends Piece> getPieceType(String notation) {
		return switch (notation.charAt(0)) {
			case 'N', 'S' -> Knight.class;
			case 'B', 'G' -> Bishop.class;
			case 'R', 'W' -> Rook.class;
			case 'Q', 'H' -> Queen.class;
			case 'K' -> King.class;
			default -> throw new IllegalStateException("Unexpected value: " + notation.charAt(0));
		};
	}
}
