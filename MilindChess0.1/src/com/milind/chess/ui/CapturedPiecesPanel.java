package com.milind.chess.ui;

import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.milind.chess.board.Move;
import com.milind.chess.peices.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CapturedPiecesPanel extends JPanel {

    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    private static final Color PANEL_COLOR = Color.decode("0xFDF5E1");
    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(50,100);

    private final JPanel southPanel;
    private final JPanel northPanel;

    public CapturedPiecesPanel(){
        super(new BorderLayout());
        setBackground(Color.decode("0xFDF5E6"));
        setBorder(PANEL_BORDER);
        this.southPanel = new JPanel(new GridLayout(8,2));
        this.northPanel = new JPanel(new GridLayout(8,2));
        this.southPanel.setBackground(PANEL_COLOR);
        this.northPanel.setBackground(PANEL_COLOR);
        this.add(this.northPanel,BorderLayout.NORTH);
        this.add(this.southPanel,BorderLayout.SOUTH);
        setPreferredSize(TAKEN_PIECES_DIMENSION);
    }

    public void redo(final Table.MoveLog moveLog){
        this.southPanel.removeAll();
        this.northPanel.removeAll();

        List<Piece> whiteCapturedPieces = new ArrayList<>();
        List<Piece> blackCapturedPieces = new ArrayList<>();

        for(final Move move:moveLog.getMoves()){
            if(move.isAttack()){
                final Piece capturedPiece = move.getAttackedPiece();
                if(capturedPiece.getPeiceAlliance().isWhite()){
                    whiteCapturedPieces.add(capturedPiece);
                }
                else if(capturedPiece.getPeiceAlliance().isBlack()){
                    blackCapturedPieces.add(capturedPiece);
                }
                else {
                    throw new RuntimeException("Not Possible");
                }
            }
        }
        Collections.sort(whiteCapturedPieces, new Comparator<Piece>() {
            @Override
            public int compare(Piece o1, Piece o2) {
                return Ints.compare(o1.getPieceValue(),o2.getPieceValue());
            }
        });

        Collections.sort(blackCapturedPieces, new Comparator<Piece>() {
            @Override
            public int compare(Piece o1, Piece o2) {
                return Ints.compare(o1.getPieceValue(),o2.getPieceValue());
            }
        });

        for (final Piece capPiece: whiteCapturedPieces){
            try {
                final BufferedImage image = ImageIO.read(new File("art/plain/"+
                        capPiece.getPeiceAlliance().toString().substring(0,1)+capPiece.toString()+".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel();
                this.southPanel.add(imageLabel);
            }
            catch (final IOException e){
                e.printStackTrace();
            }
        }


        for (final Piece capPiece: blackCapturedPieces){
            try {
                final BufferedImage image = ImageIO.read(new File("art/plain/"+
                        capPiece.getPeiceAlliance().toString().substring(0,1)
                        +capPiece.toString().substring(0,1)+".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel();
                this.northPanel.add(imageLabel);
            }
            catch (final IOException e){
                e.printStackTrace();
            }
        }
        validate();
    }




}
