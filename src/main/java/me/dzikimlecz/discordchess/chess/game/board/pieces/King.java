package me.dzikimlecz.discordchess.chess.game.board.pieces;

import me.dzikimlecz.discordchess.chess.game.board.Color;
import me.dzikimlecz.discordchess.chess.game.board.Square;


/**
 * Class representing King chess piece.
 * Not takeable
 * @see Piece
 */

public class King extends Piece {
	
	public King(Color color, Square startLocation) {
		super(color, startLocation);
	}
	
	@Override
	public String toString() {
		return "K";
	}

	@Override
	public void moveTo(Square square) {
		super.moveTo(square);
		final int startingRow = currentLocation.getRow();
		final char startingLine = currentLocation.getLine();
		for (int rowCursor = startingRow - 1;
		     rowCursor <= 'h' && rowCursor <= startingRow + 1; rowCursor++) {
			for (int lineCursor = startingLine - 1;
			     lineCursor <= 8 && lineCursor <= startingLine + 1; lineCursor++) {
				if(startingRow < 1 || startingLine < 'a') continue;
				deltas.add(new int[]{rowCursor - startingRow, lineCursor - startingLine});
			}
		}
	}
}
