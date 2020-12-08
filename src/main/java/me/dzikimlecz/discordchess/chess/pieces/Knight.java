package me.dzikimlecz.discordchess.chess.pieces;

import me.dzikimlecz.discordchess.chess.Color;
import me.dzikimlecz.discordchess.chess.board.Square;

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
	
	}
}
