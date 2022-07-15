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

public class BlackPlayer extends Player {
    public BlackPlayer(final Board board,final Collection<Move> whitelegalMoves,
                       final Collection<Move> blacklegalMoves) {
                       super(board,blacklegalMoves,whitelegalMoves);

    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.whitePlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,final Collection<Move> opponentLegals) {
        final List<Move> kingCastles = new ArrayList<>();
        if(this.playerKing.isFirstMove() && !this.isCheck()){
            if(!this.board.getTile(57).isTileOccupied() &&
                    !this.board.getTile(58).isTileOccupied()){
                final ChessTile rookTile = this.board.getTile(56);
                if(rookTile.isTileOccupied() && rookTile.getPeice().isFirstMove() &&
                        rookTile.getPeice().getPieceType().isRook()){
                    if(Player.calculateAttacksOnTile(57,opponentLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(58,opponentLegals).isEmpty() ) {
                          kingCastles.add(new Move.KingSideCastleMove(this.board, this.playerKing,
                                         57,59,(Rook) rookTile.getPeice(),
                                          rookTile.getCoordinate(),58));
                    }
                }
            }
            if(!this.board.getTile(60).isTileOccupied() &&
               !this.board.getTile(61).isTileOccupied() &&
               !this.board.getTile(62).isTileOccupied()){
                final ChessTile rookTile = this.board.getTile(63);
                if(rookTile.isTileOccupied() && rookTile.getPeice().isFirstMove() &&
                        rookTile.getPeice().getPieceType().isRook()){
                    if(Player.calculateAttacksOnTile(60,opponentLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(61,opponentLegals).isEmpty() &&
                            Player.calculateAttacksOnTile(62,opponentLegals).isEmpty()) {
                          kingCastles.add(new Move.QueenSideCastleMove(this.board,this.playerKing,61,
                                  59,(Rook) rookTile.getPeice(),rookTile.getCoordinate(),60));
                    }
                }
            }
        }
        return ImmutableList.copyOf(kingCastles);
    }
}
