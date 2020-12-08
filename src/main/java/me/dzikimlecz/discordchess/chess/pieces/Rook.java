package me.dzikimlecz.discordchess.chess.pieces;

import me.dzikimlecz.discordchess.chess.Color;
import me.dzikimlecz.discordchess.chess.board.Square;

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
	
	}
}
