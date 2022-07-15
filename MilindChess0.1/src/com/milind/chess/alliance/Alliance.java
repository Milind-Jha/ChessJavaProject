package com.milind.chess.alliance;

import com.milind.chess.board.BoardUtil;
import com.milind.chess.player.BlackPlayer;
import com.milind.chess.player.Player;
import com.milind.chess.player.WhitePlayer;

public enum Alliance {

    WHITE{
        @Override
        public int getDirection(){
            return 1;
        }

        @Override
        public int getOppositeDirection() {
            return -1;
        }

        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return BoardUtil.EIGHTH_ROW[position];
        }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer,final BlackPlayer blackPlayer) {
            return whitePlayer;
        }
    },
    BLACK {
        @Override
        public int getDirection() {
            return -1;
        }

        @Override
        public int getOppositeDirection() {
            return 1;
        }

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isBlack() {
            return true;
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return BoardUtil.FIRST_ROW[position];
        }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer,final BlackPlayer blackPlayer) {
            return blackPlayer;
        }
    };

    public abstract int getDirection();
    public abstract int getOppositeDirection();
    public abstract boolean isWhite();
    public abstract boolean isBlack();
    public abstract boolean isPawnPromotionSquare(int position);
    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
}
