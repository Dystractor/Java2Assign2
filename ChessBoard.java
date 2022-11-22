package application.pane;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.Random;

public class ChessBoard extends Pane{
    private final static ChessBoard chessBoard = new ChessBoard();
    private ChessBoard() {
        this.display();
    }

    public static ChessBoard getChessBoard()
    {
        return chessBoard;
    }
    private int[][] chessXY = new int[300][2];
    private int op;
    public void display() {
        int chessX = 50;
        for(int i =0; i < 3 ; i++){
            int chessY = 50;
            for(int j =0; j < 3; j++){
                chessXY[i * 3 + j][0] = chessX;
                chessXY[i * 3 + j][1] = chessY;
                chessY += 100;
            }
            chessX += 100;
        }
        Line l1 = new Line(-400, 100, 400, 100);
        l1.setStroke(Color.GREEN);
        Line l2 = new Line(-400, 200, 400, 200);
        l2.setStroke(Color.GREEN);
        Line l3 = new Line(100, -400, 100, 400);
        l3.setStroke(Color.GREEN);
        Line l4 = new Line(200, -400, 200, 400);
        l4.setStroke(Color.GREEN);
        getChildren().add(l1);
        getChildren().add(l2);
        getChildren().add(l3);
        getChildren().add(l4);
    }
    public void setOp(){
        Random random=new Random();
        this.op=random.nextInt(2);
    }
    public int getOp(){
        return this.op;
    }
    public int getChessX(int row, int col)
    {
        return chessXY[row * 3 + col][0];
    }

    public int getChessY(int row, int col)
    {
        return chessXY[row * 3 + col][1];
    }
}
