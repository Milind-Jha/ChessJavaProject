package com.milind.chess.board;

import com.google.common.collect.ImmutableMap;
import com.milind.chess.peices.Piece;

import java.util.HashMap;
import java.util.Map;

public abstract class ChessTile {
    protected final int coordinate;
    private static final Map<Integer,EmptyTile> EMPTY_TILE_MAP = createAllEmptyTiles();

    private static Map<Integer, EmptyTile> createAllEmptyTiles() {
        final Map<Integer,EmptyTile> emptyTileMap = new HashMap<>();
        for (int i=0;i<64;i++){
            emptyTileMap.put(i,new EmptyTile(i));
        }
        return ImmutableMap.copyOf(emptyTileMap);
    }

    private ChessTile(final int coordinate){
        this.coordinate = coordinate;
    }
    public static ChessTile createTile(final int coordinate,final Piece chessPeice){  // FACTORY METHOD
        return chessPeice==null ? EMPTY_TILE_MAP.get(coordinate) : new OccupiedTile(coordinate,chessPeice);
    }

    public abstract boolean isTileOccupied();
    public abstract Piece getPeice();
    public int getCoordinate(){
        return this.coordinate;
    }

    public static final class EmptyTile  extends ChessTile {

        private EmptyTile(final int coordinate) {
            super(coordinate);
        }

        @Override
        public boolean isTileOccupied() {
            return false;
        }

        @Override
        public Piece getPeice() {
            return null;
        }

        @Override
        public String toString() {
            return "|_|";
        }
    }
    public static final class OccupiedTile extends ChessTile{

       private final Piece chessPiece;

        private OccupiedTile(final int coordinate,final Piece chessPiece) {
            super(coordinate);
            this.chessPiece = chessPiece;
        }

        @Override
        public boolean isTileOccupied() {
            return true;
        }

        @Override
        public Piece getPeice() {
            return this.chessPiece;
        }

        @Override
        public String toString() {
            return getPeice().getPeiceAlliance().isBlack()? "|" + getPeice().toString().toLowerCase()+"|":
                    "|"+getPeice().toString().toUpperCase()+"|";
        }
    }
}
