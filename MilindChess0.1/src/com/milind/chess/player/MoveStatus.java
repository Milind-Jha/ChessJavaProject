package com.milind.chess.player;

public enum MoveStatus {

    ILLEGAL_MOVE{
        @Override
        public boolean isDone() {
            return false;
        }
    },

    DONE{
        @Override
        public boolean isDone() {
            return true;
        }
    },
    MOVE_CREATE_CHECK{
        @Override
        public boolean isDone() {
            return false;
        }
    }
    ;


   public abstract boolean isDone();

}
