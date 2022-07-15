package com.milind.chess.board;

import java.util.HashMap;
import java.util.Map;

public class BoardUtil {

    public static final boolean[] FIRST_COLUMN=initColoumns(0);
    public static final boolean[] SECOND_COLUMN=initColoumns(1);
    public static final boolean[] SEVENTH_COLUMN=initColoumns(6);
    public static final boolean[] EIGHTH_COLUMN=initColoumns(7);

    public static final boolean[] FIRST_ROW = initRow(0);
    public static final boolean[] SECOND_ROW=initRow(8);
    public static final boolean[] THIRD_ROW = initRow(16);
    public static final boolean[] FOURTH_ROW = initRow(24);
    public static final boolean[] FIFTH_ROW = initRow(32);
    public static final boolean[] SIXTH_ROW = initRow(40);
    public static final boolean[] SEVENTH_ROW=initRow(48);
    public static final boolean[] EIGHTH_ROW = initRow(56);

    public static final String[] algebricNotations = initNotations();
    private static final Map<Integer,String> notations = getMap();


    private static boolean[] initColoumns(int coordinate){
        final boolean[] sameColumn = new boolean[64];
        do {
           sameColumn[coordinate]=true;
           coordinate+=8;
        }while (coordinate<64);
        return sameColumn;
    }

    private static boolean[] initRow(int rowStart) {
        final boolean[] sameRow = new  boolean[64];
        for (int j=0;j<64;j++){
            if(j<rowStart){
                sameRow[j]=false;
            }
            else if(j<rowStart+8){
                sameRow[j] =true;
            }
            else  {
                sameRow[j] = false;
            }
        }
        return sameRow;
    }

    private static String[] initNotations() {
        return new String[]{"h1","g1","f1","e1","d1","c1","b1","a1",
                            "h2","g2","f2","e2","d2","c2","b2","a2",
                            "h3","g3","f3","e3","d3","c3","b3","a3",
                            "h4","g4","f4","e4","d4","c4","b4","a4",
                            "h5","g5","f5","e5","d5","c5","b5","a5",
                            "h6","g6","f6","e6","d6","c6","b6","a6",
                            "h7","g7","f7","e7","d7","c7","b7","a7",
                            "h8","g8","f8","e8","d8","c8","b8","a8"};
    }
    private static Map<Integer, String> getMap() {
        final Map<Integer,String> notations = new HashMap<>();
        for(int i=0;i<64;i++){
            notations.put(i,algebricNotations[i]);
        }
        return notations;
    }


    public static String getSquare(final int tile){
            if(tile>=0&&tile<64)
                return notations.get(tile);
            else
                return "";
    }

    public static int getCoordinate(final String square){
        if(notations.containsValue(square)){
            for (int n : notations.keySet()){
                if(notations.get(n).equals(square)){
                    return n;
                }
            }
        }
        return -1;
    }


    private BoardUtil(){
        throw new RuntimeException("This class cannot be instantiated");
    }

    public static boolean isValidCoordinate(int coordinate){
        return coordinate>=0&&coordinate<64;
    }




}
