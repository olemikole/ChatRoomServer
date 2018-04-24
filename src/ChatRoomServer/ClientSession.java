package ChatRoomServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSession implements Runnable {
	ChatServer server;
	BufferedReader in;
	PrintWriter out;
	String username;
    Socket socket;

	ClientSession(BufferedReader _in, PrintWriter _out, ChatServer _server,Socket _socket) {
		this.server = _server;
		this.in = _in;
		this.out = _out;
		this.socket=_socket;
	}

	@Override
	public void run() {
		String inputMessage;
		boolean running = true;

		try {
			System.out.println("New client has arrived");

			username = in.readLine();
			System.out.println(username + " is ready to chat");
			server.send(null, null);
			while (running) {
				inputMessage = in.readLine();
				System.out.println(username + ": " + inputMessage);
				server.send(inputMessage, this);
			}
		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			server.removeMyReference(this);
			if(socket!=null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
			System.out.println(username + " disconnected");
			server.send(null, null);
			System.out.println(ChatServer.clientSessions.size() + " people on the server");
		}
	}

	public void sendMessage(String message) {
		out.println(message);
		out.flush();
	}


}