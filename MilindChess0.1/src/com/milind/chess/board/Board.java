package com.milind.chess.board;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.milind.chess.alliance.Alliance;
import com.milind.chess.peices.*;
import com.milind.chess.player.BlackPlayer;
import com.milind.chess.player.Player;
import com.milind.chess.player.WhitePlayer;

import java.util.*;

public class Board{

    private final List<ChessTile> gameBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;
    private final Pawn enPaessantPawn;
    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;

    private Board(final Builder builder){
        this.gameBoard= createGameBoard(builder);
        this.whitePieces = calculateActivePieces(this.gameBoard,Alliance.WHITE);
        this.blackPieces = calculateActivePieces(this.gameBoard,Alliance.BLACK);
        this.enPaessantPawn = builder.getEnPassantPawn();
        final Collection<Move> whiteMovesPossible = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackMovesPossible = calculateLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteMovesPossible, blackMovesPossible);
        this.blackPlayer = new BlackPlayer(this, whiteMovesPossible, blackMovesPossible);
        this.currentPlayer = builder.nextMoveColor.choosePlayer(this.whitePlayer,this.blackPlayer);
    }

    @Override
    public String toString(){
        final StringBuilder builder = new StringBuilder();
        for (int i=0;i< 64;i++){
            final String tileText = this.gameBoard.get(i).toString();
            builder.append(String.format("%3s",tileText));
            if((i+1)%8==0){
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public Player getCurrentPlayer(){
        return this.currentPlayer;
    }

    public Player whitePlayer(){
        return this.whitePlayer;
    }

    public Player blackPlayer(){
        return this.blackPlayer;
    }

    public Collection<Piece> getBlackPieces(){
        return this.blackPieces;
    }

    public Collection<Piece> getWhitePieces(){
        return this.whitePieces;
    }

    public Pawn getEnPassantPawn(){
        return this.enPaessantPawn;
    }

    private Collection<Move> calculateLegalMoves(final Collection<Piece> pieces) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final Piece piece: pieces){
            legalMoves.addAll(piece.calculateLegalMoves(this));
        }
        return ImmutableList.copyOf(legalMoves);
    }

    private Collection<Piece> calculateActivePieces(final List<ChessTile> gameBoard,final Alliance alliance) {
        final List<Piece> activePices = new ArrayList<>();
        for (ChessTile chessTile: gameBoard){
            if(chessTile.isTileOccupied()){
                final Piece piece = chessTile.getPeice();
                if(piece.getPeiceAlliance()==alliance){
                    activePices.add(piece);
                }
            }
        }
        return ImmutableList.copyOf(activePices);
    }
    private static List<ChessTile> createGameBoard(final Builder builder){
        final ChessTile[] chessTiles = new ChessTile[64];
        for (int i=0;i<64;i++){
            chessTiles[i] = ChessTile.createTile(i,builder.boardConfig.get(i));
        }
        return ImmutableList.copyOf(chessTiles);
    }

    public static Board createStartingPosition(){
        final Builder builder = new Builder();

        //WHITE PEICES
        builder.setPeice(new Rook(0,Alliance.WHITE));           //h1
        builder.setPeice(new Knight(1,Alliance.WHITE));         //g1
        builder.setPeice(new Bishop(2,Alliance.WHITE));         //f1
        builder.setPeice(new King(3,Alliance.WHITE));           //e1
        builder.setPeice(new Queen(4,Alliance.WHITE));          //d1
        builder.setPeice(new Bishop(5,Alliance.WHITE));
        builder.setPeice(new Knight(6,Alliance.WHITE));
        builder.setPeice(new Rook(7,Alliance.WHITE));
        builder.setPeice(new Pawn(8,Alliance.WHITE));
        builder.setPeice(new Pawn(9,Alliance.WHITE));
        builder.setPeice(new Pawn(10,Alliance.WHITE));
        builder.setPeice(new Pawn(11,Alliance.WHITE));
        builder.setPeice(new Pawn(12,Alliance.WHITE));
        builder.setPeice(new Pawn(13,Alliance.WHITE));
        builder.setPeice(new Pawn(14,Alliance.WHITE));
        builder.setPeice(new Pawn(15,Alliance.WHITE));

        //Black PEICES
        builder.setPeice(new Pawn(48,Alliance.BLACK));
        builder.setPeice(new Pawn(49,Alliance.BLACK));
        builder.setPeice(new Pawn(50,Alliance.BLACK));
        builder.setPeice(new Pawn(51,Alliance.BLACK));
        builder.setPeice(new Pawn(52,Alliance.BLACK));
        builder.setPeice(new Pawn(53,Alliance.BLACK));
        builder.setPeice(new Pawn(54,Alliance.BLACK));
        builder.setPeice(new Pawn(55,Alliance.BLACK));
        builder.setPeice(new Rook(56,Alliance.BLACK));          //h8
        builder.setPeice(new Knight(57,Alliance.BLACK));        //g8
        builder.setPeice(new Bishop(58,Alliance.BLACK));        //f8
        builder.setPeice(new King(59,Alliance.BLACK));          //e8
        builder.setPeice(new Queen(60,Alliance.BLACK));         //d8
        builder.setPeice(new Bishop(61,Alliance.BLACK));
        builder.setPeice(new Knight(62,Alliance.BLACK));
        builder.setPeice(new Rook(63,Alliance.BLACK));

        return builder.build();
    }



    public ChessTile getTile(final int coordinate){
        return gameBoard.get(coordinate);
    }

    public Iterable<Move> getAllLegalMoves(){
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePlayer.getLegalMoves(),
                                              this.blackPlayer.getLegalMoves()));
    }


    public static class Builder{
        Map<Integer, Piece> boardConfig;
        Alliance nextMoveColor;
        Pawn enPaessantPawn;
//        Move transitionMove;

        public Builder(){
            this.boardConfig = new HashMap<>();
            this.nextMoveColor = Alliance.WHITE;
        }
        public Builder setPeice(final Piece piece){
            this.boardConfig.put(piece.getPiecePosition(),piece);
            return this;
        }
        public Builder setMoveColor(final Alliance nextMoveColor){
            this.nextMoveColor=nextMoveColor;
            return this;
        }

        public Board build(){
            return new Board(this);
        }

//        public Builder setMoveTransition(final Move transitionMove) {
//            this.transitionMove = transitionMove;
//            return this;
//        }

        public void setEnPassantPawn(Pawn movedPawn) {
            this.enPaessantPawn= movedPawn;
        }

        public Pawn getEnPassantPawn(){
            return this.enPaessantPawn;
        }
    }
}
