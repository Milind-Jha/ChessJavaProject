package com.milind.chess.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.milind.chess.alliance.Alliance;
import com.milind.chess.board.Board;
import com.milind.chess.board.Move;
import com.milind.chess.peices.King;
import com.milind.chess.peices.Piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player {
    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    Player(Board board, Collection<Move> playerLegalMoves, Collection<Move> opponentLegalMoves){
        this.board=board;
        this.playerKing= establishKing();
        this.legalMoves= ImmutableList.copyOf(Iterables.concat(playerLegalMoves,
                calculateKingCastles(playerLegalMoves,opponentLegalMoves)));
        this.isInCheck= !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(),
                        opponentLegalMoves).isEmpty();
    }

    public King getPlayerKing(){
        return this.playerKing;
    }

    public Collection<Move> getLegalMoves(){
        return this.legalMoves;
    }

    protected static Collection<Move> calculateAttacksOnTile(int piecePosition,
                                                           Collection<Move> opponentLegalMoves) {
        final List<Move> attackMoves = new ArrayList<>();
        for(final Move move:opponentLegalMoves){
            if (piecePosition == move.getDestinationCoordinate()){
                attackMoves.add(move);
            }
        }
        return ImmutableList.copyOf(attackMoves);
    }

    private King establishKing(){
        for (Piece piece: getActivePieces()) {
            if (piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }
        throw new RuntimeException("Invalid Chess Board");
    }

    public boolean isMoveLegal(Move move){

        return this.legalMoves.contains(move);
    }

    public boolean isCheck(){
        return this.isInCheck;
    }

    //TO DO
    public boolean isCheckMate(){
        return this.isInCheck && !hasEscapeMoves();
    }

    protected  boolean hasEscapeMoves(){
        for (final Move move : this.legalMoves ){
            final MoveTransition transition = makeMove(move);   // make move in imag board and check if it is ok
            if(transition.getMoveStatus().isDone()){
                return true;
            }
        }
        return false;
    }

    public boolean isStaleMate(){
        return !this.isInCheck && !hasEscapeMoves();
    }
    public boolean isCastled(){
        return false;
    }
    public MoveTransition makeMove(Move move){

        if(!isMoveLegal(move)){
            for(Move m : legalMoves){
                System.out.println(m);
            }
            return new MoveTransition(this.board,move,MoveStatus.ILLEGAL_MOVE);

        }

        final Board transitionBoard = move.execute();
        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(transitionBoard.getCurrentPlayer()
                                            .getOpponent().getPlayerKing().getPiecePosition(),
                                            transitionBoard.getCurrentPlayer().getLegalMoves());
        if(!kingAttacks.isEmpty()){
            return new MoveTransition(this.board,move,MoveStatus.MOVE_CREATE_CHECK);
        }
        return new MoveTransition(transitionBoard,move,MoveStatus.DONE);
    }


    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals,
                                                             Collection<Move> opponontLegals);

}
