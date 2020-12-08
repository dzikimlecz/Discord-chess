package me.dzikimlecz.discordchess.chess.pieces;

import me.dzikimlecz.discordchess.chess.Color;
import me.dzikimlecz.discordchess.chess.board.Square;

public class Queen extends Piece implements Takeable {
	
	public Queen(Color color, Square startLocation) {
		super(color, startLocation);
	}
	
	@Override
	public String toString() {
		return "Q";
	}
	
	@Override
	public void beTaken() {
	
	}
}
