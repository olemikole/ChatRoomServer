package ChatRoomServer;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerMain {

    ServerSocket serverSocket;
    Socket currentClientSocket;
    ClientSession currentClient;
    BufferedReader br;
    BufferedReader in;
    PrintWriter out;

    public static ArrayList<ClientSession> clientSessions = new ArrayList<ClientSession>();

    private ServerMain(int port) {
        try {
            serverSocket = new ServerSocket(port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new ServerMain(61000).startServer();
        //dominik
    }

    private void startServer() {

        System.out.println("Server is ready");
        try {
            while (true) {

                currentClientSocket = serverSocket.accept();
                out = new PrintWriter(currentClientSocket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(currentClientSocket.getInputStream()));
                currentClient = new ClientSession(in, out, this);

                new Thread(currentClient).start();

                clientSessions.add(currentClient);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void Send(String message, ClientSession user) {// sends to all clients except sender
        synchronized (clientSessions) {
            String s;
            if (message.equals("/logout"))
                s = user.username + " has disconnected!";
            else
                s = user.username + ": " + message;
            for (ClientSession cl : clientSessions) if (cl != user) cl.Send(s);

        }

    }

    public void removeMyReference(ClientSession cl)// removes the reference to a session
    {
        synchronized (clientSessions) {
            clientSessions.remove(clientSessions.indexOf(cl));
        }

    }
}
