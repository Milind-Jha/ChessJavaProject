package com.milind.chess.ui;

import com.google.common.collect.Lists;
import com.milind.chess.alliance.Alliance;
import com.milind.chess.board.Board;
import com.milind.chess.board.BoardUtil;
import com.milind.chess.board.ChessTile;
import com.milind.chess.board.Move;
import com.milind.chess.peices.Piece;
import com.milind.chess.player.MoveTransition;
import com.milind.chess.player.Player;
import com.milind.chess.player.ai.MiniMax;
import com.milind.chess.player.ai.MoveStrategy;
import com.sun.javafx.iio.ios.IosDescriptor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table extends Observable {

    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private final GameHistoryPanel gameHistoryPanel;
    private final CapturedPiecesPanel capturedPiecesPanel;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;

    private  Board chessBoard;
    private ChessTile sourceTile;
    private ChessTile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;
    private Move computerMove;

    private static Table INSTANCE;
    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600,600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400,400);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(40,40);
    private final static  String defaultPieceIconPath ="art/plain/";

    static {
        INSTANCE = new Table();
    }

    public static Table getObject(){
        return INSTANCE;
    }


    private Table(){
        this.boardDirection=BoardDirection.NORMAL;
        this.chessBoard = Board.createStartingPosition();
        this.gameFrame = new JFrame("MilindChess");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar jMenuBar =  populateMenu();
        this.gameFrame.setJMenuBar(jMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.gameHistoryPanel =new GameHistoryPanel();
        this.capturedPiecesPanel = new CapturedPiecesPanel();
        this.boardPanel = new BoardPanel();
        boardPanel.drawBoard(chessBoard);               // REVERSE BOARD FOR WHITE IN BOTTOM
        this.moveLog=new MoveLog();
        this.addObserver(new TableWatcher());
        this.gameSetup=new GameSetup(this.gameFrame,true);
        this.gameFrame.add(this.capturedPiecesPanel,BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel,BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel,BorderLayout.EAST);
        this.gameFrame.setVisible(true);

    }

    public void show(){
        Table.getObject().getMoveLog().clear();
        Table.getObject().getGameHistoryPanel().redo(chessBoard,Table.getObject().getMoveLog());
        Table.getObject().getCapturedPiecesPanel().redo(Table.getObject().getMoveLog());
        Table.getObject().getBoardPanel().drawBoard(Table.getObject().getGameBoard());
    }

    private JMenuBar populateMenu() {
        final JMenuBar jMenuBar= new JMenuBar();
        jMenuBar.add(createFileMenu());
        jMenuBar.add(createPreferenceMenu());
        jMenuBar.add(createOptionsMenu());
        return jMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem openFromPGN = new JMenuItem("Load PGN File");
        openFromPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Opening PGN file...");
            }
        });
        fileMenu.add(openFromPGN);
        return fileMenu;
    }

    private JMenu createPreferenceMenu(){
        final JMenu prefrenceMenu = new JMenu("Preference");
        final JMenuItem flipBoard = new JMenuItem("Flip Board");
        flipBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        prefrenceMenu.add(flipBoard);
        return prefrenceMenu;
    }

    private JMenu createOptionsMenu(){
        final JMenu optionsMenu = new JMenu("Options");
        JMenuItem jMenuItem = new JMenuItem("Setup Game");
        jMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.getObject().getGameSetUp().promptUser();
                Table.getObject().setUpUpdate(Table.getObject().getGameSetUp());
            }
        });
        optionsMenu.add(jMenuItem);
        return optionsMenu;
    }

    private static class TableWatcher implements Observer{

        @Override
        public void update(final Observable o, final Object arg) {
            if(Table.getObject().getGameSetUp().isAIPlayer(Table.getObject().getGameBoard().getCurrentPlayer())&&
                !Table.getObject().getGameBoard().getCurrentPlayer().isCheckMate() &&
                !Table.getObject().getGameBoard().getCurrentPlayer().isStaleMate()){
                // create a AI thread
                final AIThinkTank aiThinkTank = new AIThinkTank();
                aiThinkTank.execute();
            }

            if(Table.getObject().getGameBoard().getCurrentPlayer().isCheckMate()){
//                final Alliance winner = Table.getObject().getGameBoard().getCurrentPlayer().getAlliance()
//                        == Alliance.WHITE ? Alliance.BLACK : Alliance.WHITE;
//
//                JOptionPane.showMessageDialog(Table.getObject().getBoardPanel(),
//                        "Game Over: Player " + Table.getObject().getGameBoard().getCurrentPlayer().
//                                getOpponent() + " wins!", "Game Over",
//                        JOptionPane.INFORMATION_MESSAGE);
                System.out.println("CheckMate");
            }
            if(Table.getObject().getGameBoard().getCurrentPlayer().isStaleMate()){
//                JOptionPane.showMessageDialog(Table.getObject().getBoardPanel(),
//                        "Game Draw  Player " + Table.getObject().getGameBoard().getCurrentPlayer()
//                                + "is in StaleMate", "Match Draw",
//                        JOptionPane.INFORMATION_MESSAGE);
                System.out.println("Draw");
            }

        }
    }

    public void upDateGameBoard(final Board board){
        this.chessBoard = board;
    }

    public void upDateComputerMove(final Move move){
        this.computerMove = move;
    }

    private MoveLog getMoveLog(){
        return  this.moveLog;
    }

    private GameHistoryPanel getGameHistoryPanel(){
        return this.gameHistoryPanel;
    }

    private CapturedPiecesPanel getCapturedPiecesPanel(){
        return this.capturedPiecesPanel;
    }

    private void setUpUpdate(final GameSetup gameSetUp) {
        setChanged();
        notifyObservers(gameSetUp);
    }

    private Board getGameBoard(){
        return this.chessBoard;
    }

    private BoardPanel getBoardPanel(){
        return this.boardPanel;
    }

    private void moveMadeUpDate(final PlayerType playerType){
        setChanged();
        notifyObservers(playerType);
    }

    private static class AIThinkTank extends SwingWorker<Move,String> {

        private AIThinkTank(){

        }

        @Override
        protected Move doInBackground() throws Exception {
            final MoveStrategy moveStrategy = new MiniMax(4);
            final Move bestMove = moveStrategy.execute(Table.getObject().getGameBoard());
            return bestMove;
        }

        @Override
        public void done() {
            try {
                Move bestMove = get();
                Table.getObject().upDateComputerMove(bestMove);
                Table.getObject().upDateGameBoard(Table.getObject().getGameBoard().getCurrentPlayer()
                        .makeMove(bestMove).getTransitionBoard());
                Table.getObject().getMoveLog().addMove(bestMove);
                Table.getObject().getGameHistoryPanel().redo(Table.getObject().getGameBoard(),
                        Table.getObject().getMoveLog());
                Table.getObject().getCapturedPiecesPanel().redo(Table.getObject().getMoveLog());
                Table.getObject().getBoardPanel().drawBoard(Table.getObject().getGameBoard());
                Table.getObject().moveMadeUpDate(PlayerType.COMPUTER);
            } catch (InterruptedException e) {
                System.out.println("interepted exception");
            } catch (ExecutionException e) {
                System.out.println("execution exception");
                e.printStackTrace();
            }
        }
    }


    private GameSetup getGameSetUp() {
        return this.gameSetup;
    }


    public enum BoardDirection{
        NORMAL{
            @Override
            public List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }

            @Override
            public BoardDirection opposite() {
                return FLIPPED;
            }
        },FLIPPED{
            @Override
            public List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            public BoardDirection opposite() {
                return NORMAL;
            }
        };

        public abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        public abstract BoardDirection opposite();
    }

    private class BoardPanel extends JPanel{
        final List<TilePanel> boardTiles;
        BoardPanel(){
            super(new GridLayout(8,8));
            this.boardTiles = new ArrayList<>();
            for(int i=0;i<64;i++){
                final TilePanel tilePanel= new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                super.add(tilePanel);           //   this.add(tilePanel); add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION); //super.setPreferredSize(BOARD_DIMENSION); this.setPreferredSize(BOARD_DIMENSION);
            validate();
        }
        public void drawBoard(final Board board){
            removeAll();
            for(final TilePanel tilePanel: boardDirection.traverse(boardTiles)){
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    public static class MoveLog{

        private final List<Move> moves;

        MoveLog(){
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves(){
            return this.moves;
        }

        public void addMove(Move move){
            this.moves.add(move);
        }

        public int size(){
            return this.moves.size();
        }

        public void clear(){
            this.moves.clear();
        }

        public boolean removeMove(final Move move){
            if(this.moves.contains(move)) {
                int index=this.moves.indexOf(move);
                this.moves.remove(index);
                return true;
            }
            return false;
        }

        public Move removeMove(final int index){
            return this.removeMove(index);
        }
    }

    public enum PlayerType{
        COMPUTER,
        HUMAN;
    }


    private class TilePanel extends JPanel{

        private final int tileID;

        TilePanel(final BoardPanel boardPanel,final int tileID){
            super(new GridBagLayout());
            this.tileID=tileID;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieces(chessBoard);
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if(isRightMouseButton(e)){
                       sourceTile = null;
                       destinationTile = null;
                       humanMovedPiece = null;
                    }
                    else if(isLeftMouseButton(e)){
                        if(sourceTile==null){   //FIRST CLICK
                            System.out.println("first Click: "+BoardUtil.getSquare(tileID));
                            sourceTile = chessBoard.getTile(tileID);
                            humanMovedPiece = sourceTile.getPeice();
                            if(humanMovedPiece==null){
                                sourceTile = null;
                            }
                        }
                        else{                  // SECOND CLICK

                            destinationTile = chessBoard.getTile(tileID);
                            System.out.println("next Click: "+BoardUtil.getSquare(tileID));
                            if(destinationTile!=sourceTile){
                                final Move move = Move.MoveFactory.createMove(chessBoard,destinationTile.getCoordinate(),
                                        sourceTile.getCoordinate());
                                final MoveTransition moveTransition = chessBoard.getCurrentPlayer().makeMove(move);
                                if(moveTransition.getMoveStatus().isDone()){
                                    chessBoard = moveTransition.getTransitionBoard();
                                    moveLog.addMove(move);
                                    //TODO
                                }
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        SwingUtilities.invokeLater(()-> {
                                gameHistoryPanel.redo(chessBoard,moveLog);
                                capturedPiecesPanel.redo(moveLog);
                                if(gameSetup.isAIPlayer(chessBoard.getCurrentPlayer())){
                                    Table.getObject().moveMadeUpDate(PlayerType.HUMAN);
                                }
                                boardPanel.drawBoard(chessBoard);

                            });
                        }
                }

                @Override
                public void mousePressed(final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });
            validate();
        }

        public void drawTile(final Board board){
            assignTileColor();
            assignTilePieces(board);
            validate();
            repaint();
        }

        private void assignTileColor() {
            if(BoardUtil.FIRST_ROW[this.tileID] || BoardUtil.THIRD_ROW[this.tileID] ||
                BoardUtil.FIFTH_ROW[this.tileID] || BoardUtil.SEVENTH_ROW[this.tileID]){
                setBackground(this.tileID%2==0?Color.WHITE:Color.darkGray);
            }
            else if(BoardUtil.SECOND_ROW[this.tileID] || BoardUtil.FOURTH_ROW[this.tileID] ||
                    BoardUtil.SIXTH_ROW[this.tileID] || BoardUtil.EIGHTH_ROW[this.tileID]){
                setBackground(this.tileID%2!=0?Color.WHITE:Color.darkGray);
            }
        }

        private void assignTilePieces(final Board board){
            this.removeAll();
            if(board.getTile(this.tileID).isTileOccupied()){
                try {
                    final BufferedImage image = ImageIO.read(new File(defaultPieceIconPath +
                            board.getTile(this.tileID).getPeice().getPeiceAlliance().toString().substring(0,1)+
                            board.getTile(this.tileID).getPeice().toString()+".gif"));
                    add(new JLabel(new ImageIcon(image)));
                }
                catch (IOException e){
                    e.printStackTrace();
                }

            }
        }

    }
}
