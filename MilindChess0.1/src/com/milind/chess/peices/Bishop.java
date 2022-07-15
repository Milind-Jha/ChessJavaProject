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

public class Bishop extends Piece{

    private static final int[] MOVEMENT_DIRECTION={9,7,-7,-9};

    public Bishop(int position, Alliance peiceAlliance) {
        super(position, peiceAlliance,PieceType.BISHOP,true);
    }

    public Bishop(int position, Alliance peiceAlliance,boolean isFirstMove) {
        super(position, peiceAlliance,PieceType.BISHOP,isFirstMove);
    }


    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        List<Move> legalmoves = new ArrayList<>();
        for (int movementOffset:MOVEMENT_DIRECTION){
            int currentPos = this.position;

            while (BoardUtil.isValidCoordinate(currentPos)){
                if(isFirstColumnExclusion(this.position,currentPos)||
                        isEighthColumnExclusion(this.position,currentPos)){
                    break;                                          //MOVE NOT POSSIBLE DUE TO BiSHOP POSITION
                }
                currentPos+=movementOffset;
                if (BoardUtil.isValidCoordinate(currentPos)){       // VALID SQUARE
                    final ChessTile destinationTile = board.getTile(currentPos);
                    if(!destinationTile.isTileOccupied()){          // NO PEICE PRESENT AT DESTINATION
                        legalmoves.add(new Move.NormalMove(board,this,currentPos,this.position));
                    }
                    else {                                           // PEICE PRESENT AT DESTINATION
                        final Piece pieceAtDestination = destinationTile.getPeice();
                        final Alliance allianceAtDestination = pieceAtDestination.getPeiceAlliance();
                        if(this.peiceAlliance!=allianceAtDestination){ //OPPONENT PEICE PRESENT AT DESTINATION this.peiceAlliance comes from superclass Piece
                            legalmoves.add(new Move.CapturingMove(board,this,
                                    currentPos,
                                    this.position,pieceAtDestination));
                        }
                        break;                                          // PATH BLOCKED
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalmoves) ;
    }

    @Override
    public Bishop movePiece(final Move move) {
        return new Bishop(move.getDestinationCoordinate(),move.getPiece().getPeiceAlliance());
    }

    private static boolean isFirstColumnExclusion(int currentPosition, int coordinateMovement){
        return BoardUtil.FIRST_COLUMN[currentPosition]&&(coordinateMovement==-9||coordinateMovement==7);
    }
    private static boolean isEighthColumnExclusion(int currentPosition, int coordinateMovement){
        return BoardUtil.EIGHTH_COLUMN[currentPosition]&&(coordinateMovement==9||coordinateMovement==-7);
    }

    @Override
    public String toString() {
        return PieceType.BISHOP.toString();
    }
}
