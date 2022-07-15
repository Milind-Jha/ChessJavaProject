package com.milind.chess;

import com.milind.chess.board.Board;
import com.milind.chess.ui.Table;

import java.util.*;

  class ChessDriver {
      public static void main(String args[]) {

        Board board = Board.createStartingPosition();
        System.out.println("\t WHITE");
        System.out.println(board);
        System.out.println("\t BLACK");
        Table.getObject().show();

      }


  }


