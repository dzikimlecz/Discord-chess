package me.dzikimlecz.discordchess.chess.game.board.pieces;

import me.dzikimlecz.discordchess.chess.game.board.Color;
import me.dzikimlecz.discordchess.chess.game.board.Square;

public abstract class Piece extends ChessPiece {
	protected Square currentLocation;
	
	public Piece(Color color, Square startLocation) {
		super(color);
		currentLocation = startLocation;
	}
	
	public void moveTo(Square square) {
		boolean pieceMoved = square.putPiece(this);
		if(pieceMoved) currentLocation = square;
	}

	public char[] getLocation() {
		return new char[] {currentLocation.getLine(), (char) currentLocation.getRow()};
	}

	public Square getSquare() {
		return currentLocation;
	}


}
