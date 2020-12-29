package me.dzikimlecz.chessapi.game.board.pieces;

import me.dzikimlecz.chessapi.game.board.Square;
import me.dzikimlecz.chessapi.game.board.Color;


/**
 * Class representing bishop chess piece.
 * @see Piece
 * @see Takeable
 */
public final class Rook extends TakeablePiece {
	
	public Rook(Color color, Square startLocation) {
		super(color, startLocation);
	}
	
	@Override
	public String toString() {
		return "R";
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
			if (row != startingRow)
				deltas.add(new int[] {row - startingRow, startingLine});
		//squares on the same line
		for (char line = 'a'; line <= 'h'; line++)
			if (line != startingLine)
				deltas.add(new int[] {startingRow, line - startingLine});
	}
}
