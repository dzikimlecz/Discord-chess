package me.dzikimlecz.discordchess.chess.game.board.pieces;

import me.dzikimlecz.discordchess.chess.game.board.Color;
import me.dzikimlecz.discordchess.chess.game.board.Square;



/**
 * Class representing bishop chess piece.
 * @see Piece
 * @see Takeable
 */
public class Pawn extends Piece implements Takeable {
	
	public Pawn(Color color, Square startLocation) {
		super(color, startLocation);
	}
	
	@Override
	public String toString() {
		return "P";
	}

	@Override
	public void beTaken() {

	}
}
