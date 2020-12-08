package me.dzikimlecz.discordchess.chess.board;

import me.dzikimlecz.discordchess.chess.Color;
import me.dzikimlecz.discordchess.chess.pieces.*;

import java.text.MessageFormat;
import java.util.*;

import static me.dzikimlecz.discordchess.chess.Color.*;

public class Board {
	private final Square[][] theBoard;
	
	public Board() {
		this.theBoard = new Square[8][8];
		//initializes all squares of the board
		for (byte y = 0; y < 8; y++)
			for (byte x = 0; x < 8; x++)
				theBoard[y][x] = new Square(x, y, Color.values()[(x + y) % 2]);
		//Puts Pawns
		for (int x = 0; x < 8; x++) {
			theBoard[1][x].putPiece(new Pawn(WHITE, theBoard[1][x]));
			theBoard[6][x].putPiece(new Pawn(BLACK, theBoard[6][x]));
		}
		//Puts ever piece on its place
		//idk may be optimised i have no ideas.
		List.of(theBoard[0][0], theBoard[0][7])
				.forEach(square -> square.putPiece(new Rook(WHITE, square)));
		List.of(theBoard[7][0], theBoard[7][7])
				.forEach(square -> square.putPiece(new Rook(BLACK, square)));
		List.of(theBoard[0][1], theBoard[0][6])
				.forEach(square -> square.putPiece(new Knight(WHITE, square)));
		List.of(theBoard[7][1], theBoard[7][6])
				.forEach(square -> square.putPiece(new Knight(BLACK, square)));
		List.of(theBoard[0][2], theBoard[0][5])
				.forEach(square -> square.putPiece(new Bishop(WHITE, square)));
		List.of(theBoard[7][2], theBoard[7][5])
				.forEach(square -> square.putPiece(new Bishop(BLACK, square)));
		theBoard[0][3].putPiece(new Queen(WHITE, theBoard[0][3]));
		theBoard[7][3].putPiece(new Queen(BLACK, theBoard[7][3]));
		theBoard[0][4].putPiece(new King(WHITE, theBoard[7][3]));
		theBoard[7][4].putPiece(new King(BLACK, theBoard[7][4]));
	}

	//may be changed i used it for debugging
	@Override
	public String toString() {
		return Arrays.deepToString(theBoard);
	}

	/**
	 * parses from chess notation to coordinates in the array of squares (a, 1 -> 0, 0)
	 * @param row
	 * @param line
	 * @return
	 */
	private int[] parseCoords(int row, char line) {
		return new int[]{row-1, line - 'a'};
	}

	public List<Pawn> getPawnsFromLine(char line, Color color) throws IllegalAccessException {
		List<Pawn> pawns = new ArrayList<>();
		final int x = parseCoords(0, line)[1];
		for (int y = 0; y < 7; y++) {
			var piece = theBoard[y][x].getPiece();
			if (piece instanceof Pawn && piece.getColor() == color)
				 pawns.add((Pawn) piece);
		}
		return List.copyOf(pawns);
	}

	/**
	 * gets square on specified chess notation
	 * @param line
	 * @param row
	 * @return
	 */
	public Square square(char line, int row) {
		int[] coords = parseCoords(row, line);
		return theBoard[coords[0]][coords[1]];
	}

	public List<Bishop> getBishopsOnDiagonals(char line, int row, Color color) {
		List<Bishop> bishops = new ArrayList<>();
		int[] coords = parseCoords(row, line);
		final int startingY = coords[0];
		final int startingX = coords[1];
		int yCursor = startingY;
		int xCursor = startingX;
		byte[] yDeltas = {1, 1, -1, -1};
		byte[] xDeltas = {1, -1, 1, -1};
		for (int i = 0; i < 4; i++) {
			byte yDelta = yDeltas[i];
			byte xDelta = xDeltas[i];
			while (yCursor >= 0 && yCursor < theBoard.length &&
					xCursor >= 0 && xCursor < theBoard[0].length) {
				Piece selected = theBoard[yCursor][xCursor].getPiece();
				if (selected instanceof Bishop && selected.getColor() == color)
					bishops.add((Bishop) selected);
				yCursor += yDelta;
				xCursor += xDelta;
			}
		}
		return List.copyOf(bishops);
	}

	public Optional<King> getKingNearby(char line, int row) {
		var center = parseCoords(row, line);
		final int centerY = center[0];
		final int centerX = center[1];
		for (int yDelta = -1; yDelta <= 1; yDelta++) {
			int y = centerY + yDelta;
			for (int xDelta = -1; xDelta <= 1; xDelta++) {
				int x = centerX + xDelta;
				Piece piece = theBoard[y][x].getPiece();
				if (piece instanceof King) return Optional.of((King) piece);
			}
		}
		return Optional.empty();
	}

	public List<Knight> getKnightsAttacking(char line, int row, Color color) {
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

	public List<Queen> getQueenFromLineOrDiagonal(char line, int row, Color color) {
		List<Queen> queens = new ArrayList<>();
		int[] coords = parseCoords(row, line);
		final int startingY = coords[0];
		final int startingX = coords[1];
		int yCursor = startingY;
		int xCursor = startingX;
		byte[] yDeltas = {1, 1, -1, -1};
		byte[] xDeltas = {1, -1, 1, -1};
		for (int i = 0; i < 4; i++) {
			byte yDelta = yDeltas[i];
			byte xDelta = xDeltas[i];
			while (yCursor >= 0 && yCursor < theBoard.length &&
					xCursor >= 0 && xCursor < theBoard[0].length) {
				var piece = theBoard[yCursor][xCursor].getPiece();
				if (piece instanceof Queen && piece.getColor() == color)
					queens.add((Queen) piece);
				yCursor += yDelta;
				xCursor += xDelta;
			}
		}
		for(xCursor = 0; xCursor < theBoard[0].length; xCursor++) {
			var piece = theBoard[startingY][xCursor].getPiece();
			if (piece instanceof Queen && piece.getColor() == color)
				queens.add((Queen) piece);
		}
		for(yCursor = 0; yCursor < theBoard.length; yCursor++) {
			var piece = theBoard[yCursor][startingX].getPiece();
			if (piece instanceof Queen && piece.getColor() == color)
				queens.add((Queen) piece);
		}
		return List.copyOf(queens);

	}

	public List<Rook> getRooksOnLines(char line, int row, Color color) {
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
}
