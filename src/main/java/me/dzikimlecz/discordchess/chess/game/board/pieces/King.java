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
}
