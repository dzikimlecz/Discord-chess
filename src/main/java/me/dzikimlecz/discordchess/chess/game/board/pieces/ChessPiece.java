package me.dzikimlecz.discordchess.chess.game.board.pieces;

import me.dzikimlecz.discordchess.chess.game.board.Color;
import me.dzikimlecz.discordchess.chess.game.board.Square;

public abstract class ChessPiece {

	protected final Color color;

	public ChessPiece(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}
}
