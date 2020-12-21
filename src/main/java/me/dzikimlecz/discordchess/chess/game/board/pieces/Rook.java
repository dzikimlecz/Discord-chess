package me.dzikimlecz.discordchess.chess.game.board.pieces;

import me.dzikimlecz.discordchess.chess.game.board.Color;
import me.dzikimlecz.discordchess.chess.game.board.Square;



/**
 * Class representing bishop chess piece.
 * @see Piece
 * @see Takeable
 */
public class Rook extends Piece implements Takeable {
	
	
	public Rook(Color color, Square startLocation) {
		super(color, startLocation);
	}
	
	@Override
	public String toString() {
		return "R";
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
			if (row != startingRow)
				deltas.add(new int[] {row - startingRow, startingLine});
		for (char line = 'a'; line <= 'h'; line++)
			if (line != startingLine)
				deltas.add(new int[] {startingRow, line - startingLine});
	}
}
