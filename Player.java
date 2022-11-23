package application.pane;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.shape.Line;
import javafx.stage.WindowEvent;
import java.io.*;
import java.util.Random;

public class Player extends Application implements constance{
  private static ChessBoard chessBoard = ChessBoard.getChessBoard();
  Pane pane = new Pane();
  private boolean waiting = true;
  private static int[][] chess = new int[3][3];
  private Button bottomBegin = new Button("Start Game");
  private Button bottomExit = new Button("Exit Game");
  private Button bottomBack = new Button("Exit");
  private boolean continueToPlay = true;
  public static double x;
  public static double y;
  private int rowNow;
  private int colNow;
  private boolean stop = true;
  private int myChess = 0;
  private int current_data;
  private int current_player = 0;
  private boolean myTurn = false;
  private int op;
  private DataInputStream fromServer;
  private DataOutputStream toServer;
  @Override
  public void start(Stage primaryStage) throws Exception {
    Pane pane1 = new Pane();
    Label label = new Label("\n                  Tic-tac-toe\n   ");
    label.setFont(Font.font("",20));
    VBox vBox1 = new VBox(40);
    vBox1.getChildren().add(label);
    vBox1.getChildren().add(new HBox(new Label("                                 ") , bottomBegin));
    vBox1.getChildren().add(new HBox(new Label("                                  ") , bottomExit));
    pane1.getChildren().add(vBox1);
    Scene scene1 = new Scene(pane1,300,300);
    primaryStage.setResizable(false);
    primaryStage.setTitle("Tic-tac-toe-client");
    primaryStage.setScene(scene1);
    primaryStage.show();
    new Thread(() -> {
      try {
        while (true) {
          x = primaryStage.getX();
          y = primaryStage.getY();
          Thread.sleep(100);
        }
      }
      catch (Exception ignored) {
      }

    }).start();
    op=checkFirst();
    pane.getChildren().add(chessBoard);
    bottomBegin.setOnAction(e -> {
      ConnectServer.connectionToServer();
      initializeChess();
      connectToServer();
      scene1.setRoot(pane);
    });
    bottomExit.setOnAction(event -> {
      System.exit(0);
    });
    bottomBack.setOnAction(e ->{
      continueToPlay = false;
      stop = false;
      release();
      scene1.setRoot(pane);
    });
    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent event) {
                System.exit(0);
            }
    });
  }


  private void sendXY() throws IOException {
    if(checkClose()) {
      new Announcement().display("\nService Disconnected!\n ");
      System.exit(0);
    }
    toServer.writeInt(1);
    toServer.writeInt(rowNow);
    toServer.writeInt(colNow);
  }

  private void initializeChess() {
    if(checkClose()) {
      new Announcement().display("\nService Disconnected!\n ");
      System.exit(0);
    }
    chessBoard.getChildren().clear();
    chessBoard.display();

    for(int i =0; i < chess.length; i++) {
      for(int j = 0; j < chess[0].length; j++) {
        chess[i][j] = 0;
      }
    }
    continueToPlay = true;
    current_player = 0;
    myChess = 0;
    stop = true;
    myTurn = false;
    waiting = true;
  }

  private void receiveInfo() throws Exception {
    if(checkClose()){
      new Announcement().display("\nService Disconnected!\n ");
      System.exit(0);
    }
    String s = "\nCongratulations, You win!\n";
    String s1 = "\nYou lose! You are the loser!\n";
    String s2 = "\nDraw!\n";
    waitingForFeedback();

    if(!continueToPlay){
      return;
    }
    if (current_data == player1_win) {
      continueToPlay = false;
      waiting = false;
      toServer.writeInt(8);
      if(op==0) {
        if (myChess == 1) {
          Platform.runLater(() -> {
            new Announcement().display(s);
          });
        } else if (myChess == 2) {
          Platform.runLater(() -> {
            new Announcement().display(s1);
          });
        }
      }
      else {
        if (myChess == 2) {
          Platform.runLater(() -> {
            new Announcement().display(s);
          });
        } else if (myChess == 1) {
           Platform.runLater(() -> {
             new Announcement().display(s1);
           });
        }
      }
    }
    else if (current_data == player2_win) {
      waiting = false;
      continueToPlay = false;
      if(op==0) {
        if (myChess == 2) {
          Platform.runLater(() -> {
            new Announcement().display(s);
          });
        } else if (myChess == 1) {
           Platform.runLater(() -> {
             new Announcement().display(s1);
           });
        }
      }
      else {
        if (myChess == 1) {
          Platform.runLater(() -> {
            new Announcement().display(s);
          });
        } else if (myChess == 2) {
           Platform.runLater(() -> {
             new Announcement().display(s1);
           });
        }
      }
    }
    else if (current_data == draw) {
      waiting = false;
      continueToPlay = false;
      Platform.runLater(() -> {
        new Announcement().display(s2);
      });
      initializeChess();
    }
    else {
      myTurn = true;
    }
  }
  private int checkFirst() {
    File file = new File("close.txt");
    FileInputStream is = null;
    String tmp="";
    try {
      is = new FileInputStream(file);
      int i;
      while((i = is.read())!=-1) {
        tmp+=(char) i;
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    if (tmp.equals("0")) {
        return 0;
    }
    else {
      return 1;
    }
  }
  private boolean checkClose() {
    File file = new File("close.txt");
    FileInputStream is = null;
    String tmp="";
    try {
      is = new FileInputStream(file);
      int i;
      while((i = is.read())!=-1){
        tmp+=(char) i;
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return tmp.equals("3");
  }


  private void connectToServer() {
    if(checkClose()){
      new Announcement().display("\nService Disconnected!\n ");
      System.exit(0);
    }
    try {
      fromServer = ConnectServer.getConnect().getDataInputStream();
      toServer = ConnectServer.getConnect().getDataOutputStream();
    }
    catch (IOException e) {
      new Announcement().display("\nService Disconnected!\n ");
      System.exit(0);
    }

    new Thread(() -> {
      try {
        if(checkClose()) {
          new Announcement().display("\nService Disconnected!\n ");
          System.exit(0);
        }
        current_player = fromServer.readInt();
        if(op==0) {
          if (current_player == player1) {
            myChess = 1;
            Platform.runLater(() -> {
              new Announcement().display("\nYou are the circle!\nPlease wait the opponent connect!\n ");
            });
            fromServer.readInt();
            Platform.runLater(() -> {
                new Announcement().display("\nPlayer 2 connect successfully!\n ");
            });
            getData();
            myTurn = true;
            Platform.runLater(() -> {
                new Announcement().display("\nYou are the first, please take moves!\n ");
            });
          } else if (current_player == player2) {
            myChess = 2;
            Platform.runLater(() -> {
              new Announcement().display("\nYou are the X!\n ");
            });
            getData();
            Platform.runLater(() -> {
              new Announcement().display("\nYou play latter, wait till opponent moves!\n ");
            });
          }
        }
        else{
          if (current_player == player1) {
            myChess = 2;
            Platform.runLater(() -> {
              new Announcement().display("\nYou are the X!\n Please wait the opponent connect!\n ");
            });
            fromServer.readInt();
            Platform.runLater(() -> {
              new Announcement().display("\nPlayer 2 Connected!\n ");
            });
            getData();
            myTurn=true;
            Platform.runLater(() -> {
              new Announcement().display("\nYou are the first, please take moves!\n ");
            });
          } else if (current_player == player2) {
            myChess = 1;
            Platform.runLater(() -> {
              new Announcement().display("You are playing circle!\n ");
            });
            getData();
            Platform.runLater(() -> {
              new Announcement().display("\nYou play latter, wait till opponent moves!\n ");
            });
          }
        }
        while (continueToPlay) {
          if(checkClose()){
            new Announcement().display("\nService Disconnected!\n ");
            System.exit(0);
          }
          if(op==0) {
            if (current_player == player1) {
              PlayTheChess(current_player);
              waitingForThePlayer();
              sendXY();
              receiveInfo();
            } else if (current_player == player2) {
              receiveInfo();
              PlayTheChess(current_player);
              waitingForThePlayer();
              sendXY();
            }
          }
          else{
            if (current_player == player1) {
              PlayTheChess(player2);
              waitingForThePlayer();
              sendXY();
              receiveInfo();
            } else if (current_player == player2) {
              receiveInfo();
              PlayTheChess(player1);
              waitingForThePlayer();
              sendXY();
            }
          }
        }
      }
      catch (Exception e) {
        release();
      }
    }).start();
  }

    private void waitingForThePlayer() throws InterruptedException {
      if (checkClose()) {
        new Announcement().display("\nService Disconnected!\n ");
        System.exit(0);
      }
      while (waiting) {
        Thread.sleep(100);
      }
      waiting = true;
    }
    private void PlayTheChess(int player) {
      if(myTurn){
        Platform.runLater(() -> {
          new Announcement().display("\nYou move!\n ");
        });
      }

      if(checkClose()) {
        new Announcement().display("\nService Disconnected!\n ");
        System.exit(0);
      }
      chessBoard.setOnMouseClicked(e1 -> {
        if (myTurn) {
          play:
          for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
              double distance = Math.sqrt(
                Math.pow((e1.getSceneX() - chessBoard.getChessX(i, j)), 2)+
                Math.pow((e1.getSceneY() - chessBoard.getChessY(i, j)), 2));
              if (distance < 40 && chess[i][j] == 0) {
                if (player == player1) { // circle
                  Circle circle = new Circle();
                  circle.setCenterX(chessBoard.getChessX(i,j));
                  circle.setCenterY(chessBoard.getChessY(i,j));
                  circle.setRadius(30);
                  circle.setStroke(Color.DEEPPINK);
                  circle.setFill(Color.TRANSPARENT);
                  chessBoard.getChildren().add(circle);
                  chess[i][j] = 1;
                }
                else if(player == player2) { // line
                  Line line_a = new Line();
                  Line line_b = new Line();
                  line_a.setStartX(chessBoard.getChessX(i,j)-35);
                  line_a.setStartY(chessBoard.getChessY(i,j)-35);
                  line_a.setEndX(chessBoard.getChessX(i,j)+35);
                  line_a.setEndY(chessBoard.getChessY(i,j)+35);
                  line_a.setStroke(Color.BLUE);
                  line_b.setStartX(chessBoard.getChessX(i,j)+35);
                  line_b.setStartY(chessBoard.getChessY(i,j)-35);
                  line_b.setEndX(chessBoard.getChessX(i,j)-35);
                  line_b.setEndY(chessBoard.getChessY(i,j)+35);
                  line_b.setStroke(Color.BLUE);
                  chessBoard.getChildren().add(line_a);
                  chessBoard.getChildren().add(line_b);
                  chess[i][j] = 2;
                }
                rowNow = i;colNow = j;
                myTurn = false;waiting = false;
                break play;
              }
          }
        }
      }
    });
  }
  private void opponentPlayChess(int row, int col) {
    if(checkClose()) {
      new Announcement().display("\nService Disconnected!\n ");
      System.exit(0);
    }
    Platform.runLater(() -> {
      if (myChess == 1) {
        Line line_a = new Line();
        Line line_b = new Line();
        line_a.setStartX(chessBoard.getChessX(row,col)-35);
        line_a.setStartY(chessBoard.getChessY(row,col)-35);
        line_a.setEndX(chessBoard.getChessX(row,col)+35);
        line_a.setEndY(chessBoard.getChessY(row,col)+35);
        line_a.setStroke(Color.BLUE);

        line_b.setStartX(chessBoard.getChessX(row,col)+35);
        line_b.setStartY(chessBoard.getChessY(row,col)-35);
        line_b.setEndX(chessBoard.getChessX(row,col)-35);
        line_b.setEndY(chessBoard.getChessY(row,col)+35);
        line_b.setStroke(Color.BLUE);
        chessBoard.getChildren().add(line_a);
        chessBoard.getChildren().add(line_b);
        chess[row][col] = 2;
      }
      else {
        Circle circle = new Circle();
        circle.setCenterX(chessBoard.getChessX(row,col));
        circle.setCenterY(chessBoard.getChessY(row,col));
        circle.setRadius(30);
        circle.setStroke(Color.DEEPPINK);
        circle.setFill(Color.TRANSPARENT);
        chessBoard.getChildren().add(circle);
        chess[row][col] = 1;
      }
    });
  }
  private void receiveXY() throws IOException {
    if(checkClose()){
      new Announcement().display("\nService Disconnected!\n ");
      System.exit(0);
    }
    int row = fromServer.readInt();
    int col = fromServer.readInt();
    opponentPlayChess(row, col);
  }
  private void getData() {
    if(checkClose()) {
      new Announcement().display("\nService Disconnected!\n ");
      System.exit(0);
    }
    new Thread(()-> {
      while (continueToPlay) {
        int option;
        try {
          option = fromServer.readInt();
          if (option == 1) {
            current_data = fromServer.readInt();
            stop = false;
            if (current_data == player1_win) {
              receiveXY();
              break;
            }
            else if (current_data == player2_win) {
              receiveXY();
              break;
            }
            else if (current_data == draw) {
              if (myChess == 2) {
                receiveXY();
              }
              break;
            } else {
              receiveXY();
            }
          }
          else if (option == 15) {
            if (continueToPlay) {
              Platform.runLater(() -> {
                new Announcement().display("\nYour opponent has been left!\nYou win!");
              });
              myTurn = false;
              continueToPlay = false;
            }
            if (stop) {
              stop = false;
            }
            if (waiting) {
              waiting = false;
            }
            break;
          }
        }
        catch (IOException e) {
          continueToPlay = false;
          release();
          Platform.runLater(() -> {
            new Announcement().display("Server Disconnected!");
            System.exit(0);
          });
        }
      }
    }).start();
  }
  private void waitingForFeedback() throws InterruptedException {
    if(checkClose()) {
      new Announcement().display("\nService Disconnected!\n ");
      System.exit(0);
    }
    while(stop) {
      Thread.sleep(100);
    }
    stop = true;
  }


  private void release() {
    continueToPlay = false;
    TeConnect.close(ConnectServer.getSocket(), toServer, fromServer);
  }

  public static void main(String[] args) {
    launch(args);
  }
}
