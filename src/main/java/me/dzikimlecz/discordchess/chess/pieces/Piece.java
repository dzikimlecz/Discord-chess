package me.dzikimlecz.discordchess.chess.pieces;

import me.dzikimlecz.discordchess.chess.Color;
import me.dzikimlecz.discordchess.chess.board.Square;

public abstract class Piece {
	
	private final Color color;
	private Square currentLocation;
	
	public Piece(Color color, Square startLocation) {
		this.color = color;
		currentLocation = startLocation;
	}
	
	public boolean moveTo(Square square) {
		boolean pieceMoved = square.putPiece(this);
		if(pieceMoved) currentLocation = square;
		return pieceMoved;
	}
	
	public Color getColor() {
		return color;
	}
}
