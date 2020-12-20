package me.dzikimlecz.discordchess.chess.game.moveparsing;

import me.dzikimlecz.discordchess.chess.game.movestoring.MoveData;

public interface IMoveValidator {
	MoveData validate(MoveData moveData);

}
