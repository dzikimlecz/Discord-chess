package me.dzikimlecz.discordchess.chess;

import me.dzikimlecz.discordchess.chess.board.Board;

public class Chess {
	
	private Color turn;
	private Board board;
	
	public void move(String notation) throws Exception {
		var moveData = parse(notation);
		
	}
	
	private MoveVariations parse(String notation) throws Exception {
		return new MoveVariations(notation, board, turn);
	}


}
