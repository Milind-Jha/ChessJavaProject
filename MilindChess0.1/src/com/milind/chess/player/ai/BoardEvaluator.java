package com.milind.chess.player.ai;

import com.milind.chess.board.Board;

public interface BoardEvaluator {
    int evaluate(Board board, int depth);
}
