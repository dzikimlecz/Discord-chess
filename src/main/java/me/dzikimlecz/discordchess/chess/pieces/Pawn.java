package me.dzikimlecz.discordchess.chess.pieces;

import me.dzikimlecz.discordchess.chess.Color;
import me.dzikimlecz.discordchess.chess.board.Square;

public class Pawn extends Piece implements Takeable {
	
	public static final Square NULL_PAWN_SIGNATURE = new Square((byte) -1, (byte) -1, Color.NULL);
	public static final Pawn NULL_PAWN = new Pawn(Color.NULL, NULL_PAWN_SIGNATURE);
	
	
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
