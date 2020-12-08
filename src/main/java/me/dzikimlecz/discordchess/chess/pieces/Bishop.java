package me.dzikimlecz.discordchess.chess.pieces;

import me.dzikimlecz.discordchess.chess.Color;
import me.dzikimlecz.discordchess.chess.board.Square;

public class Bishop extends Piece implements Takeable {
	
	
	public Bishop(Color color, Square startLocation) {
		super(color, startLocation);
	}
	
	@Override
	public String toString() {
		return "B";
	}
	
	@Override
	public void beTaken() {
	
	}
}
