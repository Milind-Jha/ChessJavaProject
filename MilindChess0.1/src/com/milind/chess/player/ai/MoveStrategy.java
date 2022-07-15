package com.milind.chess.player.ai;

import com.milind.chess.board.Board;
import com.milind.chess.board.Move;

public interface MoveStrategy {

    Move execute(Board board);

}
