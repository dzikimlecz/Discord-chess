package me.dzikimlecz.discordchess.chess;

import me.dzikimlecz.discordchess.chess.board.Board;
import me.dzikimlecz.discordchess.chess.board.Square;
import me.dzikimlecz.discordchess.chess.pieces.*;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MoveVariations {

	//patterns
	private static final Pattern simplePawnMove;
	private static final Pattern simplePieceMove;
	private static final Pattern pawnMove;
	private static final Pattern pieceMove;
	private static final Pattern specifiedPawnMove;
	private static final Pattern specifiedPieceMove;
	private static final Pattern castling;

	static {
		simplePawnMove = Pattern.compile(
				"[a-h][1-8]"
		);
		simplePieceMove = Pattern.compile(
				"[PNSBGRWQHK][a-h][1-8]"
		);
		pawnMove = Pattern.compile(
				"[a-h1-8][a-h][1-8]"
		);
		pieceMove = Pattern.compile(
				"[PNSBGRWQHK][a-h1-8][a-h][1-8]"
		);
		specifiedPawnMove = Pattern.compile(
				"[a-h][1-8][a-h][1-8]"
		);
		specifiedPieceMove = Pattern.compile(
				"[PNSBGRWQHK][a-h][1-8][a-h][1-8]"
		);
		castling = Pattern.compile(
				"[Oo0](-[Oo0]){1,2}"
		);
	}

	private final Map<Piece, Square> semiPossibleMoves;
	private final String notation;
	private final Board board;
	private final Color color;
	private final IllegalArgumentException illegalMove;

	public MoveVariations(String notation, Board board, Color color) {
		this.notation = notation.replaceAll("\\s", "");
		this.board = board;
		this.color = color;
		illegalMove = new IllegalArgumentException(MessageFormat.format(
				"{0} is not valid move notation for {1}",
				notation, color.toString().toLowerCase())
		);

		semiPossibleMoves = parseMove(this.notation);
	}

	private Map<Piece, Square> parseMove(String notation) {
		if (simplePawnMove.matcher(notation).matches()) return parseSimplePawnMove(notation);
		if (simplePieceMove.matcher(notation).matches()) return parseSimplePieceMove(notation);
		if (pawnMove.matcher(notation).matches()) return parsePawnMove(notation);
		if (pieceMove.matcher(notation).matches()) return parsePieceMove(notation);
		if (specifiedPawnMove.matcher(notation).matches()) return parseSpecifiedPawnMove(notation);
		if (specifiedPieceMove.matcher(notation).matches()) return parseSpecifiedPieceMove(notation);
		if (castling.matcher(notation).matches()) return parseCastling(notation);
		throw illegalMove;
	}

	private Map<Piece, Square> parseCastling(String notation) {
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
		return Map.of(
				king, board.square(newKingLine, row),
				rook, board.square(newRookLine, row)
		);
	}

	private Map<Piece, Square> parseSimplePawnMove(String notation) {
		return parseSpecifiedPieceMove('P' + notation);
	}

	private Map<Piece, Square> parseSimplePieceMove(String notation) {
		Map<Piece, Square> moves = new HashMap<>();
		final char line = notation.charAt(1);
		final int row = notation.charAt(2) - '0';
		final var square = board.square(line, row);
		final var pieceType = getPieceType(notation);
		board.getPiecesMovingTo(line, row, pieceType, color).forEach(e -> moves.put(e, square));
		return Map.copyOf(moves);
	}

	private Map<Piece, Square> parsePawnMove(String notation) {
		return parsePieceMove("P" + notation);
	}

	private Map<Piece, Square> parsePieceMove(String notation) {
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
		}).collect(Collectors.toUnmodifiableMap(e -> e, e -> destination, (a, b) -> b));
	}

	private Map<Piece, Square> parseSpecifiedPawnMove(String notation) {
		return parseSpecifiedPieceMove('P' + notation);
	}

	private Map<Piece, Square> parseSpecifiedPieceMove(String notation) {
		final var pieceType = getPieceType(notation);
		final char startLine = notation.charAt(1);
		final int startRow = notation.charAt(2) - '0';
		final char endLine = notation.charAt(3);
		final int endRow = notation.charAt(4) - '0';
		final var startSquare = board.square(startLine, startRow);
		final var endSquare = board.square(endLine, endRow);
		var piece = startSquare.getPiece();
		if (piece == null || piece.getClass() != pieceType) return Map.of();
		return Map.of(piece, endSquare);
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
	
	public String getNotation() {
		return notation;
	}

	public Map<Piece, Square> getSemiPossibleMoves() {
		return semiPossibleMoves;
	}
}
