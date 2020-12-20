package me.dzikimlecz.discordchess.chess.game.moveparsing;

import me.dzikimlecz.discordchess.chess.game.board.Board;
import me.dzikimlecz.discordchess.chess.game.board.Color;
import me.dzikimlecz.discordchess.chess.game.board.Square;
import me.dzikimlecz.discordchess.chess.game.board.pieces.*;
import me.dzikimlecz.discordchess.chess.game.movestoring.GamesData;
import me.dzikimlecz.discordchess.chess.game.movestoring.MoveData;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.dzikimlecz.discordchess.chess.game.board.Color.BLACK;
import static me.dzikimlecz.discordchess.chess.game.board.Color.WHITE;

public class MoveValidator implements me.dzikimlecz.discordchess.chess.game.moveparsing.IMoveValidator {
	private final GamesData gamesData;

	private Board board;
	private Color color;

	public MoveValidator(GamesData gamesData) {
		this.gamesData = gamesData;
	}

	@Override
	public MoveData validate(MoveData moveData) {
		board = gamesData.getBoard();
		color = gamesData.getColor();
		Map<Piece, Square> moveVariations = moveData.getVariations();
		if (moveData.doingCastling) return validateCastling(moveData);
		for (Piece piece : moveVariations.keySet()) {
			int status = validStatus(piece, moveVariations.get(piece));
			if (status == 0) moveVariations.remove(piece);
			else if (status == -1) moveData.setToFurtherCheck(true);
		}
		return moveData;
	}

	private int validStatus(Piece piece, Square square) {
		if(isSquareOccupied(square, color)) return 0;

		if (!(piece instanceof Knight)
				&& anyPiecesBetween(piece.getSquare(), square))  return 0;

		if (!(piece instanceof King)
				&& isPieceDefendingKing(piece))  return 0;

		if (piece instanceof Pawn) return validatePawnMove((Pawn) piece, square);

		if (piece instanceof King
				&& isSquareAttacked(square))  return 0;

		return 1;
	}

	private boolean isSquareOccupied(Square square, Color color) {
		return (square.getPiece() == null) || (square.getPiece().getColor() != color);
	}

	private boolean isSquareAttacked(Square square) {
		Color oppositeColor = (color == WHITE) ? BLACK : WHITE;
		return board.getPiecesMovingTo(
				square.getLine(),
				square.getRow(),
				null,
				oppositeColor
		).stream().anyMatch(
				opponentPiece -> opponentPiece.getClass() == Knight.class
				|| !anyPiecesBetween(square, opponentPiece.getSquare())
		);
	}

	private boolean isPieceDefendingKing(Piece piece) {
		King king = board.getKing(color);
		Color oppositeColor = (color == WHITE) ? BLACK : WHITE;

		List<Piece> opponentPiecesPinningToKing =
				board.getPiecesMovingTo(
						king.getLocation()[0],
						king.getLocation()[1],
						null,
						oppositeColor
				).stream()
						.filter(opponentPiece -> opponentPiece.getClass() != Knight.class)
						.filter(opponentPiece ->
								        countOfPiecesBetween(
								        		opponentPiece.getSquare(),
										        king.getSquare()
								        ) == 1)
						.collect(Collectors.toList());
		List<Piece> attackingOpponentPieces =
				board.getPiecesMovingTo(
						piece.getLocation()[0],
						piece.getLocation()[1],
						null,
						oppositeColor
				).stream()
						.filter(opponentPiece -> opponentPiece.getClass() != Knight.class)
						.filter(opponentPiece -> !anyPiecesBetween(
								piece.getSquare(),
								opponentPiece.getSquare()))
						.collect(Collectors.toList());

		opponentPiecesPinningToKing.retainAll(attackingOpponentPieces);

		return opponentPiecesPinningToKing.isEmpty();
	}

	private boolean anyPiecesBetween(@NotNull Square square, @NotNull Square square1) {
		return board.squaresBetween(square, square1).stream()
				.anyMatch(square2 -> square2.getPiece() != null);
	}

	private int countOfPiecesBetween(@NotNull Square square, @NotNull Square square1) {
		return (int) board.squaresBetween(square, square1).stream()
				.filter(square2 -> square2.getPiece() != null).count();
	}

	private int validatePawnMove(Pawn pawn, Square square) {
		final Square pawnSquare = pawn.getSquare();
		final int yDelta = (color == WHITE) ? -1 : 1;
		final int startRow = (color == WHITE) ? 2 : 7;
		final int opponentStartRow = (color == BLACK) ? 2 : 7;
		final Square squareBefore = board.square(square.getLine(), square.getRow() + yDelta);
		if (pawnSquare.getLine() != square.getLine()) {
			Piece piece = square.getPiece();
			if(piece instanceof King) return 0;
			if (piece == null) {
				boolean valid = squareBefore.getPiece() instanceof Pawn
						&& square.getRow() == opponentStartRow;
				return valid ? -1 : 0;
			}
		}
		else if (Math.abs(pawnSquare.getRow() - square.getRow()) == 2) {
			boolean valid = squareBefore.getPiece() == null && pawn.getLocation()[1] == startRow;
			if (!valid) return 0;
		}
		return 1;
	}

	private MoveData validateCastling(MoveData moveData) {
		LinkedHashMap<Piece, Square> map = (LinkedHashMap<Piece, Square>) moveData.getVariations();
		King king = (King) map.keySet().toArray()[0];
		Rook rook = (Rook) map.keySet().toArray()[1];
		Square kingSquare = king.getSquare();
		Square rookSquare = rook.getSquare();
		boolean invalid =
				board.squaresBetween(kingSquare, map.get(king), true)
						.stream().anyMatch(this::isSquareAttacked)
				|| anyPiecesBetween(kingSquare, rookSquare);

		if (invalid) map.clear();
		else moveData.setToFurtherCheck(true);
		return moveData;
	}
}
