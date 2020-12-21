package me.dzikimlecz.discordchess.chess.game.board.pieces;

import me.dzikimlecz.discordchess.chess.game.board.Color;
import me.dzikimlecz.discordchess.chess.game.board.Square;

import java.util.ArrayList;
import java.util.List;


/**
 * Class representing bishop chess piece.
 * @see Piece
 * @see Takeable
 */
public final class Queen extends Piece implements Takeable {

	public Queen(Color color, Square startLocation) {
		super(color, startLocation);
	}
	
	@Override
	public String toString() {
		return "Q";
	}

	@Override
	public void beTaken() {
		currentLocation.putPiece(null);
		currentLocation = null;
		deltas = null;
	}

	@Override
	public void moveTo(Square square) {
		super.moveTo(square);

		final int startingRow = currentLocation.getRow();
		final char startingLine = currentLocation.getLine();
		for (int row = 1; row <= 8; row++)
			if(row != startingRow)
				deltas.add(new int[]{row - startingRow, startingLine});
		for (char line = 'a'; line <= 'h'; line++)
			if(line != startingLine)
				deltas.add(new int[]{startingRow, line - startingLine});

		byte[][] diagonalDeltasSets = {{1,1}, {1,-1}, {-1, 1}, {-1, -1}};
		for (byte[] diagonalDeltas : diagonalDeltasSets) {
			byte rowDelta = diagonalDeltas[0];
			byte lineDelta = diagonalDeltas[1];
			int rowCursor = startingRow;
			int lineCursor = startingLine;
			while (rowCursor >= 1 && rowCursor <= 8 &&
					lineCursor >= 1 && lineCursor < 8) {
				if (lineCursor != startingLine || rowCursor != startingRow) {
					deltas.add(new int[] {lineCursor - startingLine, rowCursor = startingRow});
				}
				rowCursor += rowDelta;
				lineCursor += lineDelta;
			}
		}
	}

}
