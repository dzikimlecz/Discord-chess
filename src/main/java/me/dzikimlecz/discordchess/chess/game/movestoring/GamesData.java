package me.dzikimlecz.discordchess.chess.game.movestoring;

import me.dzikimlecz.discordchess.chess.game.board.Color;
import me.dzikimlecz.discordchess.chess.game.board.Board;


import static me.dzikimlecz.discordchess.chess.game.board.Color.*;

public class GamesData {
	private Board board;
	private Color color;

	public GamesData() {
		this.color = WHITE;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Board getBoard() {
		return board;
	}

	public Color getColor() {
		return color;
	}
}
