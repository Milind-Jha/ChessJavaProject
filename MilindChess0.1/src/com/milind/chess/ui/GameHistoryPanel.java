package com.milind.chess.ui;

import com.milind.chess.board.Board;
import com.milind.chess.board.Move;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameHistoryPanel extends JPanel {

    private final DataModel model;
    private final JScrollPane scrollPane;

    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(100,200);

    GameHistoryPanel(){
        this.setLayout(new BorderLayout());
        this.model= new DataModel();
        final JTable table = new JTable(model);
        table.setRowHeight(15);
        this.scrollPane= new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scrollPane,BorderLayout.CENTER);
        this.setVisible(true);
    }

    void redo(final Board board, final Table.MoveLog moveLogHistory){
        int currentRow =0;
        this.model.clear();
        for(Move move:moveLogHistory.getMoves()){
            final String moveText = move.toString();
            if(move.getPiece().getPeiceAlliance().isWhite()){
                this.model.setValueAt(moveText,currentRow,0);
            }
            else if(move.getPiece().getPeiceAlliance().isBlack()){
                this.model.setValueAt(moveText,currentRow,1);
                currentRow++;
            }
        }
        if(moveLogHistory.size()>0){
            final Move lastMove = moveLogHistory.getMoves().get(moveLogHistory.size()-1);
            final String moveText = lastMove.toString();

            if(lastMove.getPiece().getPeiceAlliance().isWhite()){
                this.model.setValueAt(moveText + caculateCheckAndCheckMateHash(board),
                        currentRow,0);
                System.out.println(moveText + caculateCheckAndCheckMateHash(board));
            }
            else if(lastMove.getPiece().getPeiceAlliance().isBlack()){
                this.model.setValueAt(moveText + caculateCheckAndCheckMateHash(board),currentRow-1,
                        1);
            }
        }

        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());

    }

    private String caculateCheckAndCheckMateHash(Board board) {
        if(board.getCurrentPlayer().isCheckMate()){
            return "#";
        }
        else if(board.getCurrentPlayer().isCheck()){
            return "+";
        }
        return "";
    }

    private static class DataModel extends DefaultTableModel {
        private final List<Row> values;
        private final String[] NAMES = {"White","Black"};

        DataModel(){
            this.values = new ArrayList<>();
        }

        public void clear(){
            this.values.clear();
        }

        @Override
        public int getRowCount() {
            if(this.values==null){
                return 0;
            }
            return this.values.size();
        }

        @Override
        public int getColumnCount() {
            return NAMES.length;
        }

        @Override
        public Object getValueAt(int row, int column) {
            final Row currentRow = this.values.get(row);
            if(column==0){
                return currentRow.getWhiteMove();
            }
            else if(column==1){
                return currentRow.getBlackMove();
            }
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            final Row currnetRow;
            if(this.values.size()<=row){
                currnetRow = new Row();
                this.values.add(currnetRow);
            }
            else {
                currnetRow= this.values.get(row);
            }
            if(column==0){
                currnetRow.setWhiteMove( (String) aValue);
                fireTableRowsInserted(row,row);
            }
            else if(column ==1){
                currnetRow.setBlackMove( (String) aValue);
                fireTableCellUpdated(row,column);
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Move.class;
        }

        @Override
        public String getColumnName(int column) {
            return NAMES[column];
        }
    }

    private static class Row{
        private String whiteMove;
        private String blackMove;

        Row(){

        }

        public String getWhiteMove(){
            return this.whiteMove;
        }

        public String getBlackMove(){
            return this.blackMove;
        }

        public void setWhiteMove(final String whiteMove) {
            this.whiteMove = whiteMove;
        }

        public void setBlackMove(final String blackMove) {
            this.blackMove = blackMove;
        }
    }
}
