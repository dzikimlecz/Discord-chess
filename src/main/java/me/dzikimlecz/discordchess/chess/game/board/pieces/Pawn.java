package me.dzikimlecz.discordchess.chess.game.board.pieces;

import me.dzikimlecz.discordchess.chess.game.board.Color;
import me.dzikimlecz.discordchess.chess.game.board.Square;



/**
 * Class representing bishop chess piece.
 * @see Piece
 * @see Takeable
 */
public final class Pawn extends Piece implements Takeable {
	
	public Pawn(Color color, Square startLocation) {
		super(color, startLocation);
	}
	
	@Override
	public String toString() {
		return "P";
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
		int lineDelta = (color == Color.WHITE) ? 1 : -1;
		char colorStartLine = (color == Color.WHITE) ? 'b' : 'g';
		deltas.add(new int[]{startingLine + lineDelta, startingRow});
		deltas.add(new int[]{startingLine + lineDelta, startingRow + 1});
		deltas.add(new int[]{startingLine + lineDelta, startingRow - 1});
		if (colorStartLine == startingLine)
			deltas.add(new int[]{startingLine + (2 * lineDelta), startingRow});
	}
}
