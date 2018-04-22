package ChatRoomServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {

    ServerSocket serverSocket;
    Socket currentClientSocket;
    ClientSession currentClient;
    BufferedReader in;
    PrintWriter out;

    public static ArrayList<ClientSession> clientSessions = new ArrayList<>();

    public final static String LOGOUT_COMMAND = "/logout";

    private ChatServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
        }
         catch (IOException e) {
             e.printStackTrace();
         }
    }

    public static void main(String[] args) {
        new ChatServer(61000).startServer();
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

    public void send(String message, ClientSession user) {
        synchronized (clientSessions) {
            String stringToSent;
            if (message.equals(ChatServer.LOGOUT_COMMAND))
                stringToSent = user.username + " has disconnected!";
            else
                stringToSent = user.username + ": " + message;
            for (ClientSession cl : clientSessions) {
                if (cl != user) cl.sendMessage(stringToSent);
            }
        }
    }

    public void removeMyReference(ClientSession cl) {
        synchronized (clientSessions) {
            clientSessions.remove(clientSessions.indexOf(cl));
        }
    }
}