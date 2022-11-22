package application.pane;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectServer {
    private final static ConnectServer connect  = new ConnectServer();
    private static Socket socket;

    private ConnectServer() {}
    public static void connectionToServer() {
        try {
            socket = new Socket("127.0.0.1", 9020);
        }
        catch (Exception e){
            new Announcement().display("\nService Disconnected!\n ");
            Clear();
        }
    }

    public static ConnectServer getConnect()
    {
        return connect;
    }

    public DataInputStream getDataInputStream() throws IOException
    {
        return new DataInputStream(socket.getInputStream());
    }

    public DataOutputStream getDataOutputStream() throws IOException
    {
        return new DataOutputStream(socket.getOutputStream());
    }

    public static Socket getSocket()
    {
        return socket;
    }

    private static void Clear()
    {
        TeConnect.close(socket);
        System.exit(0);
    }
}
