package com.milind.chess.peices;

import com.milind.chess.alliance.Alliance;
import com.milind.chess.board.Board;
import com.milind.chess.board.Move;

import java.util.Collection;
import java.util.List;

public abstract class Piece {

    protected final int position;
    protected final Alliance peiceAlliance;
    protected boolean isFirstMove;
    protected final PieceType pieceType;
    private final int cacheHash;

    Piece(final int position,final Alliance peiceAlliance,PieceType pieceType,boolean isFirstMove){
        this.peiceAlliance=peiceAlliance;
        this.position=position;
        this.isFirstMove=isFirstMove;
        this.pieceType = pieceType;
        this.cacheHash = generateHash();
    }

    protected  int generateHash(){
        return this.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj){          // same reference
            return true;
        }
        if(!(obj instanceof Piece)){
            return false;
        }
        final Piece otherPiece = (Piece) obj;
        return this.peiceAlliance == otherPiece.getPeiceAlliance() && this.pieceType == otherPiece.getPieceType()
                && this.position == otherPiece.getPiecePosition() && this.isFirstMove == otherPiece.isFirstMove();
    }

    @Override
    public int hashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + peiceAlliance.hashCode();
        result = 31 * result + position;
        result = 31 * result +  (isFirstMove?1:0);
        return result;
    }


    public boolean isFirstMove(){
        return isFirstMove;
    }

    public Alliance getPeiceAlliance(){
        return this.peiceAlliance;
    }

    public int getPiecePosition(){
        return this.position;
    }

    public PieceType getPieceType(){
        return pieceType;
    }

    public int getPieceValue(){
        return this.pieceType.getValue();
    }


    public abstract Collection<Move> calculateLegalMoves(final Board board);
    public abstract Piece movePiece(Move move);

    public enum PieceType {

        PAWN("P"){
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public int getValue() {
                return 100;
            }
        },
        KNIGHT("N") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public int getValue() {
                return 300;
            }
        },
        ROOK("R") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }

            @Override
            public int getValue() {
                return 500;
            }
        },
        BISHOP("B") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public int getValue() {
                return 325;
            }
        },
        KING("K") {
            @Override
            public boolean isKing() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public int getValue() {
                return Integer.MAX_VALUE;
            }
        },
        QUEEN("Q") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public int getValue() {
                return 900;
            }
        };

        private String pieceName;

        PieceType(final String pieceName){   // AN ENUM CONSTRUCTOR CAN'T BE CALLED EXPLICITLY
            this.pieceName = pieceName;      // RATHER IS EXECUTED SEPARELTY EVERY TIME WE USE A CONSTANT
                                             // OF THIS ENUM.
        }

        @Override
        public String toString() {       // AN ENUM  CAN OVERRIDE toString only
            return this.pieceName;
        }

        public abstract boolean isKing();
        public abstract boolean isRook();
        public abstract int getValue();
    }

}
