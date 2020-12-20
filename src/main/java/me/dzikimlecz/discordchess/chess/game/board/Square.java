package me.dzikimlecz.discordchess.chess.game.board;

import me.dzikimlecz.discordchess.chess.game.board.pieces.Piece;
import org.jetbrains.annotations.Nullable;

public class Square {
	private final byte x;
	private final byte y;
	public final Color color;
	@Nullable private Piece containedPiece;
	
	public Square(byte x, byte y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}
	
	public boolean putPiece(@Nullable Piece piece) {
		if(containedPiece != null) return false;
		containedPiece = piece;
		return true;
	}
	
	@Nullable
	public Piece getPiece() {
		return containedPiece;
	}
	
	public char getLine() {
		return (char) (y + 'a');
	}
	
	public int getRow(){
		return x;
	}
	
	@Override
	public String toString() {
		return containedPiece == null ? " " : containedPiece.toString();
	}
}
