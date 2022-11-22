package application.server;
import application.pane.constance;
import application.pane.TeConnect;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

class HandleProcess implements Runnable, constance {
    private int row, col;
    private boolean waiting = true;
    private boolean exit = false;
    private Socket Client1;
    private Socket Client2;
    private ServerSocket server;
    private CopyOnWriteArrayList<Socket> sockets = new CopyOnWriteArrayList<Socket>();
    private int[][] board = new int[3][3];
    private DataOutputStream toPlayer1;
    private DataOutputStream toPlayer2;

    public HandleProcess(Socket client1, Socket client2, ServerSocket server) {
        this.Client1 = client1;
        this.Client2 = client2;
        this.server = server;
        sockets.add(client1);
        sockets.add(client2);
        try {
            toPlayer1 = new DataOutputStream(this.Client1.getOutputStream());
            toPlayer2 = new DataOutputStream(this.Client2.getOutputStream());
        }

        catch (IOException e){
            release();
        }
        new Thread(new process(client1,client2)).start();
        new Thread(new process(client2,client1)).start();

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = 0;
            }
        }
    }

    @Override
    public void run() {
        try {
            toPlayer1.writeInt(0);
        } catch (Exception e) {
            release();
        }
        while (!exit) {
            try {
                waitingForPlayer();
                board[row][col] = 1;
                if (Check() == 1) {
                    toPlayer1.writeInt(1);
                    toPlayer1.writeInt(player1_win);
                    toPlayer2.writeInt(1);
                    toPlayer2.writeInt(player1_win);
                    sendMove(toPlayer2, row, col);
                    exit = true;
                    break;
                } else if (isDraw()) {
                    toPlayer1.writeInt(1);
                    toPlayer1.writeInt(draw);
                    toPlayer2.writeInt(1);
                    toPlayer2.writeInt(draw);
                    sendMove(toPlayer2, row, col);
                    exit = true;
                    break;
                } else {
                    toPlayer2.writeInt(1);
                    toPlayer2.writeInt(Continue);
                    sendMove(toPlayer2, row, col);
                }

                waitingForPlayer();
                board[row][col] = 2;
                if (Check() == 2) {
                    toPlayer1.writeInt(1);
                    toPlayer1.writeInt(player2_win);
                    toPlayer2.writeInt(1);
                    toPlayer2.writeInt(player2_win);
                    sendMove(toPlayer1, row, col);
                    exit = true;
                    break;
                } else {
                    toPlayer1.writeInt(1);
                    toPlayer1.writeInt(Continue);
                    sendMove(toPlayer1, row, col);
                }
            }
            catch (Exception e) {
                release();
            }
        }
    }

    private void waitingForPlayer() throws InterruptedException{
        while(waiting){
            Thread.sleep(100);
        }
        waiting = true;
    }

    private int Check() {
        int winner=3;
        if(board[0][0]>0 && board[0][0]==board[0][1] && board[0][1]==board[0][2])
            winner=board[0][0];
        if(board[1][0]>0 && board[1][0]==board[1][1] && board[1][1]==board[1][2])
            winner=board[1][0];
        if(board[2][0]>0 && board[2][0]==board[2][1] && board[2][1]==board[2][2])
            winner=board[2][0];
        if(board[0][0]>0 && board[0][0]==board[1][0] && board[1][0]==board[2][0])
            winner=board[0][0];
        if(board[0][1]>0 && board[0][1]==board[1][1] && board[1][1]==board[2][1])
            winner=board[0][1];
        if(board[0][2]>0 && board[0][2]==board[1][2] && board[1][2]==board[2][2])
            winner=board[0][2];
        if(board[0][0]>0 && board[0][0]==board[1][1] && board[1][1]==board[2][2])
            winner=board[0][0];
        if(board[0][2]>0 && board[0][2]==board[1][1] && board[1][1]==board[2][0])
            winner=board[0][2];
        return winner;
    }
    private void sendMove(DataOutputStream out, int row, int col) throws IOException {
        out.writeInt(row);
        out.writeInt(col);
    }

    private boolean isDraw() {
        for (int[] ints : board) {
            for (int j = 0; j < board[0].length; j++) {
                if (ints[j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void release() {
        exit = true;
        TeConnect.close(Client1, Client2, toPlayer1, toPlayer2);
    }
    class process implements Runnable {
        private int option;
        private Socket user1;
        private Socket user2;
        private boolean exit;
        private DataInputStream fromUser1;
        private DataOutputStream toUser2;
        public process(Socket user1, Socket user2) {
            this.user1 = user1;
            this.user2 = user2;
            try {
                fromUser1 = new DataInputStream(this.user1.getInputStream());
                toUser2 = new DataOutputStream(this.user2.getOutputStream());
            } catch (IOException e) {
                release();
            }
        }
        @Override
        public void run() {
            while (!exit) {
                try {
                    option = fromUser1.readInt();
                    if (option == 1) {
                        row = fromUser1.readInt();
                        col = fromUser1.readInt();
                        waiting = false;
                    }
                } catch (Exception e) {
                    sockets.remove(user1);
                    waiting = false;
                    try {
                        for (Socket socket : sockets) {
                            if (socket == user2)
                                toUser2.writeInt(15);
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    release();
                }
            }
        }
        private void release() {
            exit = true;
            TeConnect.close(user1, user2, fromUser1, toUser2);
        }
    }
}

