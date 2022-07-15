package com.milind.chess.player.ai;

import com.milind.chess.board.Board;
import com.milind.chess.board.BoardUtil;
import com.milind.chess.board.ChessTile;
import com.milind.chess.board.Move;
import com.milind.chess.peices.Pawn;
import com.milind.chess.peices.Piece;
import com.milind.chess.player.Player;

import java.util.ArrayList;
import java.util.List;

public final class StandardBoardEvluator implements BoardEvaluator { // Not Extendible Class

    private final static int CHECK_MATE_BONUS = 5000;
    private final static int CHECK_BONUS = 110;
    private final static int PASSED_PAWN_BONUS = 100;
    private final static int DOUBLE_CHECK_BONUS = 150;
    private final static int DEVLOPMENT_BONUS = 5;
    private final static int CASTLE_BONUS = 50;

    @Override
    public int evaluate(final Board board,final int depth) {
        return scorePlayer(board,board.whitePlayer(),depth) - scorePlayer(board,board.whitePlayer(),depth);
    }

    private int scorePlayer(final Board board,final  Player player,final int depth) {
        return allPiecesValue(player) + mobility(player) + check(player) +
                checkMate(player,depth) + casteled(player); //+ passedPawn(player);
    }

//    private static int passedPawn(Player player) {
//        List<Integer> pawnTiles = new ArrayList<>();
//        List<Integer> opponentPawnTiles = new ArrayList<>();
//        for (Piece p :player.getActivePieces()){
//            if(p instanceof Pawn){
//                pawnTiles.add(p.getPiecePosition());
//            }
//        }
//        for (Piece p :player.getOpponent().getActivePieces()){
//            if(p instanceof Pawn){
//                opponentPawnTiles.add(p.getPiecePosition());
//            }
//        }
//
//        int passedPawns = 0;
//        int[] directions = {7,8,9};
//        for(int tile:pawnTiles){
//            for (int offset : directions){
//                while (BoardUtil.isValidCoordinate(tile)){
//                    if (opponentPawnTiles.contains(tile+offset))
//                        break;
//                    else
//                        offset+=8;
//                }
//            }
//            passedPawns++;
//        }
//        return 0;
//    }

    private static int casteled(Player player) {
        return player.isCastled()?CASTLE_BONUS:0;
    }

    private static int allPiecesValue(Player player) {
        int pieceValueScore = 0;
        for(final Piece piece: player.getActivePieces()){
            pieceValueScore+=piece.getPieceValue() ;
        }
        return pieceValueScore;
    }

    private static int checkMate(Player player,int depth) {
        return player.getOpponent().isCheckMate()?CHECK_MATE_BONUS-(depth*200):0;
    }

    private static int check(Player player) {
        int attackers = 0;
        if(player.getOpponent().isCheck()){
            for (Move move: player.getLegalMoves()){
                int targetKing = player.getOpponent().getPlayerKing().getPiecePosition();
                if(move.getDestinationCoordinate()==targetKing){
                    attackers++;
                }

            }
            return attackers==1?CHECK_BONUS:DOUBLE_CHECK_BONUS;
        }
        else
            return 0;
    }

    private static int mobility(Player player) {
        return DEVLOPMENT_BONUS*(player.getLegalMoves().size()-player.getOpponent().getLegalMoves().size());
    }
}
