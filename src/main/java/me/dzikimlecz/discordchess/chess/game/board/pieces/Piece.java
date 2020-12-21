package me.dzikimlecz.discordchess.chess.game.board.pieces;

import me.dzikimlecz.discordchess.chess.game.board.Color;
import me.dzikimlecz.discordchess.chess.game.board.Square;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece extends ChessPiece implements Movable {
	protected Square currentLocation;
	protected List<int[]> deltas;
	
	public Piece(Color color, Square startLocation) {
		super(color);
		deltas = new ArrayList<>();
		moveTo(startLocation);
	}

	public void moveTo(Square square) {
		boolean pieceMoved = square.putPiece(this);
		if (pieceMoved) currentLocation = square;
		deltas.clear();
	}

	public char[] getLocation() {
		return new char[] {currentLocation.getLine(), (char) currentLocation.getRow()};
	}

	public Square getSquare() {
		return currentLocation;
	}

	public List<int[]> getMoveDeltas() {
		return deltas;
	}
}
