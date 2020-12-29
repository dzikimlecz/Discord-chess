package me.dzikimlecz.chessapi.game.board.pieces;

import me.dzikimlecz.chessapi.game.board.Square;
import me.dzikimlecz.chessapi.game.board.Color;


/**
 * Class representing bishop chess piece.
 * @see Piece
 * @see Takeable
 */
public final class Queen extends TakeablePiece {

	public Queen(Color color, Square startLocation) {
		super(color, startLocation);
	}
	
	@Override
	public String toString() {
		return "Q";
	}

	/**
	 * Updates move deltas, called by super after each move.
	 */
	@Override
	protected void updateDeltas() {
		final int startingRow = currentLocation.getRow();
		final char startingLine = currentLocation.getLine();
		//squares on the same row
		for (int row = 1; row <= 8; row++)
			if(row != startingRow)
				deltas.add(new int[]{row - startingRow, startingLine});
		//squares on the same line
		for (char line = 'a'; line <= 'h'; line++)
			if(line != startingLine)
				deltas.add(new int[]{startingRow, line - startingLine});

		//changes of the coords between squares on diagonals.
		byte[][] diagonalDeltasSets = {{1,1}, {1,-1}, {-1, 1}, {-1, -1}};
		for (byte[] diagonalDeltas : diagonalDeltasSets) {
			byte rowDelta = diagonalDeltas[0];
			byte lineDelta = diagonalDeltas[1];
			int rowCursor = startingRow;
			int lineCursor = startingLine;
			//iterates through all of squares on the diagonal
			while (rowCursor >= 1 && rowCursor <= 8 &&
					lineCursor >= 1 && lineCursor < 8) {
				if (lineCursor != startingLine || rowCursor != startingRow)
					deltas.add(new int[] {lineCursor - startingLine, rowCursor = startingRow});
				rowCursor += rowDelta;
				lineCursor += lineDelta;
			}
		}
	}

}
