package com.milind.chess.player;

import com.google.common.collect.ImmutableList;
import com.milind.chess.alliance.Alliance;
import com.milind.chess.board.Board;
import com.milind.chess.board.ChessTile;
import com.milind.chess.board.Move;
import com.milind.chess.peices.Piece;
import com.milind.chess.peices.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WhitePlayer extends Player {

    public WhitePlayer(final Board board,final Collection<Move> whitelegalMoves, final Collection<Move> blacklegalMoves) {
        super(board,whitelegalMoves,blacklegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,final Collection<Move> opponontLegals) {
        final List<Move> kingCastles = new ArrayList<>();
        if(this.playerKing.isFirstMove() && !this.isCheck()){
            if(!this.board.getTile(1).isTileOccupied() &&
               !this.board.getTile(2).isTileOccupied()){
               final ChessTile rookTile = this.board.getTile(0);
                    if(rookTile.isTileOccupied() && rookTile.getPeice().isFirstMove() &&
                       rookTile.getPeice().getPieceType().isRook()){
                        if(Player.calculateAttacksOnTile(1,opponontLegals).isEmpty() &&
                           Player.calculateAttacksOnTile(2,opponontLegals).isEmpty() ) {
                              kingCastles.add(new Move.KingSideCastleMove(this.board,this.playerKing,1,
                                      3,(Rook) rookTile.getPeice(),rookTile.getCoordinate(),
                                      2));
                        }
                    }
            }
            if(!this.board.getTile(6).isTileOccupied() &&
               !this.board.getTile(5).isTileOccupied() &&
               !this.board.getTile(4).isTileOccupied()){
                final ChessTile rookTile = this.board.getTile(7);
                    if(rookTile.isTileOccupied() && rookTile.getPeice().isFirstMove() &&
                       rookTile.getPeice().getPieceType().isRook()){
                        if(Player.calculateAttacksOnTile(6,opponontLegals).isEmpty() &&
                           Player.calculateAttacksOnTile(5,opponontLegals).isEmpty() &&
                           Player.calculateAttacksOnTile(4,opponontLegals).isEmpty()) {
                              kingCastles.add(new Move.QueenSideCastleMove(this.board,this.playerKing,5,
                                      3,(Rook) rookTile.getPeice(),rookTile.getCoordinate(),4));
                        }
                    }
            }
        }
        return ImmutableList.copyOf(kingCastles);
    }
}
