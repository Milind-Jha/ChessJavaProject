package com.milind.chess.peices;

import com.google.common.collect.ImmutableList;
import com.milind.chess.alliance.Alliance;
import com.milind.chess.board.Board;
import com.milind.chess.board.BoardUtil;
import com.milind.chess.board.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn extends Piece{

    private final int[] MOVEMENT_DIRECTION={8,16,7,9};
    public Pawn(final int position,final Alliance peiceAlliance) {
        super(position, peiceAlliance,PieceType.PAWN,true);
    }
    public Pawn(final int position,final Alliance peiceAlliance,boolean isFirstMove) {
        super(position, peiceAlliance,PieceType.PAWN,isFirstMove);
    }


    @Override
    public boolean isFirstMove() {
        return BoardUtil.SECOND_ROW[this.position]&&this.getPeiceAlliance().isWhite()||
                BoardUtil.SEVENTH_ROW[this.position]&&this.getPeiceAlliance().isBlack();
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        List<Move> legalmoves = new ArrayList<>();
        for (int movementOffset:MOVEMENT_DIRECTION){
            final int destinationPos = this.position+(movementOffset*this.getPeiceAlliance().getDirection()); // -1 for BLACK
            if (!BoardUtil.isValidCoordinate(destinationPos)){
                continue;
            }
            if(movementOffset == 8 && !board.getTile(destinationPos).isTileOccupied()){
                if(this.getPeiceAlliance().isPawnPromotionSquare(destinationPos)){  //QUEENING
                    legalmoves.add(new Move.PawnPromotion((new Move.PawnMove(board,this,destinationPos,this.position))));
                }
                else{
                    legalmoves.add(new Move.PawnMove(board,this,destinationPos,this.position));   //this refers to Pawn object
                }
            }
            else if(movementOffset == 16 && this.isFirstMove() &&
                    (BoardUtil.SEVENTH_ROW[this.position] && this.getPeiceAlliance().isBlack() ||
                    BoardUtil.SECOND_ROW[this.position] && this.getPeiceAlliance().isWhite())) {
                    final int intermidiatePos = this.position + (8*this.getPeiceAlliance().getDirection());
                    if(!board.getTile(intermidiatePos).isTileOccupied() &&
                            !board.getTile(destinationPos).isTileOccupied()){
                        legalmoves.add(new Move.PawnJump(board,this,destinationPos,this.position));
                    }
            }
            else if(movementOffset == 7 &&
                    (!BoardUtil.EIGHTH_COLUMN[this.position] && this.peiceAlliance.isBlack() ||
                    !BoardUtil.FIRST_COLUMN[this.position] && this.peiceAlliance.isWhite())){
                    if (board.getTile(destinationPos).isTileOccupied()){ // destination not empty
                        final Piece pieceOnDestination = board.getTile(destinationPos).getPeice();
                        if((pieceOnDestination!=null)&&
                                pieceOnDestination.getPeiceAlliance()!=this.getPeiceAlliance()){
                            if(this.getPeiceAlliance().isPawnPromotionSquare(destinationPos)){  //QUEENING
                                legalmoves.add(new Move.PawnPromotion((new Move.PawnCapturingMove(board,this,destinationPos,
                                        this.position,pieceOnDestination))));
                            }
                            else {
                                legalmoves.add(new Move.PawnCapturingMove(board,this,destinationPos,
                                        this.position,pieceOnDestination));
                            }
                        }
                    }
                    else if(board.getEnPassantPawn()!=null){       // destination empty i.e. Check for EnPaessant Move
                        if(board.getEnPassantPawn().getPiecePosition() ==
                                (this.getPiecePosition()+this.getPeiceAlliance().getOppositeDirection())){
                            final Piece enPassantPawn = board.getEnPassantPawn();
                            if(this.getPeiceAlliance() != enPassantPawn.getPeiceAlliance()){
                                legalmoves.add(new Move.PawnEnPaessantMove(board,this,destinationPos,
                                this.position,enPassantPawn));
                            }
                        }
                    }
            }
            else if(movementOffset == 9 &&
                    (!BoardUtil.FIRST_COLUMN[this.position] && this.peiceAlliance.isBlack() ||
                     !BoardUtil.EIGHTH_COLUMN[this.position] && this.peiceAlliance.isWhite())){
                    if (board.getTile(destinationPos).isTileOccupied()){
                         final Piece pieceOnDestination = board.getTile(destinationPos).getPeice();
                         if((pieceOnDestination!=null)&&
                            pieceOnDestination.getPeiceAlliance()!=this.getPeiceAlliance()){
                             if(this.getPeiceAlliance().isPawnPromotionSquare(destinationPos)){  //QUEENING
                                 legalmoves.add(new Move.PawnPromotion(new Move.PawnCapturingMove(board,this,destinationPos,
                                         this.position,pieceOnDestination)));
                             }
                             else {
                                 legalmoves.add(new Move.PawnCapturingMove(board,this,destinationPos,
                                         this.position,pieceOnDestination));
                             }

                        }
                    }
                    else if(board.getEnPassantPawn()!=null){       // destination empty i.e. Check for EnPaessant Move
                        if(board.getEnPassantPawn().getPiecePosition() ==
                                (this.getPiecePosition()+this.getPeiceAlliance().getDirection())){
                            final Piece enPassantPawn = board.getEnPassantPawn();
                            if(this.getPeiceAlliance() != enPassantPawn.getPeiceAlliance()){
                                legalmoves.add(new Move.PawnEnPaessantMove(board,this,destinationPos,
                                        this.position,enPassantPawn));
                            }
                        }
                    }
            }
        }
       return ImmutableList.copyOf(legalmoves);
    }

    public Piece getPromotionDecision(){
        return new Queen(this.getPiecePosition(),this.getPeiceAlliance()); // Promoting to queen
    }


    @Override
    public Pawn movePiece(final Move move) {
        return new Pawn(move.getDestinationCoordinate(),move.getPiece().getPeiceAlliance());
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }
}
