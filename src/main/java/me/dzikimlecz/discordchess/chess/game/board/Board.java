package me.dzikimlecz.discordchess.chess.game.board;

import me.dzikimlecz.discordchess.chess.game.board.pieces.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static me.dzikimlecz.discordchess.chess.game.board.Color.BLACK;
import static me.dzikimlecz.discordchess.chess.game.board.Color.WHITE;

public class Board {
	private final Square[][] theBoard;
	private final King whiteKing;
	private final King blackKing;


	public Board() {
		this.theBoard = new Square[8][8];
		//initializes all squares of the board
		for (byte y = 0; y < 8; y++)
			for (byte x = 0; x < 8; x++)
				theBoard[y][x] = new Square(x, y, Color.values()[(x + y) % 2]);
		//Puts Pawns
		for (int x = 0; x < 8; x++) {
			new Pawn(WHITE, theBoard[1][x]);
			new Pawn(BLACK, theBoard[6][x]);
		}
		//Puts every piece on its place
		List.of(theBoard[0][0], theBoard[0][7])
				.forEach(square -> new Rook(WHITE, square));
		List.of(theBoard[7][0], theBoard[7][7])
				.forEach(square -> new Rook(BLACK, square));
		List.of(theBoard[0][1], theBoard[0][6])
				.forEach(square -> new Knight(WHITE, square));
		List.of(theBoard[7][1], theBoard[7][6])
				.forEach(square -> new Knight(BLACK, square));
		List.of(theBoard[0][2], theBoard[0][5])
				.forEach(square -> new Bishop(WHITE, square));
		List.of(theBoard[7][2], theBoard[7][5])
				.forEach(square -> new Bishop(BLACK, square));
		new Queen(WHITE, theBoard[0][3]);
		new Queen(BLACK, theBoard[7][3]);
		whiteKing = new King(WHITE, theBoard[7][3]);
		blackKing = new King(BLACK, theBoard[7][4]);
	}

	/**
	 * parses from chess notation to coordinates in the array of squares e.g. (a, 1 -> 0, 0)
	 * @param row row of the board (1-8)
	 * @param line line of the board (a-h)
	 * @return array mapping both row and line to ints in closed range from 0 to 7
	 */
	private int[] parseCoords(int row, char line) {
		return new int[]{row-1, line - 'a'};
	}

	/**
	 * gets square on specified chess notation
	 * @param line line of the board (a-h)
	 * @param row row of the board (1-8)
	 * @return square on the specified location
	 */
	public Square square(char line, int row) {
		int[] coords = parseCoords(row, line);
		return theBoard[coords[0]][coords[1]];
	}


	/**
	 * Gets list of all squares lying in space between other 2 (exclusive of them).<br>
	 * MAY PRODUCE BUGS WHEN SQUARES ARE NOT ON THE SAME LINE OR DIAGONAL - IT'S MISUSE
	 * @param square start of the space to be returned.
	 * @param square1 end of the space to be returned.
	 * @return all squares between {@code square} and {@code square1}
	 */
	public List<Square> squaresBetween(Square square, Square square1) {
		return squaresBetween(square, square1, false);
	}

	/**
	 * Gets list of all squares lying in space between other 2.<br>
	 * MAY PRODUCE BUGS WHEN SQUARES ARE NOT ON THE SAME LINE OR DIAGONAL - IT'S MISUSE
	 * @param square start of the space to be returned.
	 * @param square1 end of the space to be returned.
	 * @param inclusive are the squares on the edges supposed to be included.
	 * @return all squares between {@code square} and {@code square1}
	 */
	public List<Square> squaresBetween(Square square, Square square1, boolean inclusive) {
		int lesserChange = inclusive ? 0 : 1;
		int biggerChange = inclusive ? 1 : 0;
		final int lesserY = Math.min(square.getRow(), square1.getRow()) + lesserChange;
		final int biggerY = Math.max(square.getRow(), square1.getRow()) + biggerChange;
		final char lesserX = (char) (Math.min(square.getLine(), square1.getLine()) + lesserChange);
		final char biggerX = (char) (Math.max(square.getLine(), square1.getLine()) + biggerChange);

		List<Square> squares = new ArrayList<>();
		for (int y = lesserY; y < biggerY; y++)
			for (char x = lesserX; x < biggerX; x++)
				squares.add(this.square(x, y));

		return List.copyOf(squares);
	}

	public List<? extends Piece> getPiecesMovingTo(char line,
	                                               int row,
	                                               @Nullable Class<? extends Piece> type,
	                                               @NotNull Color color) {
		List<Piece> pieces = new ArrayList<>();
		if (type != null) {
			Arrays.stream(theBoard).forEach(boardRow -> Arrays.stream(boardRow)
					.map(Square::getPiece)
					.filter(Objects::nonNull)
					.filter(piece -> piece.getColor() == color && piece.getClass() == type)
					.filter(piece -> piece.getMoveDeltas().stream().anyMatch(set -> {
						        boolean lineMatch = piece.getLocation()[0] + set[0] == line;
						        boolean rowMatch = piece.getLocation()[1] + set[1] == row;
						        return lineMatch && rowMatch;
					        })
					).forEach(pieces::add));
		} else {
			List.of(
					Knight.class,
					Bishop.class,
					Rook.class,
					Queen.class
			).forEach(clazz -> pieces.addAll(getPiecesMovingTo(line, row, clazz, color)));
		}

		return List.copyOf(pieces);
	}


	private List<Pawn> getPawnsNearby(char line, int row, Color color) {
		List<Pawn> pawns = new ArrayList<>();
		int[] coords = parseCoords(row, line);
		final int startingY = coords[0];
		final int startingX = coords[1];
		int yCursor = startingY;
		int xCursor = startingX;
		byte[] yDeltas = {1, 1, 1, 2};
		byte[] xDeltas = {0, -1, 1, 0};
		for (int i = 0; i < yDeltas.length; i++) {
			byte yDelta = yDeltas[i];
			byte xDelta = xDeltas[i];
			while (yCursor >= 0 && yCursor < theBoard.length &&
					xCursor >= 0 && xCursor < theBoard[0].length) {
				Piece selected = theBoard[yCursor][xCursor].getPiece();
				if (selected instanceof Pawn && selected.getColor() == color)
					pawns.add((Pawn) selected);
				yCursor += yDelta;
				xCursor += xDelta;
			}
		}
		return List.copyOf(pawns);
	}

	private List<Bishop> getBishopsOnDiagonals(char line, int row, Color color) {
		List<Bishop> bishops = new ArrayList<>();
		int[] coords = parseCoords(row, line);
		final int startingY = coords[0];
		final int startingX = coords[1];
		byte[][] diagonalDeltasSets = {{1,1}, {1,-1}, {-1, 1}, {-1, -1}};
		for (byte[] deltas : diagonalDeltasSets) {
			byte yDelta = deltas[0];
			byte xDelta = deltas[1];
			int yCursor = startingY;
			int xCursor = startingX;
			while (yCursor >= 0 && yCursor < theBoard.length &&
					xCursor >= 0 && xCursor < theBoard[0].length) {
				if (xCursor != startingX || yCursor != startingY) {
					var piece = theBoard[yCursor][xCursor].getPiece();
					if (piece instanceof Bishop && piece.getColor() == color)
						bishops.add((Bishop) piece);
				}
				yCursor += yDelta;
				xCursor += xDelta;
			}
		}
		return List.copyOf(bishops);
	}

	private List<King> getKingNearby(char line, int row, Color color) {
		var center = parseCoords(row, line);
		final int centerY = center[0];
		final int centerX = center[1];
		for (int yDelta = -1; yDelta <= 1; yDelta++) {
			int y = centerY + yDelta;
			for (int xDelta = -1; xDelta <= 1; xDelta++) {
				int x = centerX + xDelta;
				Piece piece = theBoard[y][x].getPiece();
				if (piece instanceof King && piece.getColor() == color)
					return List.of((King) piece);
			}
		}
		return List.of();
	}

	private List<Knight> getKnightsAttacking(char line, int row, Color color) {
		List<Knight> knights = new ArrayList<>();
		int[] coords = parseCoords(row, line);
		final int y = coords[0];
		final int x = coords[1];
		byte[] yDeltas = {-2, -1, 1, 2, 2, 1, -1, -2};
		byte[] xDeltas = {1, 2, 2, 1, -1, -2, -2, -1};
		for (int i = 0, length = yDeltas.length; i < length; i++) {
			byte yDelta = yDeltas[i];
			byte xDelta = xDeltas[i];
			Piece piece;
			try {
				piece = theBoard[y + yDelta][x + xDelta].getPiece();
			} catch(ArrayIndexOutOfBoundsException e) {
				continue;
			}
			if (piece instanceof Knight && piece.getColor() == color)
				knights.add((Knight) piece);
		}
		return List.copyOf(knights);
	}

	private List<Queen> getQueenFromLineOrDiagonal(char line, int row, Color color) {
		List<Queen> queens = new ArrayList<>();
		int[] coords = parseCoords(row, line);

		final int startingY = coords[0];
		final int startingX = coords[1];
		byte[][] diagonalDeltasSets = {{1,1}, {1,-1}, {-1, 1}, {-1, -1}};
		for (byte[] deltas : diagonalDeltasSets) {
			byte yDelta = deltas[0];
			byte xDelta = deltas[1];
			int yCursor = startingY;
			int xCursor = startingX;
			while (yCursor >= 0 && yCursor < theBoard.length &&
					xCursor >= 0 && xCursor < theBoard[0].length) {
				if (xCursor != startingX || yCursor != startingY) {
					var piece = theBoard[yCursor][xCursor].getPiece();
					if (piece instanceof Queen && piece.getColor() == color)
						queens.add((Queen) piece);
				}
				yCursor += yDelta;
				xCursor += xDelta;
			}
		}

		for(int xCursor = 0; xCursor < theBoard[0].length; xCursor++) {
			var piece = theBoard[startingY][xCursor].getPiece();
			if (piece instanceof Queen && piece.getColor() == color)
				queens.add((Queen) piece);
		}
		for (Square[] squares : theBoard) {
			var piece = squares[startingX].getPiece();
			if (piece instanceof Queen && piece.getColor() == color)
				queens.add((Queen) piece);
		}
		return List.copyOf(queens);
	}

	private List<Rook> getRooksOnLines(char line, int row, Color color) {
		List<Rook> rooks = new ArrayList<>();
		int[] coords = parseCoords(row, line);
		final int y = coords[0];
		final int x = coords[1];
		for(int xCursor = 0; xCursor < theBoard[0].length; xCursor++) {
			var piece = theBoard[y][xCursor].getPiece();
			if (piece instanceof Rook && piece.getColor() == color)
				rooks.add((Rook) piece);
		}
		for (Square[] squares : theBoard) {
			var piece = squares[x].getPiece();
			if (piece instanceof Rook && piece.getColor() == color)
				rooks.add((Rook) piece);
		}
		return List.copyOf(rooks);
	}

	public King getKing(Color color) {
		return (color == WHITE) ? whiteKing : blackKing;
	}

	//may be changed i used it for debugging
	@Override
	public String toString() {
		return Arrays.deepToString(theBoard);
	}
}