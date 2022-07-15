package com.milind.chess.peices;

import com.google.common.collect.ImmutableList;
import com.milind.chess.alliance.Alliance;
import com.milind.chess.board.Board;
import com.milind.chess.board.BoardUtil;
import com.milind.chess.board.ChessTile;
import com.milind.chess.board.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class King extends Piece{

    private static final int[] MOVEMENT_DIRECTION={-9,-8,-7,-1,1,7,8,9};
    public King(final int position,final Alliance peiceAlliance) {
        super(position, peiceAlliance,PieceType.KING,true);
    }
    public King(int position, Alliance peiceAlliance,boolean isFirstMove) {
        super(position, peiceAlliance,PieceType.BISHOP,isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalmoves = new ArrayList<>();
        for (int currentOffSet: MOVEMENT_DIRECTION ) {
            final int destinationPos = this.position + currentOffSet;
            if (BoardUtil.isValidCoordinate(destinationPos)){
                if(isFirstColumnExclusion(this.position,currentOffSet)||
                    isEighthColumnExclusion(this.position,currentOffSet)){
                    continue;
                }


                final ChessTile destinationTile = board.getTile(destinationPos);
                if(!destinationTile.isTileOccupied()){  // NO PEICE PRESENT AT DESTINATION
                    legalmoves.add(new Move.NormalMove(board,this,destinationPos,this.position));
                }
                else {                                  // PEICE PRESENT AT DESTINATION
                    final Piece pieceAtDestination = destinationTile.getPeice();
                    final Alliance allianceAtDestination = pieceAtDestination.getPeiceAlliance();
                    if(this.peiceAlliance!=allianceAtDestination){ //OPPONENT PEICE PRESENT AT DESTINATION this.peiceAlliance comes from superclass Piece
                        legalmoves.add(new Move.CapturingMove(board,this,
                                destinationPos,
                                this.position,pieceAtDestination));
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalmoves);
    }

    @Override
    public King movePiece(final Move move) {
        return new King(move.getDestinationCoordinate(),move.getPiece().getPeiceAlliance());
    }


    private static boolean isFirstColumnExclusion(int currentPosition, int coordinateMovement){
        return BoardUtil.FIRST_COLUMN[currentPosition]&&(coordinateMovement==-9||coordinateMovement==-1||
                coordinateMovement==7);
    }
    private static boolean isEighthColumnExclusion(int currentPosition, int coordinateMovement){
        return BoardUtil.EIGHTH_COLUMN[currentPosition]&&(coordinateMovement==-7||coordinateMovement==1||
                coordinateMovement==9);
    }
    @Override
    public String toString() {
        return PieceType.KING.toString();
    }
}
