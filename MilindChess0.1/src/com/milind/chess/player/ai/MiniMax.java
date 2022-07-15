package com.milind.chess.player.ai;

import com.milind.chess.board.Board;
import com.milind.chess.board.Move;
import com.milind.chess.player.MoveTransition;

public class MiniMax implements MoveStrategy {

    private final BoardEvaluator boardEvaluator;
    private final int depth;
    public MiniMax(final int depth) {
        this.boardEvaluator = new StandardBoardEvluator();
        this.depth = depth;
    }

    @Override
    public Move execute(Board board) {
        long currentTimeMillis = System.currentTimeMillis();
        Move bestMove = null;
        int maxSeenValue = Integer.MIN_VALUE;
        int minSeenValue = Integer.MAX_VALUE;
        int currentValue;
        System.out.println(board.getCurrentPlayer() + "calculating with depth = "+this.depth);
        int numMoves = board.getCurrentPlayer().getLegalMoves().size();
        for (final Move move: board.getCurrentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                currentValue = board.getCurrentPlayer().getAlliance().isWhite()?
                    min(board,this.depth-1):max(board,this.depth-1);// min calls max for the first time i.e. best for white and vice versa
                if(board.getCurrentPlayer().getAlliance().isWhite() && currentValue>=maxSeenValue){
                        maxSeenValue=currentValue;
                        bestMove = move;
                }
                else if(board.getCurrentPlayer().getAlliance().isBlack() && currentValue<=minSeenValue){
                        minSeenValue = currentValue;
                        bestMove = move;
                }
            }
        }
        long currentTimeMillis1 = System.currentTimeMillis()-currentTimeMillis;
        System.out.println("Calculation done in : "+currentTimeMillis1);
        return bestMove;
    }

    public int min(Board board, int depth) {
        if (depth == 0 || gameOver(board)) {
            return this.boardEvaluator.evaluate(board, depth);
        }
        int low = Integer.MAX_VALUE;
        for (final Move move : board.getCurrentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final int currentValue = max(moveTransition.getTransitionBoard(), depth - 1);
                if (currentValue <= low) {
                    low = currentValue;
                }
            }
        }
        return low;
    }

    public int max(Board board, int depth) {
        if (depth == 0|| gameOver(board)) {
            return this.boardEvaluator.evaluate(board, depth);
        }
        int high = Integer.MIN_VALUE;
        for (final Move move : board.getCurrentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final int currentValue = min(moveTransition.getTransitionBoard(), depth - 1);
                if (currentValue >= high) {
                    high = currentValue;
                }
            }
        }
        return high;
    }

    private static boolean gameOver(Board board) {
        return board.getCurrentPlayer().isCheckMate() || board.getCurrentPlayer().isStaleMate();
    }
}
