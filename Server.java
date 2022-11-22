package application.server;
import application.pane.constance;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server extends Application implements constance {
    public static void main(String[] args)
    {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        File file = new File("close.txt");
        Writer writer = null;
        Random random=new Random();
        try {
            writer = new FileWriter(file,false);
            int op=random.nextInt(2);
            String s = "1";
            if(op==0)
                s="0";
            writer.write(s);
        }  catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(writer !=null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        TextArea text = new TextArea();
        text.setEditable(false);

        Scene scene = new Scene(new Pane(text), 450, 250);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Tic-tac-toe-Server");
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                File file = new File("close.txt");
                Writer writer = null;
                try {
                    writer = new FileWriter(file,false);
                    String s = "3";
                    writer.write(s);
                }  catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if(writer !=null){
                        try {
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                System.exit(0);
            }
        });

        new Thread(() -> {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(9020);
                while (true) {
                    Platform.runLater(() -> {
                        text.appendText("Server has started!\n" + "Waiting for player connecting.......\n");
                    });
                    Socket Player1 = serverSocket.accept();
                    Platform.runLater(() -> {
                        text.appendText("Player 1 connect!\n");
                        text.appendText("The IP address of Player 1: "+ Player1.getInetAddress().getHostAddress() + "\n");
                    });
                    DataOutputStream toUser1 = new DataOutputStream(Player1.getOutputStream());
                    toUser1.writeInt(player1);
                    Socket Player2 = serverSocket.accept();
                    Platform.runLater(() -> {
                        text.appendText("Player 2 connect!\n");
                        text.appendText("The IP address of Player 2: "+Player2.getInetAddress().getHostAddress() + "\n");
                    });
                    DataOutputStream toUser2 = new DataOutputStream(Player2.getOutputStream());
                    toUser2.writeInt(player2);
                    new Thread(new HandleProcess(Player1, Player2, serverSocket)).start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
