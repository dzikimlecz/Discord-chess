package me.dzikimlecz.chessapi.game.moveanalysing;

import me.dzikimlecz.chessapi.game.board.pieces.Pawn;
import me.dzikimlecz.chessapi.game.board.pieces.Piece;
import me.dzikimlecz.chessapi.game.moveparsing.IMoveValidator;
import me.dzikimlecz.chessapi.game.movestoring.MoveData;
import me.dzikimlecz.chessapi.game.movestoring.MoveDatabase;

public class EnPassantCastlingValidator implements IMoveValidator {
	private final MoveDatabase moveDatabase;

	public EnPassantCastlingValidator(MoveDatabase moveDatabase) {
		this.moveDatabase = moveDatabase;
	}

	@Override
	public MoveData validate(MoveData data) {
		if (data.toFurtherCheck())
			if (data.doingCastling()) validateCastling(data);
			else validateEnPassant(data);
		return data;
	}

	public void validateEnPassant(MoveData data) {
		var opponentColor = data.getColor().opposite();
		var variations = data.getVariations();
		var lastMove = moveDatabase.getLastMove(opponentColor);
		var lastMoveVariations = lastMove.getVariations();
		var lastMovePieces = lastMoveVariations.keySet();
		if (lastMovePieces.size() != 1 ||
				variations.size() != 1 ||
				lastMovePieces.stream().noneMatch(piece -> piece instanceof Pawn)) {
			variations.clear();
			return;
		}
		var lastMovedPiece = (Pawn) lastMovePieces.stream().findFirst().get();
		var destinationSquare = variations.get(variations.keySet().stream().findFirst().get());
		if (
				lastMovedPiece.movesCount() != 1
				|| lastMovedPiece.getSquare().getLine() != destinationSquare.getLine()
		) variations.clear();
	}

	public void validateCastling(MoveData data) {
		var moves = moveDatabase.getAllMoves(data.getColor());
		var variations = data.getVariations();
		for (MoveData moveData : moves) {
			for (Piece piece : variations.keySet()) {
				if (moveData.getVariations().containsKey(piece)) {
					variations.clear();
					return;
				}
			}
		}
	}
}
