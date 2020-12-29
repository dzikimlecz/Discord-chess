package me.dzikimlecz.chessapi.game.moveanalysing;

import me.dzikimlecz.chessapi.game.board.pieces.*;
import me.dzikimlecz.chessapi.DrawReason;
import me.dzikimlecz.chessapi.game.board.Board;
import me.dzikimlecz.chessapi.game.board.Color;
import me.dzikimlecz.chessapi.game.board.Square;
import me.dzikimlecz.chessapi.game.moveparsing.IMoveValidator;
import me.dzikimlecz.chessapi.game.movestoring.GamesData;
import me.dzikimlecz.chessapi.game.movestoring.MoveData;
import me.dzikimlecz.chessapi.game.movestoring.MoveDatabase;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.dzikimlecz.chessapi.game.board.Color.BLACK;
import static me.dzikimlecz.chessapi.game.board.Color.WHITE;

public class DrawAnalyser {
	private final MoveDatabase moveDatabase;
	private final GamesData gamesData;
	private final IMoveValidator validator;
	private Board board;
	private List<MoveData> whiteMoves;
	private List<MoveData> blackMoves;

	public DrawAnalyser(MoveDatabase moveDatabase, GamesData gamesData, IMoveValidator validator) {
		this.moveDatabase = moveDatabase;
		this.gamesData = gamesData;
		this.validator = validator;
	}

	@Nullable
	public DrawReason lookForDraw() {
		this.board = gamesData.getBoard();
		whiteMoves = moveDatabase.getAllMoves(WHITE);
		blackMoves = moveDatabase.getAllMoves(BLACK);
		if (noMovesWithPawnDuring50Moves()) return DrawReason.FIFTY_MOVES_WITHOUT_PAWN;
		if (triplePositionRepeat()) return DrawReason.TRIPLE_POSITION_REPEAT;
		if (staleMate()) return DrawReason.STALE_MATE;
		if (deadPosition()) return DrawReason.LACK_OF_PIECES;
		return null;
	}

	//fixme 23.12.2020: It's a workaround, checks if three LAST positions were the same based on
	// move notations, it doesn't fit chess rules and need to be fixed. I didn't have any other
	// ideas than just store state of the board in MoveDatabase but i guess it would be
	// memory-consuming. Will be fixed after memory tests and optimization.
	private boolean triplePositionRepeat() {
		var whites = whiteMoves.subList(whiteMoves.size() - 3, whiteMoves.size());
		var blacks = blackMoves.subList(blackMoves.size() - 3, blackMoves.size());
		return whites.stream().distinct().count() == 1 && blacks.stream().distinct().count() == 1;
	}

	private boolean staleMate() {
		if (whiteMoves.size() < 10) return false;
		Color color = gamesData.getColor();
		List<Piece> pieces = new ArrayList<>();
		for (int row = 1; row <= 8; row++) {
			for (char line = 'a'; line <= 'h'; line++) {
				var piece = board.getSquare(line, row).getPiece();
				if (piece != null && piece.getColor() == color)
					pieces.add(piece);
			}
		}
		Map<Piece, Square> possibleResponses = new HashMap<>();
		for (Piece piece : pieces) {
			var pieceSquare = piece.getSquare();
			var moveDeltas = piece.getMoveDeltas();
			for (int[] moveDelta : moveDeltas)
				possibleResponses.put(piece, board.getSquareByDelta(pieceSquare, moveDelta));
		}
		var responseData = new MoveData("#stalemate", possibleResponses, color);
		return validator.validate(responseData).getVariations().isEmpty();
	}

	private boolean noMovesWithPawnDuring50Moves() {
		return moveDatabase.movesWithoutPawnCount() >= 50;
	}

	private boolean deadPosition() {
		List<Piece> whitePieces = new ArrayList<>();
		List<Piece> blackPieces = new ArrayList<>();
		for (int row = 1; row <= 8; row++) {
			for (char line = 'a'; line <= 'h'; line++) {
				var piece = board.getSquare(line, row).getPiece();
				if (piece != null) {
					List<Piece> list = (piece.getColor() == WHITE) ? whitePieces : blackPieces;
					list.add(piece);
				}
			}
		}

		if (anyRooksOrQueens(whitePieces) || anyRooksOrQueens(blackPieces))
			return false;
		var pawnsBlocked = pawnsBlocked(whitePieces) && pawnsBlocked(blackPieces);
		if (!pawnsBlocked) return false;
		if (blackPieces.stream().dropWhile(piece -> piece instanceof Pawn).count() == 1)
			return true;
		if (whitePieces.stream().dropWhile(piece -> piece instanceof Pawn).count() == 1)
			return true;
		return deadPieceSet(whitePieces, blackPieces);
	}

	private boolean deadPieceSet(List<Piece> whites, List<Piece> blacks) {
		if (whites.size() == blacks.size()) {
			if (whites.size() == 1) return true;
			if (whites.size() == 2) {
				Optional<Piece> whiteBishopOpt =
						whites.stream().filter(piece -> piece instanceof Bishop).findAny();
				if (whiteBishopOpt.isEmpty()) return false;
				Optional<Piece> blackBishopOpt =
						blacks.stream().filter(piece -> piece instanceof Bishop).findAny();
				if (blackBishopOpt.isEmpty()) return false;
				return blackBishopOpt.get().getColor() == whiteBishopOpt.get().getColor();
			}
			return false;
		}
		List<Piece> notSingletonList;
		if (whites.size() == 1) notSingletonList = blacks;
		else if (blacks.size() == 1) notSingletonList = whites;
		else return false;

		return notSingletonList.stream()
				.allMatch(piece -> piece instanceof Knight ||
						piece instanceof Bishop || piece instanceof King);
	}

	private boolean pawnsBlocked(List<Piece> pieces) {
		return pieces.stream()
				.filter(piece -> piece instanceof Pawn)
				.allMatch(pawn -> {
					Map<Piece, Square> variations = new HashMap<>();
					pawn.getMoveDeltas()
							.forEach(delta -> variations.put(
									pawn, board.getSquareByDelta(pawn.getSquare(), delta))
							);
					return validator.validate(
							new MoveData("#deadposition", variations, pawn.getColor()))
							.getVariations().isEmpty();
				});
	}

	private boolean anyRooksOrQueens(List<Piece> pieces) {
		return pieces.stream().anyMatch(piece -> piece instanceof Rook || piece instanceof Queen);
	}
}
