package me.dzikimlecz.discordchess.chess.game.board.pieces;

import me.dzikimlecz.discordchess.chess.game.board.Color;
import me.dzikimlecz.discordchess.chess.game.board.Square;

import java.util.stream.IntStream;


/**
 * Class representing bishop chess piece.
 * @see Piece
 * @see Takeable
 */

public class Knight extends Piece implements Takeable {
	
	public Knight(Color color, Square startLocation) {
		super(color, startLocation);
	}
	
	@Override
	public String toString() {
		return "N";
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
		final int startingLine = currentLocation.getLine();
		byte[][] deltasSets =
				{{-2, 1}, {-1, 2}, {1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1}};
		for (byte[] deltas: deltasSets) {
			byte rowDelta = deltas[0];
			byte lineDelta = deltas[1];
			int newRow = startingRow + rowDelta;
			int newLine = startingLine + lineDelta;
			if (newLine >= 'a' && newLine <= 'h' && newRow >= '1' && newRow <= '8')
				this.deltas.add(new int[] {newLine - startingLine, newRow - startingRow});
		}
	}
}
