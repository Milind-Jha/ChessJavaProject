package com.milind.chess.peices;

import com.google.common.collect.ImmutableList;
import com.milind.chess.alliance.Alliance;
import com.milind.chess.board.Board;
import com.milind.chess.board.BoardUtil;
import com.milind.chess.board.ChessTile;
import com.milind.chess.board.Move;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece{


    private final static int[] KNIGHT_MOVES = {17,15,10,6,-6,-10,-15,-17};
    private final double initialPieceValue = 3.5;


    public Knight(int position, Alliance peiceAlliance) {
        super(position, peiceAlliance,PieceType.KNIGHT,true);
    }

    public Knight(int position, Alliance peiceAlliance,boolean isFirstMove) {
        super(position, peiceAlliance,PieceType.BISHOP,isFirstMove);
    }

    @Override
    public List<Move> calculateLegalMoves(Board board) {

        List<Move> legalmoves = new ArrayList<>();
        for (final int coordinateMovement: KNIGHT_MOVES ) {
            final int destinationCoordinate = this.position + coordinateMovement;
            if (BoardUtil.isValidCoordinate(destinationCoordinate)){

                if(isFirstColumnExclusion(this.position,coordinateMovement)||
                        isSecondColumnExclusion(this.position,coordinateMovement)||
                        isSeventhColumnExclusion(this.position,coordinateMovement)||
                        isEighthColumnExclusion(this.position,coordinateMovement)){
                    continue;
                }

                final ChessTile destinationTile = board.getTile(destinationCoordinate);
                if(!destinationTile.isTileOccupied()){  // NO PEICE PRESENT AT DESTINATION
                    legalmoves.add(new Move.NormalMove(board,this,destinationCoordinate,this.position));
                }
                else {                                  // PEICE PRESENT AT DESTINATION
                    final Piece pieceAtDestination = destinationTile.getPeice();
                    final Alliance allianceAtDestination = pieceAtDestination.getPeiceAlliance();
                    if(this.peiceAlliance!=allianceAtDestination){ //OPPONENT PEICE PRESENT AT DESTINATION this.peiceAlliance comes from superclass Piece
                        legalmoves.add(new Move.CapturingMove(board,this,
                                destinationCoordinate,
                                this.position,pieceAtDestination));
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalmoves);
    }

    @Override
    public Knight movePiece(final Move move) {
        return new Knight(move.getDestinationCoordinate(),move.getPiece().getPeiceAlliance());
    }

    private static boolean isFirstColumnExclusion(int currentPosition, int coordinateMovement){
        return BoardUtil.FIRST_COLUMN[currentPosition]&&(coordinateMovement==-17||coordinateMovement==-10||
                coordinateMovement==6||coordinateMovement==15);
    }
    private static boolean isSecondColumnExclusion(int currentPosition, int coordinateMovement){
        return BoardUtil.SECOND_COLUMN[currentPosition]&&(coordinateMovement==-10||coordinateMovement==6);
    }

    private static boolean isSeventhColumnExclusion(int currentPosition, int coordinateMovement){
        return BoardUtil.SEVENTH_COLUMN[currentPosition]&&(coordinateMovement==10||coordinateMovement==-6);
    }
    private static boolean isEighthColumnExclusion(int currentPosition, int coordinateMovement){
        return BoardUtil.EIGHTH_COLUMN[currentPosition]&&(coordinateMovement==17||coordinateMovement==10||
                coordinateMovement==-6||coordinateMovement==-15);
    }

    @Override
    public String toString() {
        return PieceType.KNIGHT.toString();
    }
}
