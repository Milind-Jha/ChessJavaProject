package com.milind.chess.board;

import com.milind.chess.alliance.Alliance;
import com.milind.chess.peices.Pawn;
import com.milind.chess.peices.Piece;
import com.milind.chess.peices.Queen;
import com.milind.chess.peices.Rook;

import java.util.Base64;

public abstract class Move {
    protected final Board board;
    protected final Piece piece;
    protected final int destinationCoordinate;
    protected final int currentCoordinate;
    protected final boolean isFirstMove;

    private Move(final Board board, final Piece piece,
        final int destinationCoordinate, final int currentCoordinate){
        this.board=board;
        this.piece=piece;
        this.currentCoordinate=currentCoordinate;
        this.destinationCoordinate=destinationCoordinate;
        isFirstMove =piece.isFirstMove();
    }

    private Move(final Board board, final int destinationCoordinate, final int currentCoordinate){
        this.board=board;
        this.piece=null;
        this.currentCoordinate=currentCoordinate;
        this.destinationCoordinate=destinationCoordinate;
        isFirstMove =false;
    }

//    @Override
//    public String toString() {
//        return piece.toString()+" -> "+destinationCoordinate;
//    }

    @Override
    public int hashCode() {
        int result =  this.destinationCoordinate;
        result = 31 * result + this.piece.hashCode();
        result = 31 * result + this.currentCoordinate;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(!(obj instanceof Move)){
            return false;
        }
        final Move move = (Move) obj;
        return getDestinationCoordinate() == move.getDestinationCoordinate() &&
               getPiece().equals(move.getPiece()) && getCurrentCoordinate() == move.getCurrentCoordinate();
    }

    public  int getDestinationCoordinate(){
        return  this.destinationCoordinate;
    }

    public  int getCurrentCoordinate(){
        return this.currentCoordinate;
    }

    public Piece getPiece(){
        return this.piece;
    }
    public Board getBoard(){
        return this.board;
    }

    public boolean isAttack(){
        return false;
    }

    public boolean isCastlingMove(){
        return false;
    }

    public Piece getAttackedPiece(){
        return null;
    }


    public Board execute() { //execute works on imaginary  future board
       final Board.Builder builder = new Board.Builder();

       for (final Piece piece:this.board.getCurrentPlayer().getActivePieces()){
            // TO DO hashCode() and equals()
           if(!this.piece.equals(piece)){
               builder.setPeice(piece);
           }
       }
       for (final Piece piece:this.board.getCurrentPlayer().getOpponent().getActivePieces()){
           builder.setPeice(piece);
       }
       builder.setPeice(this.piece.movePiece(this));
       builder.setMoveColor(this.board.getCurrentPlayer().getOpponent().getAlliance()); //DECIDES NEXT MOVE COLOR
       return builder.build();

    }

    public static class NormalMove extends Move{
       public NormalMove(final Board board,final Piece piece,
                   final int destinationCoordinate,
                   final int currentCoordinate) {
            super(board, piece, destinationCoordinate, currentCoordinate);
        }

        @Override
        public String toString() {
            return this.getPiece().getPieceType()+BoardUtil.getSquare(this.getDestinationCoordinate());
        }
    }

    public static abstract class CastleMove extends Move{
         final Rook castleRook;
         final int castleRookStart;
         final int castleRookDestination;
        public CastleMove(final Board board,final Piece piece,
                          final int destinationCoordinate,
                          final int currentCoordinate, final Rook castleRook,
                          final int castleRookStart, final int castleRookDestination) {
            super(board, piece, destinationCoordinate, currentCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }
        public Rook getCastleRook(){
            return this.castleRook;
        }
        @Override
        public boolean isCastlingMove(){
            return true;
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            for (final Piece piece: this.board.getCurrentPlayer().getActivePieces()){
                if(!this.piece.equals(piece) && !this.castleRook.equals(piece)){
                    builder.setPeice(piece);
                }
            }
            for(final Piece piece: this.board.getCurrentPlayer().getOpponent().getActivePieces()){
                builder.setPeice(piece);
            }
            builder.setPeice(this.piece.movePiece(this));
            builder.setPeice(new Rook(this.castleRookDestination,this.castleRook.getPeiceAlliance(),false));
            builder.setMoveColor(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public int hashCode() {
            int prime =31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDestination;
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return this==obj || obj instanceof CastleMove && super.equals((CastleMove)obj) &&
                    this.castleRook.equals(((CastleMove) obj).getCastleRook());
        }
    }

    public static class KingSideCastleMove extends CastleMove{
        public KingSideCastleMove(final Board board,final Piece piece,
                          final int destinationCoordinate,
                          final int currentCoordinate, final Rook castleRook,
                          final int castleRookStart, final int castleRookDestination) {
            super(board, piece, destinationCoordinate, currentCoordinate,castleRook,
                    castleRookStart,castleRookDestination);
        }
        @Override
        public String toString() {
            return "0-0";
        }

        @Override
        public boolean equals(Object obj) {
            return this==obj || obj instanceof KingSideCastleMove && super.equals(obj);
        }
    }

    public static class QueenSideCastleMove extends CastleMove{
        public QueenSideCastleMove(final Board board,final Piece piece,
                          final int destinationCoordinate,
                          final int currentCoordinate, final Rook castleRook,
                          final int castleRookStart, final int castleRookDestination) {
            super(board, piece, destinationCoordinate, currentCoordinate,castleRook,
                    castleRookStart,castleRookDestination);
        }
        @Override
        public String toString() {
            return "0-0-0";
        }
        @Override
        public boolean equals(Object obj) {
            return this==obj || obj instanceof QueenSideCastleMove && super.equals(obj);
        }
    }

    public static class PawnMove extends Move{
        public PawnMove(final Board board,final Piece piece,
                          final int destinationCoordinate,
                          final int currentCoordinate) {
            super(board, piece, destinationCoordinate, currentCoordinate);
        }

        @Override
        public String toString() {
            return BoardUtil.getSquare(destinationCoordinate);
        }

        @Override
        public boolean equals(Object obj) {
            return this==obj || obj instanceof PawnMove && super.equals(obj);
        }
    }

    public static class PawnJump extends Move{
        public PawnJump(final Board board,final Piece piece,
                        final int destinationCoordinate,
                        final int currentCoordinate) {
            super(board, piece, destinationCoordinate, currentCoordinate);
        }
        @Override
        public String toString() {
            return BoardUtil.getSquare(destinationCoordinate);
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            for (final Piece piece: this.board.getCurrentPlayer().getActivePieces()){
                if(!this.piece.equals(piece)){
                    builder.setPeice(piece);
                }
            }
            for(final Piece piece: this.board.getCurrentPlayer().getOpponent().getActivePieces()){
                builder.setPeice(piece);
            }
            final Pawn movedPawn = (Pawn) this.piece.movePiece(this);
            builder.setPeice(movedPawn);
            builder.setMoveColor(this.board.getCurrentPlayer().getOpponent().getAlliance());
            builder.setEnPassantPawn(movedPawn);

            return builder.build();
        }
    }

    public static class CapturingMove extends Move{
        final Piece attackedPeice;
        public CapturingMove(final Board board,
                      final Piece piece,
                      final int destinationCoordinate,
                      final int currentCoordinate,
                      final Piece attackedPeice) {
            super(board, piece, destinationCoordinate, currentCoordinate);
            this.attackedPeice=attackedPeice;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if(this == obj){
                return true;
            }
            if(!(obj instanceof CapturingMove)){
                return false;
            }
            final CapturingMove move = (CapturingMove) obj;
            return super.equals(move) && getAttackedPiece().equals(move.getAttackedPiece());
        }

//        @Override
//        public Board execute() {
//            return null;
//        }

        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public Piece getAttackedPiece() {
            return this.attackedPeice;
        }

        @Override
        public String toString() {
            return piece.getPieceType().toString() +BoardUtil.getSquare(destinationCoordinate);
        }
    }
    public static class PawnPromotion extends Move{

        private final Move promotingMove;
        private final Pawn promotedPawn;

        public PawnPromotion(Move promotingMove) {
            super(promotingMove.getBoard(), promotingMove.getPiece(), promotingMove.getDestinationCoordinate(),
                    promotingMove.getCurrentCoordinate());
            this.promotingMove = promotingMove;
            this.promotedPawn = (Pawn) promotingMove.getPiece();
        }

        @Override
        public Board execute() {
            final Board boardAfterPawnMove = this.promotingMove.execute(); // calling execute method of Move object
            final Board.Builder builder = new Board.Builder();
            for (Piece piece: boardAfterPawnMove.getCurrentPlayer().getActivePieces()){
                if(!this.promotedPawn.equals(piece)){
                    builder.setPeice(piece);
                }
            }
            for (final Piece opponentPiece : boardAfterPawnMove.getCurrentPlayer().getOpponent().getActivePieces()){
                builder.setPeice(opponentPiece);
            }
            builder.setPeice(this.promotedPawn.getPromotionDecision().movePiece(this));
            builder.setMoveColor(boardAfterPawnMove.getCurrentPlayer().getAlliance());
            return builder.build();
        }

        @Override
        public boolean isAttack() {
            return this.promotingMove.isAttack();
        }

        @Override
        public Piece getAttackedPiece() {
            return this.promotingMove.getAttackedPiece();
        }

        @Override
        public String toString() {
            if(promotingMove instanceof PawnCapturingMove){
                return BoardUtil.getSquare(promotingMove.getCurrentCoordinate()).substring(0,1)+"x"
                        +BoardUtil.getSquare(promotingMove.getDestinationCoordinate())+"=Q";
            }
            else{
                return BoardUtil.getSquare(promotingMove.getDestinationCoordinate())+"=Q";
            }
        }

        @Override
        public int hashCode() {
            return promotingMove.hashCode() + (31 * promotedPawn.hashCode());
        }

        @Override
        public boolean equals(Object obj) {
            return this==obj || obj instanceof PawnPromotion &&super.equals(obj);
        }
    }
    public static class PawnCapturingMove extends CapturingMove{
        public PawnCapturingMove(final Board board,
                             final Piece piece,
                             final int destinationCoordinate,
                             final int currentCoordinate,
                             final Piece attackedPeice) {
            super(board, piece, destinationCoordinate, currentCoordinate,attackedPeice);
        }

        @Override
        public boolean equals(Object obj) {
            return this==obj || obj instanceof PawnCapturingMove && super.equals(obj);
        }

        @Override
        public String toString() {
            return BoardUtil.getSquare(currentCoordinate).substring(0,1)+"x"+BoardUtil.getSquare(destinationCoordinate);
        }
    }
    public static class PawnEnPaessantMove extends CapturingMove{
        public PawnEnPaessantMove(final Board board,
                                 final Piece piece,
                                 final int destinationCoordinate,
                                 final int currentCoordinate,
                                 final Piece attackedPeice) {
            super(board, piece, destinationCoordinate, currentCoordinate,attackedPeice);
        }

        @Override
        public boolean equals(Object obj) {
            return this==obj || obj instanceof PawnEnPaessantMove && super.equals(obj);
        }

        @Override
        public String toString() {
            return BoardUtil.getSquare(currentCoordinate).substring(0,1)+"x"+BoardUtil.getSquare(destinationCoordinate);
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            for(final Piece piece: this.board.getCurrentPlayer().getActivePieces()){
                if(!this.piece.equals(piece)){  // Player Pieces not involved in the enpaessant move
                    builder.setPeice(piece);
                }
            }
            for (final Piece opponentPiece : this.board.getCurrentPlayer().getOpponent().getActivePieces()){
                if(!this.getAttackedPiece().equals(opponentPiece)){ // Opponent Pieces not involved in the enpaessant move
                    builder.setPeice(opponentPiece);
                }
            }
            builder.setPeice(this.piece.movePiece(this));
            builder.setMoveColor(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    public static class IllegalMove extends Move{
        public IllegalMove() {
            super(null, -1, -1);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Illegal Move");
        }
    }

    public static class MoveFactory{

        private MoveFactory(){
            throw new RuntimeException("not allowed");
        }

        public static Move createMove(final Board board,final int destinationCoordinate,
                                      final int currentCoordinate){
            for(final  Move move : board.getAllLegalMoves()){
                if(move.getCurrentCoordinate() == currentCoordinate &&
                        move.getDestinationCoordinate() == destinationCoordinate){
                    return move;
                }
            }
            return new IllegalMove();
        }

    }
}
