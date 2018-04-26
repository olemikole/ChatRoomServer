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



    private ChatServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
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
                currentClient = new ClientSession(in, out, this,currentClientSocket);

                new Thread(currentClient).start();

                clientSessions.add(currentClient);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String message, ClientSession currentClient) {
        synchronized (clientSessions) {
            String stringToSent;
            stringToSent = currentClient.username + ": " + message;
            for (ClientSession clientSession : clientSessions) {
                if (clientSession != currentClient)
                    clientSession.sendMessage(stringToSent);
            }
        }
    }

    public void sendUpdate()
    {
        String stringToSent = "SERVER:Online Users "+getStringWithOnlineUsers();
        for (ClientSession clientSession : clientSessions) {
            clientSession.sendMessage(stringToSent);
        }
    }

    public void removeMyReference(ClientSession cl) {
        synchronized (clientSessions) {

            clientSessions.remove(clientSessions.indexOf(cl));
        }
    }



    public String getStringWithOnlineUsers() {
        // lager en String med alle usernames som er tilkoblet
        String onlineUsers = "";
        synchronized (clientSessions) {
            for (ClientSession clientSession : clientSessions) {
                onlineUsers += clientSession.username + " ";
            }
        }
        return onlineUsers;
    }
}